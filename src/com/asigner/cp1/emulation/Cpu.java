package com.asigner.cp1.emulation;


import sun.plugin2.main.client.DisconnectedExecutionContext;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class Cpu {

    private static final Logger logger = Logger.getLogger(Cpu.class.getName());

    // Bits in PSW
    public static final int CY_BIT = 7;
    public static final int AC_BIT = 6;
    public static final int F0_BIT = 5;
    public static final int BS_BIT = 4;

    // Masks
    public static final int CY_MASK = 1 << CY_BIT;
    public static final int AC_MASK = 1 << AC_BIT;
//    public static final int F0_MASK = 0x20;
    public static final int BS_MASK = 1 << BS_BIT;
//    public static final int SP_MASK = 0x07;

    public static final int REGISTER_BANK_0_BASE = 0;
    public static final int REGISTER_BANK_1_BASE = 24;

    // Interrupt pins, flipflops, additional flags
    private boolean TF; // Timer Flag
    private boolean notINT;
    private boolean T0;
    private boolean T1;
    private boolean F1;

    private int T;
    private int A;
    private int PC;
    private int PSW;
    private int DBF;
    private boolean externalInterruptsEnabled;
    private boolean tcntInterruptsEnabled;
    private boolean counterRunning; // Whether counter is bound to T1 (STRT CNT)
    private boolean timerRunning; // Whether counter is bound to clock (STRT T)
    private long cyclesUntilCount; // Number of cycles until we need to increment the count (if counter is bound to clock)
    private boolean inInterrupt; // True iff handling an interrupt. Reset by RETR

    private byte[] rom = new byte[2048];
    private byte[] ram = new byte[128];

    public Cpu() {
        reset();
    }

    public void reset() {
        TF = false;
        notINT = true;
        T0 = false;
        T1 = false;
        F1 = false;

        // T is not affected by reset()
        A = 0;
        PC = 0;
        PSW = 0xf; // bit 3 is always readRom as "1"
        DBF = 0; // memory bank 0
        externalInterruptsEnabled = false;
        tcntInterruptsEnabled = false;
        counterRunning = false;
        timerRunning = false;
        inInterrupt = false;
        Arrays.fill(ram, (byte)0);
    }

    //
    // Write to pins
    //

    public void writeT1(boolean newVal) {
        boolean oldT1 = T1;
        T1 = newVal;
        if (counterRunning && oldT1 && !T1) {
            // high -> low: count
            incCounter();
        }
    }

    public void writeNotINT(boolean newVal) {
        notINT = newVal;
    }

    //
    // Read/Write
    //

    private int readReg(int reg) {
        int base = ((PSW & BS_MASK) == 0) ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        return readRam(base + reg);
    }

    private void writeReg(int reg, int val) {
        int base = ((PSW & BS_MASK) == 0) ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        writeRam(base + reg, val);
    }

    private int readPort(int port) {
        // IMPLEMENT ME
        return 0;
    }

    private void writePort(int port, int data) {
        // IMPLEMENT ME
    }

    private int readBus() {
        // IMPLEMENT ME
        return 0;
    }

    private void writeBus(int data) {
        // IMPLEMENT ME
    }

    private int readExternal(int addr) {
        // IMPLEMENT ME
        return 0;
    }

    private void writeExternal(int addr, int data) {
        // IMPLEMENT ME
    }

    private int readRom(int addr) {
        return rom[addr] & 0xff;
    }

    private int readRam(int addr) {
        return ram[addr] & 0xff;
    }

    private void writeRam(int addr, int val) {
        ram[addr] = (byte)(val & 0xff);
    }

    //
    // Bit-fiddling
    //

    private int getBit(int val, int bit) {
        int mask = 1 << bit;
        return (val & mask) > 0 ? 1 : 0;
    }

    private int setBit(int val, int bit, int bitVal) {
        int mask = 1 << bit;
        val &= ~mask;
        val |= bitVal * mask;
        return val;
    }

    private void setCarry(boolean carry) {
        PSW = setBit(PSW, CY_BIT, carry ? 1 : 0);
    }

    private void setAuxCarry(boolean carry) {
        PSW = setBit(PSW, AC_BIT, carry ? 1 : 0);
    }

    private int fetch() {
        return readRom(PC++);
    }

    private void push() {
        int sp = PSW & 0x7;
        writeRam(8+2*sp, (byte)(PC & 0xff));
        writeRam(9+2*sp, (byte)( PSW & 0xf0 | (PC >> 8) & 0xf ));
        PSW = (byte)(PSW & 0xf8 | (sp + 1) & 0x7);
    }

    private void pop(boolean restoreState) {
        int sp = (PSW - 1) & 0x7;
        PC = (readRam(9+2*sp) & 0xf) << 8 | readRam(8+2*sp) & 0xff;
        if (restoreState) {
            PSW = (byte)(readRam(9+2*sp) & 0xf0 | 0x8 | (sp & 0x7));
            inInterrupt = false;
        }
    }

    private void incCounter() {
        TF = TF ||  T == 0xff;
        T = (T + 1) & 0xff;
    }

    private void tick() {
        // "executes" another cycle, and performs some period stuff (e.g. counting after a STRT T, or interrupt checks
        if (timerRunning) {
            cyclesUntilCount--;
            if (cyclesUntilCount == 0) {
                cyclesUntilCount = 32;
                incCounter();
            }
        }
    }

    private void addToAcc(int value) {
        value &= 0xff;
        int oldA = A;
        int oldLoNibble = A & 0xf;
        A = (A + value) & 0xff;
        int loNibble = A & 0xf;
        setCarry(oldA > A);
        setAuxCarry(oldLoNibble > loNibble);
    }

    private int executeSingleInstr() {
        int cycles = 1;
        int op = fetch();
        tick();
        switch(op) {
            case 0x00: // NOP
                break;

            case 0x02: { // OUTL BUS, A
                cycles++;
                tick();
                writeBus(A);
            }
            break;

            case 0x03: { // ADD A, #data
                int data = fetch();
                cycles++;
                tick();
                addToAcc(data);
            }
            break;

            case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4: {
                // JMP addr
                int addr = (DBF << 11 ) | (op & 0xe0) << 3 | (fetch() & 0xff);
                cycles++;
                tick();
                PC = addr;
            }
            break;

            case 0x05: { // EN I
                externalInterruptsEnabled = true;
            }
            break;

            case 0x07: { // DEC A
                A = (A - 1) & 0xff;
            }
            break;

            case 0x08: { // INS A, BUS
                cycles++;
                tick();
                A = readBus();
            }
            break;

            case 0x09: case 0x0a: { // IN A, Pp
                cycles++;
                tick();
                int p = op & 0x1;
                A = readPort(p);
            }
            break;

            case 0x0c: case 0x0d: case 0x0e: case 0x0f: {
                // MOVD A, Pp
                cycles++;
                tick();
                int p = op & 0x3;
                A = readPort(p) & 0xf;
            }
            break;

            case 0x10: case 0x11: { // INC @Rr
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                writeRam(pos, readRam(pos) + 1);
            }
            break;

            case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2: {
                // JBb addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                int b = op & 0x7;
                if ((A & 1<<b) > 0) {
                    PC = addr;
                }
            }
            break;

            case 0x13: { // ADDC A, #data
                int data = fetch();
                cycles++;
                tick();
                int carry = PSW & CY_MASK;
                addToAcc((carry > 0 ? 1 : 0) + data);
            }
            break;

            case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4: {
                // CALL addr
                int addr = (DBF << 11) | (op & 0xe0) << 3 | (fetch() & 0xff);
                cycles++;
                tick();
                push();
                PC = addr;
            }
            break;

            case 0x15: { // DIS I
                externalInterruptsEnabled = false;
            }
            break;

            case 0x16: { // JTF addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (TF) {
                    TF = false;
                    PC = addr;
                }
            }
            break;

            case 0x17: { // INC A
                A = (A + 1) & 0xff;
            }
            break;

            case 0x18: case 0x19: case 0x1a: case 0x1b: case 0x1c: case 0x1d: case 0x1e: case 0x1f: {
                // INC Rr
                int r = op & 0x7;
                writeReg(r, readReg(r) + 1);
            }
            break;

            case 0x20: case 0x21: { // XCH A, @Rr
                int pos = readReg(op & 0x1);
                int tmp = A;
                A = readRam(pos);
                writeRam(pos, tmp);
            }
            break;

            case 0x23: { // MOV A, #data
                int data = fetch();
                cycles++;
                tick();
                A = data & 0xff;
            }
            break;

            case 0x25: { // EN TCNTI
                tcntInterruptsEnabled = true;
            }
            break;

            case 0x26: { // JNT0 addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (!T0) {
                    PC = addr;
                }
            }
            break;

            case 0x27: { // CLR A
                A = 0;
            }
            break;

            case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f: {
                // XCH A, Rr
                int r  = op & 0x7;
                int tmp = readReg(r);
                writeReg(r, A);
                A = tmp;
            }
            break;

            case 0x30: case 0x31: { // XCHD A, @R
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                int tmp = readRam(pos) & 0xf;
                writeRam(pos, readRam(pos) & 0xf0 | A & 0x0f);
                A = A & 0xf0 | tmp;
            }
            break;

            case 0x35: { // DIS TCNTI
                tcntInterruptsEnabled = false;
            }
            break;


            case 0x36: { // JT0 addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (T0) {
                    PC = addr;
                }
            }
            break;

            case 0x37: { // CPL A
                A = ~A & 0xff;
            }
            break;


            case 0x39: case 0x3a: { // OUTL Pp, A
                cycles++;
                tick();
                int p = op & 0x1;
                writePort(p, A);
            }
            break;

            case 0x3c: case 0x3d: case 0x3e: case 0x3f: {
                // MOVD Pp, A
                cycles++;
                tick();
                int p = op & 0x3;
                writePort(4 + p, A & 0xf);
            }
            break;

            case 0x40: case 0x41: { // ORL A, @Rr
                int pos = readReg(op & 0x1) & 0x7f;
                A |= readRam(pos);
            }
            break;

            case 0x42: { // MOV A, T
                A = T;
            }
            break;

            case 0x43: { // ORL A, #data
                int data = fetch();
                cycles++;
                tick();
                A |= data & 0xff;
            }
            break;

            case 0x45: { // STRT CNT
                counterRunning = true;
                timerRunning = false;
            }
            break;

            case 0x46: { // JNT1 addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (!T1) {
                    PC = addr;
                }
            }
            break;

            case 0x47: { // SWAP A
                int hiNibble = A & 0xf0;
                int loNibble = A & 0x0f;
                A = (loNibble << 4) | (hiNibble >> 4);
            }
            break;

            case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f: {
                // ORL A, Rr
                int r = op & 0x7;
                A |= readReg(r);
            }
            break;

            case 0x50: case 0x51: { // ANL A, @Rr
                int pos = readReg(op & 0x1) & 0x7f;
                A &= readRam(pos);
            }
            break;

            case 0x53: { // ANL A, #data
                int data = fetch();
                cycles++;
                tick();
                A &= data & 0xff;
            }
            break;

            case 0x55: { // STRT T
                counterRunning = false;
                timerRunning = true;
                cyclesUntilCount = 32;
            }
            break;

            case 0x56: { // JT1 addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (T1) {
                    PC = addr;
                }
            }
            break;

            case 0x57: { // DA A
                if ( (A & 0x0f) > 9 || (PSW & AC_MASK) > 0) {
                    A += 9;
                }
                int hiNibble = (A & 0xf0) >> 4;
                if (hiNibble > 9 || (PSW & CY_MASK) > 0) {
                    hiNibble += 6;
                }
                A = (hiNibble << 4 | (A & 0xf)) &0xff;
                setCarry(hiNibble > 15);
            }
            break;

            case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f: {
                // ANL A, Rr
                int r = op & 0x7;
                A &= readReg(r);
            }

            case 0x60: case 0x61: { // ADD A, @Rr
                int pos = readReg(op & 0x1);
                addToAcc(readRam(pos));
            }
            break;

            case 0x62: { // MOV T, A
                T = A;
            }
            break;

            case 0x65: { // STOP TCNT
                timerRunning = false;
                counterRunning = false;
            }
            break;

            case 0x67: { // RRC A
                int newCarry = A & 1;
                A = A >> 1;
                A = setBit(A, 7, (PSW & CY_MASK) > 0 ? 1 : 0);
                setCarry(newCarry > 0);
            }
            break;

            case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f: {
                // ADD A, Rr
                addToAcc(readReg(op & 0x7));
            }
            break;

            case 0x70: case 0x71: { // ADDC A, @Rr
                int pos = readReg(op & 0x1) & 0x7f;
                int carry = PSW & CY_MASK;
                addToAcc((carry > 0 ? 1 : 0) + readRam(pos));
            }
            break;

            case 0x75: { // ENT0 CLK
                throw new IllegalStateException("ENT0 CLK is not implemented");
            }

            case 0x76: { // JF1 addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (F1) {
                    PC = addr;
                }
            }
            break;

            case 0x77: { // RR A
                int a0 = A & 1;
                A = A >> 1;
                A = setBit(A, 7, a0);
            }
            break;

            case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f: {
                // ADDC A, Rr
                int carry = PSW & CY_MASK;
                addToAcc((carry > 0 ? 1 : 0) + readReg(op & 0x7));
            }
            break;

            case 0x80: case 0x81: { // MOVX A, @Rr
                cycles++;
                tick();
                int pos = readReg(op & 0x1);
                A = readExternal(pos);
            }
            break;

            case 0x83: { // RET
                cycles++;
                tick();
                pop(false);
            }
            break;

            case 0x85: { // CLR F0
                PSW = setBit(PSW, F0_BIT, 0);
            }
            break;

            case 0x86: { // JNI addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (notINT) {
                    PC = addr;
                }
            }
            break;

            case 0x88: { // ORL BUS, #data
                cycles++;
                tick();
                int data = fetch();
                int val = readBus();
                writeBus((readBus() | data) & 0xff);
            }
            break;

            case 0x89: case 0x8a: { // ORL Pp, #data
                int p = op & 0x3;
                cycles++;
                tick();
                int data = fetch();
                writePort(p, (readPort(p) | data) & 0xff);
            }
            break;

            case 0x8c: case 0x8d: case 0x8e: case 0x8f: { // ORLD Pp, A
                int p = op & 0x3;
                cycles++;
                tick();
                writePort(p, (readPort(4 + p) | A & 0xf) & 0xff);
            }
            break;

            case 0x90: case 0x91: { // MOVX @R, A
                cycles++;
                tick();
                int pos = readReg(op & 0x1);
                writeExternal(pos, A);
            }
            break;

            case 0x93: { // RETR
                cycles++;
                tick();
                pop(true);
            }
            break;

            case 0x95: { // CPL F0
                PSW = setBit(PSW, F0_BIT, 1 - getBit(PSW, F0_BIT));
            }
            break;

            case 0x96: { // JNZ addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (A != 0) {
                    PC = addr;
                }
            }
            break;

            case 0x97: { // CLR C
                PSW = setBit(PSW, CY_BIT, 0);
            }
            break;

            case 0x98: { // ANL BUS, #data
                cycles++;
                tick();
                int data = fetch();
                int val = readBus();
                writeBus((readBus() & data) & 0xff);
            }
            break;

            case 0x99: case 0x9a: { // ANL Pp, #data
                int p = op & 0x3;
                cycles++;
                tick();
                int data = fetch();
                writePort(p, (readPort(p) & data) & 0xff);
            }
            break;

            case 0x9c: case 0x9d: case 0x9e: case 0x9f: { // ANLD Pp, A
                int p = op & 0x3;
                cycles++;
                tick();
                writePort(p, (readPort(4 + p) & A & 0xf) & 0xff);
            }
            break;

            case 0xa0: case 0xa1: { // MOV @Rr, A
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                writeRam(pos, A);
            }
            break;

            case 0xa3: { // MOVP A, @A
                cycles++;
                tick();
                int pos = (PC & 0xf00) | (A & 0xff);
                A = readRam(pos);
            }
            break;

            case 0xa5: { // CLR F1
                F1 = false;
            }
            break;

            case 0xa7: { // CPL C
                PSW = setBit(PSW, CY_BIT, 1 - getBit(PSW, CY_BIT));
            }
            break;

            case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf: {
                // MOV Rr, A
                writeReg(op & 0x7, A);
            }
            break;

            case 0xb0: case 0xb1: { // MOV @Rr, #data
                int r = op & 0x1;
                int data = fetch();
                cycles++;
                tick();
                writeRam(readReg(r), data);
            }
            break;

            case 0xb3: { // JMPP @A
                cycles++;
                tick();
                PC = PC & 0xf00 | A & 0xff;
            }
            break;

            case 0xb5: { // CPL F1
                F1 = !F1;
            }
            break;

            case 0xb6: { // JF0 addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (getBit(PSW, F0_BIT) > 0) {
                    PC = addr;
                }
            }
            break;

            case 0xb8: case 0xb9: case 0xba: case 0xbb: case 0xbc: case 0xbd: case 0xbe: case 0xbf: {
                // MOV Rr, #data
                int r = op & 0x7;
                int data = fetch();
                cycles++;
                tick();
                writeReg(r, data);
            }
            break;

            case 0xc5: { // SEL RB0
                PSW = setBit(PSW, BS_BIT, 0);
            }
            break;

            case 0xc6: { // JZ addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (A == 0) {
                    PC = addr;
                }
            }
            break;

            case 0xc7: { // MOV A, PSW
                A = PSW;
            }
            break;

            case 0xc8: case 0xc9: case 0xca: case 0xcb: case 0xcc: case 0xcd: case 0xce: case 0xcf: {
                // DEC Rr
                int r = op & 0x7;
                writeReg(r, readReg(r) - 1);
            }
            break;

            case 0xd0: case 0xd1: { // XRL A, @Rr
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                A = (A ^ readRam(pos)) & 0xff;
            }
            break;

            case 0xd3:{ // XRL A, #data
                int data = fetch();
                cycles++;
                tick();
                A ^= data & 0xff;
            }
            break;

            case 0xd5: { // SEL RB1
                PSW = setBit(PSW, BS_BIT, 1);
            }
            break;

            case 0xd7: { // MOV PSW, A
                PSW = A;
            }
            break;

            case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf: {
                // XRL A, Rr
                int r = op & 0x7;
                A ^= readReg(r);
            }
            break;

            case 0xe3: { // MOVP3 A, @A
                cycles++;
                tick();
                int pos = 0x300 | (A & 0xff);
                A = readRam(pos);
            }
            break;

            case 0xe5: { // SEL MB0
                DBF = 0;
            }
            break;

            case 0xe6: { // JNC addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (getBit(PSW, CY_BIT) == 0) {
                    PC = addr;
                }
            }
            break;

            case 0xe7: { // RL A
                A = (A << 1) & 0xff | ((A & 0x80) >> 7);
            }
            break;

            case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef: {
                // DJNZ Rr, addr
                int r = op & 0x7;
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                int val = readReg(r) - 1;
                writeReg(r, val);
                if (val != 0) {
                    PC = addr;
                }
            }
            break;

            case 0xf0: case 0xf1: { // MOV A, @Rr
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                A = readRam(pos);
            }
            break;

            case 0xf5: { // SEL MB1
                DBF = 1;
            }
            break;

            case 0xf6: { // JC addr
                int addr = fetch();
                addr = (PC & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (getBit(PSW, CY_BIT) != 0) {
                    PC = addr;
                }
            }
            break;

            case 0xf7: { // RLC A
                int newCarry = A & 0x80;
                A = (A << 1) & 0xff;
                if (getBit(PSW, CY_BIT) > 0) {
                    A |= 1;
                }
                setCarry(newCarry > 0);
            }
            break;

            case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff: {
                // MOV A, Rr
                A = readReg(op & 0x7);
            }
            break;

            case 0x01:
            case 0x06:
            case 0x0b:
            case 0x22:
            case 0x33:
            case 0x38:
            case 0x3b:
            case 0x63:
            case 0x66:
            case 0x73:
            case 0x82:
            case 0x87:
            case 0x8b:
            case 0x9b:
            case 0xa2:
            case 0xa6:
            case 0xb7:
            case 0xc0:
            case 0xc1:
            case 0xc2:
            case 0xc3:
            case 0xd6:
            case 0xe0:
            case 0xe1:
            case 0xe2:
            case 0xf3:
                logger.info(String.format("Illegal op-code 0x%02x", op));
                break;
        }
        return cycles;
    }

    private void handleInterrupts() {
        if (!inInterrupt) {
            // not handling an interrupt, so let's check if we need to.
            if (!notINT && externalInterruptsEnabled) {
                // handle external interrupt
                push();
                PC = 3;
                inInterrupt = true;
            } else if (TF && tcntInterruptsEnabled) {
                // handle timer interrupt
                push();
                PC = 7;
                inInterrupt = true;
            }
        }
    }

    void run(int cycles) {
        while (cycles > 0) {
            cycles -= executeSingleInstr();
            handleInterrupts();
        }
    }

    //
    // Memory related stuff
    //

    public void loadMemory(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int read;
        while ( (read = is.read(buf)) > 0) {
            os.write(buf, 0, read);
        }
        rom = os.toByteArray();
    }

    //
    // Utilities
    //
    public void disassemble(int from, int to) {
        int pos = from;
        while (pos < to) {
            int curPos = pos;
            int op = readRom(pos++) & 0xff;
            int r, p, b, addr;
            int data;

            switch(op) {
                case 0x00:
                    emit(curPos, 1, "NOP");
                    break;

                case 0x02:
                    emit(curPos, 1, "OUTL", "BUS", "A");
                    break;

                case 0x03:
                    data = readRom(pos++);
                    emit(curPos, 2, "ADD", "A", "#" + String.format("%02x", data));
                    break;

                case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4:
                    addr = (op & 0xe0) << 3 | (readRom(pos++) & 0xff);
                    emit(curPos, 2, "JMP", String.format("%04x", addr));
                    break;

                case 0x05:
                    emit(curPos, 1, "EN", "I");
                    break;

                case 0x07:
                    emit(curPos, 1, "DEC", "A");
                    break;

                case 0x08:
                    emit(curPos, 1, "INS", "A", "BUS");
                    break;

                case 0x09: case 0x0a:
                    p = op & 0x3;
                    emit(curPos, 1, "IN", "A", "P" + p);
                    break;

                case 0x0c: case 0x0d: case 0x0e: case 0x0f:
                    p = op & 0x3;
                    emit(curPos, 1, "MOVD", "A", "P" + (4 + p));
                    break;

                case 0x10: case 0x11:
                    r = op & 0x1;
                    emit(curPos, 1, "INC", "@R" + r);
                    break;

                case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    b = op & 0x7;
                    emit(curPos, 2, "JB" + b, String.format("%04x", addr));
                    break;

                case 0x13:
                    data = readRom(pos++) & 0xff;
                    emit(curPos, 2, "ADDC", "A", "#" + String.format("%02x", data));
                    break;

                case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4:
                    addr = (op & 0xe0) << 3 | (readRom(pos++) & 0xff);
                    emit(curPos, 1, "CALL", String.format("%04x", addr));
                    break;

                case 0x15:
                    emit(curPos, 1, "DIS", "I");
                    break;

                case 0x16:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JTF", String.format("%04x", addr));
                    break;

                case 0x17:
                    emit(curPos, 1, "INC", "A");
                    break;

                case 0x18: case 0x19: case 0x1a: case 0x1b: case 0x1c: case 0x1d: case 0x1e: case 0x1f:
                    r = op & 0x7;
                    emit(curPos, 1, "INC", "R" + r);
                    break;

                case 0x20: case 0x21:
                    r = op & 0x1;
                    emit(curPos, 1, "XCH", "A", "@R" + r);
                    break;

                case 0x23:
                    data = readRom(pos++) & 0xff;
                    emit(curPos, 2, "MOV", "A", "#" + String.format("%02x", data));
                    break;

                case 0x25:
                    emit(curPos, 1, "EN", "TCNTI");
                    break;

                case 0x26:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNT0", String.format("%04x", addr));
                    break;

                case 0x27:
                    emit(curPos, 1, "CLR", "A");
                    break;

                case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f:
                    r = op & 0x7;
                    emit(curPos, 1, "XCH", "A", "R" + r);
                    break;

                case 0x30: case 0x31:
                    r = op & 0x1;
                    emit(curPos, 1, "XCHD", "A", "@R" + r);
                    break;

                case 0x35:
                    emit(curPos, 1, "DIS", "TCNTI");
                    break;

                case 0x36:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JT0", String.format("%04x", addr));
                    break;

                case 0x37:
                    emit(curPos, 1, "CPL", "A");
                    break;

                case 0x39: case 0x3a:
                    p = op & 0x3;
                    emit(curPos, 1, "OUTL", "P" + p, "A");
                    break;

                case 0x3c: case 0x3d: case 0x3e: case 0x3f:
                    p = op & 0x3;
                    emit(curPos, 1, "MOVD", "P" + (4 + p), "A");
                    break;

                case 0x40: case 0x41:
                    r = op & 0x1;
                    emit(curPos, 1, "ORL", "A", "@R" + r);
                    break;

                case 0x42:
                    emit(curPos, 1, "MOV", "A", "T");
                    break;

                case 0x43:
                    data = readRom(pos++) & 0xff;
                    emit(curPos, 2, "ORL", "A", "#" + String.format("%02x", data));
                    break;

                case 0x45:
                    emit(curPos, 1, "STRT", "CNT");
                    break;

                case 0x46:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNT1", String.format("%04x", addr));
                    break;

                case 0x47:
                    emit(curPos, 1, "SWAP", "A");
                    break;

                case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f:
                    r = op & 0x7;
                    emit(curPos, 1, "ORL", "A", "R" + r);
                    break;

                case 0x50: case 0x51:
                    r = op & 0x1;
                    emit(curPos, 1, "ANL", "A", "@R" + r);
                    break;

                case 0x53:
                    data = readRom(pos++);
                    emit(curPos, 2, "ANL", "A", "#" + String.format("%02x", data));
                    break;

                case 0x55:
                    emit(curPos, 1, "STRT", "T");
                    break;

                case 0x56:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JT1", String.format("%04x", addr));
                    break;

                case 0x57:
                    emit(curPos, 1, "DA", "A");
                    break;

                case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f:
                    r = op & 0x7;
                    emit(curPos, 1, "ANL", "A", "R" + r);
                    break;

                case 0x60: case 0x61:
                    r = op & 0x1;
                    emit(curPos, 1, "ADD", "A", "@R" + r);
                    break;

                case 0x62:
                    emit(curPos, 1, "MOV", "T", "A");
                    break;

                case 0x65:
                    emit(curPos, 1, "STOP", "TCNT");
                    break;

                case 0x67:
                    emit(curPos, 1, "RRC", "A");
                    break;

                case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f:
                    r = op & 0x7;
                    emit(curPos, 1, "ADD", "A", "R" + r);
                    break;

                case 0x70: case 0x71:
                    r = op & 0x1;
                    emit(curPos, 1, "ADDC", "A", "@R" + r);
                    break;

                case 0x75:
                    emit(curPos, 1, "ENT0", "CLK");
                    break;

                case 0x76:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JF1", String.format("%04x", addr));
                    break;

                case 0x77:
                    emit(curPos, 1, "RR", "A");
                    break;

                case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f:
                    r = op & 0x7;
                    emit(curPos, 1, "ADDC", "A", "R" + r);
                    break;

                case 0x80: case 0x81:
                    r = op & 0x1;
                    emit(curPos, 1, "MOVX", "A", "@R" + r);
                    break;

                case 0x83:
                    emit(curPos, 1, "RET");
                    break;

                case 0x85:
                    emit(curPos, 1, "CLR", "F0");
                    break;

                case 0x86:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNI", String.format("%04x", addr));
                    break;

                case 0x88:
                    data = readRom(pos++);
                    emit(curPos, 2, "ORL", "BUS", "#" + String.format("%02x", data));
                    break;

                case 0x89: case 0x8a:
                    data = readRom(pos++);
                    p = op & 0x3;
                    emit(curPos, 2, "ORL", "P" + p, "#" + String.format("%02x", data));
                    break;

                case 0x8c: case 0x8d: case 0x8e: case 0x8f:
                    p = op & 0x3;
                    emit(curPos, 1, "ORLD", "P" + (4 + p), "A");
                    break;

                case 0x90: case 0x91:
                    r = op & 0x1;
                    emit(curPos, 1, "MOVX", "@R" + r, "A");
                    break;

                case 0x93:
                    emit(curPos, 1, "RETR");
                    break;

                case 0x95:
                    emit(curPos, 1, "CPL", "F0");
                    break;

                case 0x96:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNZ", String.format("%04x", addr));
                    break;

                case 0x97:
                    emit(curPos, 1, "CLR", "C");
                    break;

                case 0x98:
                    data = readRom(pos++);
                    emit(curPos, 2, "ANL", "BUS", "#" + String.format("%02x", data));
                    break;

                case 0x99: case 0x9a:
                    data = readRom(pos++);
                    p = op & 0x3;
                    emit(curPos, 2, "ANL", "P" + p, "#" + String.format("%02x", data));
                    break;

                case 0x9c: case 0x9d: case 0x9e: case 0x9f:
                    p = op & 0x3;
                    emit(curPos, 1, "ANLD", "P" + (4 + p), "A");
                    break;

                case 0xa0: case 0xa1:
                    r = op & 0x1;
                    emit(curPos, 1, "MOV", "@R" + r, "A");
                    break;

                case 0xa3:
                    emit(curPos, 1, "MOVP", "A", "@A");
                    break;

                case 0xa5:
                    emit(curPos, 1, "CLR", "F1");
                    break;

                case 0xa7:
                    emit(curPos, 1, "CPL", "C");
                    break;

                case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf:
                    r = op & 0x7;
                    emit(curPos, 1, "MOV", "R" + r, "A");
                    break;

                case 0xb0: case 0xb1:
                    data = readRom(pos++);
                    r = op & 0x1;
                    emit(curPos, 2, "MOV", "@R" + r, "#" + String.format("%02x", data));
                    break;

                case 0xb3:
                    emit(curPos, 1, "JMPP", "@A");
                    break;

                case 0xb5:
                    emit(curPos, 1, "CPL", "F1");
                    break;

                case 0xb6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JF0", String.format("%04x", addr));
                    break;

                case 0xb8: case 0xb9: case 0xba: case 0xbb: case 0xbc: case 0xbd: case 0xbe: case 0xbf:
                    data = readRom(pos++);
                    r = op & 0x7;
                    emit(curPos, 2, "MOV", "R" + r, "#" + String.format("%02x", data));
                    break;

                case 0xc5:
                    emit(curPos, 1, "SEL", "RB0");
                    break;

                case 0xc6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JZ", String.format("%04x", addr));
                    break;

                case 0xc7:
                    emit(curPos, 1, "MOV", "A", "PSW");
                    break;

                case 0xc8: case 0xc9: case 0xca: case 0xcb: case 0xcc: case 0xcd: case 0xce: case 0xcf:
                    r = op & 0x7;
                    emit(curPos, 1, "DEC", "R" + r);
                    break;

                case 0xd0: case 0xd1:
                    r = op & 0x1;
                    emit(curPos, 1, "XRL", "A", "@R" + r);
                    break;

                case 0xd3:
                    data = readRom(pos++);
                    emit(curPos, 2, "XRL", "A", "#" + String.format("%02x", data));
                    break;

                case 0xd5:
                    emit(curPos, 1, "SEL", "RB1");
                    break;

                case 0xd7:
                    emit(curPos, 1, "MOV", "PSW", "A");
                    break;

                case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf:
                    r = op & 0x7;
                    emit(curPos, 1, "XRL", "A", "R" + r);
                    break;

                case 0xe3:
                    emit(curPos, 1, "MOVP3", "A", "@A");
                    break;

                case 0xe5:
                    emit(curPos, 1, "SEL", "MB0");
                    break;

                case 0xe6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JNC", String.format("%04x", addr));
                    break;

                case 0xe7:
                    emit(curPos, 1, "RL", "A");
                    break;

                case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    r = op & 0x7;
                    emit(curPos, 2, "DJNZ", "R" + r, String.format("%04x", addr));
                    break;

                case 0xf0: case 0xf1:
                    r = op & 0x1;
                    emit(curPos, 1, "MOV", "A", "@R" + r);
                    break;

                case 0xf5:
                    emit(curPos, 1, "SEL", "MB1");
                    break;

                case 0xf6:
                    addr = readRom(pos++);
                    addr = (pos & 0xf00) | (addr & 0xff);
                    emit(curPos, 2, "JC", String.format("%04x", addr));
                    break;

                case 0xf7:
                    emit(curPos, 1, "RLC", "A");
                    break;

                case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff:
                    r = op & 0x7;
                    emit(curPos, 1, "MOV", "A", "R" + r);
                    break;

                case 0x01:
                case 0x06:
                case 0x0b:
                case 0x22:
                case 0x33:
                case 0x38:
                case 0x3b:
                case 0x63:
                case 0x66:
                case 0x73:
                case 0x82:
                case 0x87:
                case 0x8b:
                case 0x9b:
                case 0xa2:
                case 0xa6:
                case 0xb7:
                case 0xc0:
                case 0xc1:
                case 0xc2:
                case 0xc3:
                case 0xd6:
                case 0xe0:
                case 0xe1:
                case 0xe2:
                case 0xf3:
                    emit(curPos, 1, ".DB", "" + op);
                    break;
            }
        }
    }

    private void emit(int curPos, int bytes, String op, String ... args) {
        String hexDump = "";
        for (int i = 0; i < 2; i++) {
            if (hexDump.length() > 0) hexDump += " ";
            hexDump += i < bytes ? String.format("%02x", readRom(curPos + i)) : "  ";
        }
        System.out.println(String.format("%04x: %s | %-4s %s", curPos, hexDump, op, join(args)));
    }

    private String join(String ... args) {
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        for (String arg : args) {
            if (!first) {
                buf.append(", ");
            }
            buf.append(arg);
            first = false;
        }
        return buf.toString();
    }


    public static void main(String ... args) {
        try {
            Cpu cpu = new Cpu();
            cpu.loadMemory(new FileInputStream("CP1.bin"));
            cpu.disassemble(0, 0x800);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
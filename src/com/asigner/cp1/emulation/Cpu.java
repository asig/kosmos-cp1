package com.asigner.cp1.emulation;


import java.util.logging.Logger;

public class Cpu {

    private static final Logger logger = Logger.getLogger(Cpu.class.getName());

    // Bits in PSW
    public static final int CY_BIT = 7;
    public static final int AC_BIT = 6;
    public static final int F0_BIT = 5;
    public static final int BS_BIT = 4;

    public static final int REGISTER_BANK_0_BASE = 0;
    public static final int REGISTER_BANK_1_BASE = 24;

    // Interrupt pins and flipflops
    private boolean TF; // Timer Flag
    private boolean notINT;
    private boolean T0;
    private boolean T1;

    private int T;
    private int A;
    private int PC;
    private int PSW;
    private int DBF;
    private int F1;

    private boolean externalInterruptsEnabled;
    private boolean tcntInterruptsEnabled;
    private boolean counterRunning; // Whether counter is bound to T1 (STRT CNT)
    private boolean timerRunning; // Whether counter is bound to clock (STRT T)
    private long cyclesUntilCount; // Number of cycles until we need to increment the count (if counter is bound to clock)
    private boolean inInterrupt; // True iff handling an interrupt. Reset by RETR

    private Rom rom;
    private Ram ram;

    public Cpu(Ram ram, Rom rom) {
        this.ram = ram;
        this.rom = rom;
        reset();
    }

    public void reset() {
        TF = false;
        notINT = true;
        T0 = false;
        T1 = false;

        // T is not affected by reset()
        A = 0;
        PC = 0;
        PSW = 0xf; // bit 3 is always rom.read as "1"
        DBF = 0; // memory bank 0
        F1 = 0;
        externalInterruptsEnabled = false;
        tcntInterruptsEnabled = false;
        counterRunning = false;
        timerRunning = false;
        inInterrupt = false;
        ram.clear();
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
        int base = (getBit(PSW, BS_BIT) == 0) ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        return ram.read(base + reg);
    }

    private void writeReg(int reg, int val) {
        int base = (getBit(PSW, BS_BIT) == 0) ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        ram.write(base + reg, val);
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
        return rom.read(PC++);
    }

    private void push() {
        int sp = PSW & 0x7;
        ram.write(8+2*sp, (byte)(PC & 0xff));
        ram.write(9+2*sp, (byte)( PSW & 0xf0 | (PC >> 8) & 0xf ));
        PSW = (byte)(PSW & 0xf8 | (sp + 1) & 0x7);
    }

    private void pop(boolean restoreState) {
        int sp = (PSW - 1) & 0x7;
        PC = (ram.read(9+2*sp) & 0xf) << 8 | ram.read(8+2*sp) & 0xff;
        if (restoreState) {
            PSW = (byte)(ram.read(9+2*sp) & 0xf0 | 0x8 | (sp & 0x7));
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

    public int executeSingleInstr() {
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
                ram.write(pos, ram.read(pos) + 1);
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
                int carry = getBit(PSW, CY_BIT);
                addToAcc(carry + data);
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
                A = ram.read(pos);
                ram.write(pos, tmp);
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
                int tmp = ram.read(pos) & 0xf;
                ram.write(pos, ram.read(pos) & 0xf0 | A & 0x0f);
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
                A |= ram.read(pos);
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
                A &= ram.read(pos);
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
                if ( (A & 0x0f) > 9 || getBit(PSW, AC_BIT) > 0) {
                    A += 9;
                }
                int hiNibble = (A & 0xf0) >> 4;
                if (hiNibble > 9 || getBit(PSW, CY_BIT) > 0) {
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
                addToAcc(ram.read(pos));
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
                A = setBit(A, 7, getBit(PSW, CY_BIT));
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
                int carry = getBit(PSW, CY_BIT);
                addToAcc(carry + ram.read(pos));
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
                if (F1 != 0) {
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
                int carry = getBit(PSW, CY_BIT);
                addToAcc(carry + readReg(op & 0x7));
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
                int bus = readBus();
                writeBus((bus | data) & 0xff);
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
                int bus = readBus();
                writeBus((bus & data) & 0xff);
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
                ram.write(pos, A);
            }
            break;

            case 0xa3: { // MOVP A, @A
                cycles++;
                tick();
                int pos = (PC & 0xf00) | (A & 0xff);
                A = ram.read(pos);
            }
            break;

            case 0xa5: { // CLR F1
                F1 = 0;
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
                ram.write(readReg(r), data);
            }
            break;

            case 0xb3: { // JMPP @A
                cycles++;
                tick();
                PC = PC & 0xf00 | A & 0xff;
            }
            break;

            case 0xb5: { // CPL F1
                F1 = 1 - F1;
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
                A = (A ^ ram.read(pos)) & 0xff;
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
                A = ram.read(pos);
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
                A = ram.read(pos);
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
        handleInterrupts();
        return cycles;
    }

    void execute(int cycles) {
        while (cycles > 0) {
            cycles -= executeSingleInstr();
        }
    }

}

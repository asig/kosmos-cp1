/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.asigner.cp1.emulation;


import com.asigner.cp1.emulation.util.Disassembler;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Intel8049 {

    public interface StateListener {
        void instructionExecuted();
        void resetExecuted();
        void stateChanged(State newState);
    }

    private static final Logger logger = Logger.getLogger(Intel8049.class.getName());

    private List<StateListener> listeners = new LinkedList<>();

    // TODO(asigner):
    // - ALE should happen once per cycle. This is currently not the case, but done in MOVX directly
    // - Signal /RD and /WR in other BUS operations as well.

    // Pins
    public OutputPin pinPROG = new OutputPin("PROG");
    public OutputPin pinALE = new OutputPin("ALE");
    public OutputPin pinRDLowActive = new OutputPin("/RD");
    public OutputPin pinWRLowActive = new OutputPin("/WR");

    // Bits in PSW
    public static final int CY_BIT = 7;
    public static final int AC_BIT = 6;
    public static final int F0_BIT = 5;
    public static final int BS_BIT = 4;

    public static final int REGISTER_BANK_0_BASE = 0;
    public static final int REGISTER_BANK_1_BASE = 24;

    public static final class State {
        // Interrupt pins and flipflops
        private boolean TF; // Timer Flag
        private boolean notINT;
        private boolean timerInterruptRequested;
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
        private long cyclesUntilCount; // prescaler: Number of cycles until we need to increment the count (if counter is bound to clock)
        private boolean inInterrupt; // True iff handling an interrupt. Reset by RETR
    }

    private Rom rom;
    private Ram ram;
    private State state;
    private DataPort[] ports;

    private Disassembler disassembler;

    public Intel8049(Rom rom, DataPort bus, DataPort p1, DataPort p2) {
        this.ram = new Ram(128);
        this.rom = rom;
        this.disassembler = new Disassembler(rom);
        this.state = new State();
        this.ports = new DataPort[] { bus, p1, p2 };
        reset();
    }

    public DataPort getPort(int p) {
        return ports[p];
    }

    public Ram getRam() {
        return ram;
    }

    public Rom getRom() {
        return rom;
    }

    public int getT() {
        return state.T;
    }

    public int getA() {
        return state.A;
    }

    public int getPC() {
        return state.PC;
    }

    public int getPSW() {
        return state.PSW;
    }

    public int getDBF() {
        return state.DBF;
    }

    public int getF1() {
        return state.F1;
    }

    public void setT(int t) {
        if (t != state.T) {
            state.T = t;
            listeners.forEach(l -> l.stateChanged(state));
        }
    }

    public void setA(int a) {
        if (a != state.A) {
            state.A = a;
            listeners.forEach(l -> l.stateChanged(state));
        }

    }

    public void setPC(int pc) {
        if (pc != state.PC) {
            state.PC = pc;
            listeners.forEach(l -> l.stateChanged(state));
        }
    }

    public void setPSW(int psw) {
        if (psw != state.PSW) {
            state.PSW = psw;
            listeners.forEach(l -> l.stateChanged(state));
        }
    }

    public void setDBF(int dbf) {
        if (dbf != state.DBF) {
            state.DBF = dbf;
            listeners.forEach(l -> l.stateChanged(state));
        }
    }

    public void setF1(int f1) {
        if (f1 != state.F1) {
            state.F1 = f1;
            listeners.forEach(l -> l.stateChanged(state));
        }
    }


    public void addListener(StateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(StateListener listener) {
        listeners.remove(listener);
    }

    public void reset() {
        state.TF = false;
        state.notINT = true;
        state.T0 = false;
        state.T1 = false;
        state.timerInterruptRequested = false;

        // T is not affected by reset()
        state.A = 0;
        state.PC = 0;
        state.PSW = 0x8; // bit 3 is always rom.read as "1"
        state.DBF = 0; // memory bank 0
        state.F1 = 0;
        state.externalInterruptsEnabled = false;
        state.tcntInterruptsEnabled = false;
        state.counterRunning = false;
        state.timerRunning = false;
        state.inInterrupt = false;

        ports[0].write(0x00);
        ports[1].write(0xff);
        ports[2].write(0xff);

        ram.clear();

        listeners.forEach(StateListener::resetExecuted);
    }

    //
    // Write to pins
    //

    public void writeT1(boolean newVal) {
        boolean oldT1 = state.T1;
        state.T1 = newVal;
        if (state.counterRunning && oldT1 && !state.T1) {
            // high -> low: count
            incCounter();
        }
    }

    public void writeNotINT(boolean newVal) {
        state.notINT = newVal;
    }

    //
    // Read/Write
    //

    private int readReg(int reg) {
        int base = (getBit(state.PSW, BS_BIT) == 0) ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        return ram.read(base + reg);
    }

    private void writeReg(int reg, int val) {
        int base = (getBit(state.PSW, BS_BIT) == 0) ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        ram.write(base + reg, val);
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
        state.PSW = setBit(state.PSW, CY_BIT, carry ? 1 : 0);
    }

    private void setAuxCarry(boolean carry) {
        state.PSW = setBit(state.PSW, AC_BIT, carry ? 1 : 0);
    }

    public int peek() {
        return rom.read(state.PC);
    }

    private int fetch() {
        return rom.read(state.PC++);
    }

    private void push() {
        int sp = state.PSW & 0x7;
        ram.write(8+2*sp, (byte)(state.PC & 0xff));
        ram.write(9+2*sp, (byte)(state.PSW & 0xf0 | (state.PC >> 8) & 0xf ));
        state.PSW = (byte)(state.PSW & 0xf8 | (sp + 1) & 0x7);
    }

    private void pop(boolean restoreState) {
        int sp = (state.PSW - 1) & 0x7;
        state.PC = (ram.read(9+2*sp) & 0xf) << 8 | ram.read(8+2*sp) & 0xff;
        if (restoreState) {
            state.PSW = (byte)(ram.read(9+2*sp) & 0xf0 | 0x8 | (sp & 0x7));
            state.inInterrupt = false;
        } else {
            state.PSW = state.PSW & 0xf0 | 0x8 | (sp & 0x7);
        }
    }

    private void incCounter() {
        state.TF = state.TF || state.T == 0xff;
        state.timerInterruptRequested = state.timerInterruptRequested || state.T == 0xff;
        state.T = (state.T + 1) & 0xff;
    }

    private void tick() {
        // Force a fall

        // "executes" another cycle, and performs some periodic stuff (e.g. counting after a STRT T, or interrupt checks
        if (state.timerRunning) {
            state.cyclesUntilCount--;
            if (state.cyclesUntilCount == 0) {
                state.cyclesUntilCount = 32;
                incCounter();
            }
        }
    }

    private void addToAcc(int value) {
        value &= 0xff;
        int oldA = state.A;
        int oldLoNibble = state.A & 0xf;
        state.A = (state.A + value) & 0xff;
        int loNibble = state.A & 0xf;
        setCarry(oldA > state.A);
        setAuxCarry(oldLoNibble > loNibble);
    }

    private void handleInterrupts() {
        if (!state.inInterrupt) {
            // not handling an interrupt, so let's check if we need to.
            if (!state.notINT && state.externalInterruptsEnabled) {
                // handle external interrupt
                push();
                state.PC = 3;
                state.inInterrupt = true;
            } else if (state.timerInterruptRequested && state.tcntInterruptsEnabled) {
                // handle timer interrupt
                state.timerInterruptRequested = false;
                push();
                state.PC = 7;
                state.inInterrupt = true;
            }
        }
    }

    public int executeSingleInstr() {

        if (logger.isLoggable(Level.FINEST)) {
            Disassembler.Line line = disassembler.disassemble(state.PC);
            logger.finest(String.format("Executing instr: %03x %s", line.getAddress(), line.getDisassembly()));
        }

        int cycles = 1;
        int op = fetch();
        tick();
        switch(op) {
            case 0x00: // NOP
                break;

            case 0x02: { // OUTL BUS, A
                cycles++;
                tick();
                ports[0].write(state.A);
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
                int addr = (state.DBF << 11 ) | (op & 0xe0) << 3 | (fetch() & 0xff);
                cycles++;
                tick();
                state.PC = addr;
            }
            break;

            case 0x05: { // EN I
                state.externalInterruptsEnabled = true;
            }
            break;

            case 0x07: { // DEC A
                state.A = (state.A - 1) & 0xff;
            }
            break;

            case 0x08: { // INS A, BUS
                cycles++;
                tick();
                state.A = ports[0].read();
            }
            break;

            case 0x09: case 0x0a: { // IN A, Pp
                cycles++;
                tick();
                int p = op & 0x1;
                state.A = ports[p].read();
            }
            break;

            case 0x0c: case 0x0d: case 0x0e: case 0x0f: {
                // MOVD A, Pp
                cycles++;
                tick();
                int p = op & 0x3;
                int nibble = 0b0000 | p; // READ port p

                pinPROG.write(1);
                ports[2].write(nibble, 0x0f);
                pinPROG.write(0); // Address is valid

                state.A = ports[2].read() & 0xf;
                pinPROG.write(1);
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
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                int b = (op >> 5) & 0x7;
                if ((state.A & 1<<b) > 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0x13: { // ADDC A, #data
                int data = fetch();
                cycles++;
                tick();
                int carry = getBit(state.PSW, CY_BIT);
                addToAcc(carry + data);
            }
            break;

            case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4: {
                // CALL addr
                int addr = (state.DBF << 11) | (op & 0xe0) << 3 | (fetch() & 0xff);
                cycles++;
                tick();
                push();
                state.PC = addr;
            }
            break;

            case 0x15: { // DIS I
                state.externalInterruptsEnabled = false;
            }
            break;

            case 0x16: { // JTF addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.TF) {
                    state.TF = false;
                    state.PC = addr;
                }
            }
            break;

            case 0x17: { // INC A
                state.A = (state.A + 1) & 0xff;
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
                int tmp = state.A;
                state.A = ram.read(pos);
                ram.write(pos, tmp);
            }
            break;

            case 0x23: { // MOV A, #data
                int data = fetch();
                cycles++;
                tick();
                state.A = data & 0xff;
            }
            break;

            case 0x25: { // EN TCNTI
                state.tcntInterruptsEnabled = true;
            }
            break;

            case 0x26: { // JNT0 addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (!state.T0) {
                    state.PC = addr;
                }
            }
            break;

            case 0x27: { // CLR A
                state.A = 0;
            }
            break;

            case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f: {
                // XCH A, Rr
                int r  = op & 0x7;
                int tmp = readReg(r);
                writeReg(r, state.A);
                state.A = tmp;
            }
            break;

            case 0x30: case 0x31: { // XCHD A, @R
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                int tmp = ram.read(pos) & 0xf;
                ram.write(pos, ram.read(pos) & 0xf0 | state.A & 0x0f);
                state.A = state.A & 0xf0 | tmp;
            }
            break;

            case 0x35: { // DIS TCNTI
                state.tcntInterruptsEnabled = false;
                state.timerInterruptRequested = false;
            }
            break;


            case 0x36: { // JT0 addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.T0) {
                    state.PC = addr;
                }
            }
            break;

            case 0x37: { // CPL A
                state.A = ~state.A & 0xff;
            }
            break;


            case 0x39: case 0x3a: { // OUTL Pp, A
                cycles++;
                tick();
                int p = op & 0x1;
                ports[p].write(state.A);
            }
            break;

            case 0x3c: case 0x3d: case 0x3e: case 0x3f: {
                // MOVD Pp, A
                cycles++;
                tick();

                int p = op & 0x3;
                int nibble = 0b0100 | p; // WRITE port p

                pinPROG.write(1);
                ports[2].write(nibble, 0x0f);
                pinPROG.write(0); // Address is valid

                ports[2].write(state.A & 0xf, 0x0f);
                pinPROG.write(1); // Data is valid
            }
            break;

            case 0x40: case 0x41: { // ORL A, @Rr
                int pos = readReg(op & 0x1) & 0x7f;
                state.A |= ram.read(pos);
            }
            break;

            case 0x42: { // MOV A, T
                state.A = state.T;
            }
            break;

            case 0x43: { // ORL A, #data
                int data = fetch();
                cycles++;
                tick();
                state.A |= data & 0xff;
            }
            break;

            case 0x45: { // STRT CNT
                state.counterRunning = true;
                state.timerRunning = false;
            }
            break;

            case 0x46: { // JNT1 addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (!state.T1) {
                    state.PC = addr;
                }
            }
            break;

            case 0x47: { // SWAP A
                int hiNibble = state.A & 0xf0;
                int loNibble = state.A & 0x0f;
                state.A = (loNibble << 4) | (hiNibble >> 4);
            }
            break;

            case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f: {
                // ORL A, Rr
                int r = op & 0x7;
                state.A |= readReg(r);
            }
            break;

            case 0x50: case 0x51: { // ANL A, @Rr
                int pos = readReg(op & 0x1) & 0x7f;
                state.A &= ram.read(pos);
            }
            break;

            case 0x53: { // ANL A, #data
                int data = fetch();
                cycles++;
                tick();
                state.A &= data & 0xff;
            }
            break;

            case 0x55: { // STRT T
                state.counterRunning = false;
                state.timerRunning = true;
                state.cyclesUntilCount = 32;
            }
            break;

            case 0x56: { // JT1 addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.T1) {
                    state.PC = addr;
                }
            }
            break;

            case 0x57: { // DA A
                if ( (state.A & 0x0f) > 9 || getBit(state.PSW, AC_BIT) > 0) {
                    state.A += 9;
                }
                int hiNibble = (state.A & 0xf0) >> 4;
                if (hiNibble > 9 || getBit(state.PSW, CY_BIT) > 0) {
                    hiNibble += 6;
                }
                state.A = (hiNibble << 4 | (state.A & 0xf)) &0xff;
                setCarry(hiNibble > 15);
            }
            break;

            case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f: {
                // ANL A, Rr
                int r = op & 0x7;
                state.A &= readReg(r);
            }

            case 0x60: case 0x61: { // ADD A, @Rr
                int pos = readReg(op & 0x1);
                addToAcc(ram.read(pos));
            }
            break;

            case 0x62: { // MOV T, A
                state.T = state.A;
            }
            break;

            case 0x65: { // STOP TCNT
                state.timerRunning = false;
                state.counterRunning = false;
            }
            break;

            case 0x67: { // RRC A
                int newCarry = state.A & 1;
                state.A = state.A >> 1;
                state.A = setBit(state.A, 7, getBit(state.PSW, CY_BIT));
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
                int carry = getBit(state.PSW, CY_BIT);
                addToAcc(carry + ram.read(pos));
            }
            break;

            case 0x75: { // ENT0 CLK
                throw new IllegalStateException("ENT0 CLK is not implemented");
            }

            case 0x76: { // JF1 addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.F1 != 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0x77: { // RR A
                int a0 = state.A & 1;
                state.A = state.A >> 1;
                state.A = setBit(state.A, 7, a0);
            }
            break;

            case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f: {
                // ADDC A, Rr
                int carry = getBit(state.PSW, CY_BIT);
                addToAcc(carry + readReg(op & 0x7));
            }
            break;

            case 0x80: case 0x81: { // MOVX A, @Rr
                int pos = readReg(op & 0x1);
                pinALE.write(1);
                ports[0].write(pos); // emit address to read from
                pinALE.write(0);
                cycles++;
                tick();

                pinRDLowActive.write(0);
                // Now the 8155 will write data to the bus
                pinRDLowActive.write(1);
                state.A = ports[0].read();
            }
            break;

            case 0x83: { // RET
                cycles++;
                tick();
                pop(false);
            }
            break;

            case 0x85: { // CLR F0
                state.PSW = setBit(state.PSW, F0_BIT, 0);
            }
            break;

            case 0x86: { // JNI addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.notINT) {
                    state.PC = addr;
                }
            }
            break;

            case 0x88: { // ORL BUS, #data
                cycles++;
                tick();
                int data = fetch();
                int bus = ports[0].read();
                ports[0].write((bus | data) & 0xff);
            }
            break;

            case 0x89: case 0x8a: { // ORL Pp, #data
                int p = op & 0x3;
                cycles++;
                tick();
                int data = fetch();
                ports[p].write((ports[p].read() | data) & 0xff);
            }
            break;

            case 0x8c: case 0x8d: case 0x8e: case 0x8f: { // ORLD Pp, A
                int p = op & 0x3;
                cycles++;
                tick();

                int nibble = 0b1000 | p; // OR port p

                pinPROG.write(1);
                ports[2].write(nibble, 0xf);
                pinPROG.write(0); // Address is valid

                ports[2].write(state.A & 0xf, 0xf);
                pinPROG.write(1); // Data is valid
            }
            break;

            case 0x90: case 0x91: { // MOVX @R, A
                int pos = readReg(op & 0x1);
                pinALE.write(1);
                ports[0].write(pos); // emit address to write to
                pinALE.write(0);
                cycles++;
                tick();
                pinWRLowActive.write(0);
                ports[0].write(state.A);
                pinWRLowActive.write(1);
            }
            break;

            case 0x93: { // RETR
                cycles++;
                tick();
                pop(true);
            }
            break;

            case 0x95: { // CPL F0
                state.PSW = setBit(state.PSW, F0_BIT, 1 - getBit(state.PSW, F0_BIT));
            }
            break;

            case 0x96: { // JNZ addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.A != 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0x97: { // CLR C
                state.PSW = setBit(state.PSW, CY_BIT, 0);
            }
            break;

            case 0x98: { // ANL BUS, #data
                cycles++;
                tick();
                int data = fetch();
                int bus = ports[0].read();
                ports[0].write((bus & data) & 0xff);
            }
            break;

            case 0x99: case 0x9a: { // ANL Pp, #data
                int p = op & 0x3;
                cycles++;
                tick();
                int data = fetch();
                ports[p].write((ports[p].read() & data) & 0xff);
            }
            break;

            case 0x9c: case 0x9d: case 0x9e: case 0x9f: { // ANLD Pp, A
                int p = op & 0x3;
                cycles++;
                tick();

                int nibble = 0b1100 | p; // OR port p

                pinPROG.write(1);
                ports[2].write(nibble, 0xf);
                pinPROG.write(0); // Address is valid

                ports[2].write(state.A & 0xf, 0xf);
                pinPROG.write(1); // Data is valid
            }
            break;

            case 0xa0: case 0xa1: { // MOV @Rr, A
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                ram.write(pos, state.A);
            }
            break;

            case 0xa3: { // MOVP A, @A
                cycles++;
                tick();
                int pos = (state.PC & 0xf00) | (state.A & 0xff);
                state.A = rom.read(pos);
            }
            break;

            case 0xa5: { // CLR F1
                state.F1 = 0;
            }
            break;

            case 0xa7: { // CPL C
                state.PSW = setBit(state.PSW, CY_BIT, 1 - getBit(state.PSW, CY_BIT));
            }
            break;

            case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf: {
                // MOV Rr, A
                writeReg(op & 0x7, state.A);
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
                state.PC = (state.PC & 0xff00 ) | rom.read((state.PC & 0xff00) | (state.A & 0xff));
            }
            break;

            case 0xb5: { // CPL F1
                state.F1 = 1 - state.F1;
            }
            break;

            case 0xb6: { // JF0 addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (getBit(state.PSW, F0_BIT) > 0) {
                    state.PC = addr;
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
                state.PSW = setBit(state.PSW, BS_BIT, 0);
            }
            break;

            case 0xc6: { // JZ addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (state.A == 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0xc7: { // MOV A, PSW
                state.A = state.PSW;
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
                state.A = (state.A ^ ram.read(pos)) & 0xff;
            }
            break;

            case 0xd3:{ // XRL A, #data
                int data = fetch();
                cycles++;
                tick();
                state.A ^= data & 0xff;
            }
            break;

            case 0xd5: { // SEL RB1
                state.PSW = setBit(state.PSW, BS_BIT, 1);
            }
            break;

            case 0xd7: { // MOV PSW, A
                state.PSW = state.A;
            }
            break;

            case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf: {
                // XRL A, Rr
                int r = op & 0x7;
                state.A ^= readReg(r);
            }
            break;

            case 0xe3: { // MOVP3 A, @A
                cycles++;
                tick();
                int pos = 0x300 | (state.A & 0xff);
                state.A = rom.read(pos);
            }
            break;

            case 0xe5: { // SEL MB0
                state.DBF = 0;
            }
            break;

            case 0xe6: { // JNC addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (getBit(state.PSW, CY_BIT) == 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0xe7: { // RL A
                state.A = (state.A << 1) & 0xff | ((state.A & 0x80) >> 7);
            }
            break;

            case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef: {
                // DJNZ Rr, addr
                int r = op & 0x7;
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                int val = readReg(r) - 1;
                writeReg(r, val);
                if (val != 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0xf0: case 0xf1: { // MOV A, @Rr
                int r = op & 0x1;
                int pos = readReg(r) & 0x7f;
                state.A = ram.read(pos);
            }
            break;

            case 0xf5: { // SEL MB1
                state.DBF = 1;
            }
            break;

            case 0xf6: { // JC addr
                int addr = fetch();
                addr = ( (state.PC-1) & 0xf00) | (addr & 0xff);
                cycles++;
                tick();
                if (getBit(state.PSW, CY_BIT) != 0) {
                    state.PC = addr;
                }
            }
            break;

            case 0xf7: { // RLC A
                int newCarry = state.A & 0x80;
                state.A = (state.A << 1) & 0xff;
                if (getBit(state.PSW, CY_BIT) > 0) {
                    state.A |= 1;
                }
                setCarry(newCarry > 0);
            }
            break;

            case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff: {
                // MOV A, Rr
                state.A = readReg(op & 0x7);
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
                logger.info(String.format("Illegal op-code 0x%02x at %04x", op, state.PC - 1));
                break;
        }
        handleInterrupts();
        listeners.forEach(StateListener::instructionExecuted);
        return cycles;
    }

    public void execute(int cycles) {
        while (cycles > 0) {
            cycles -= executeSingleInstr();
        }
    }
}

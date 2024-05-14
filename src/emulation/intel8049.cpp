#include "intel8049.h"

#include <iostream>
#include "fmt/format.h"

#include <QDebug>

namespace kosmos_cp1::emulation {

void Intel8049::reset() {
    state_.tf = false;
    state_.notInt = true;
    state_.t0 = false;
    state_.t1 = false;
    state_.timerInterruptRequested = false;

    // T is not affected by reset()
    state_.a = 0;
    state_.pc = 0;
    state_.psw = 0x8; // bit 3 is always rom.read as "1"
    state_.dbf = 0; // memory bank 0
    state_.f1 = 0;
    state_.externalInterruptsEnabled = false;
    state_.tcntInterruptsEnabled = false;
    state_.counterRunning = false;
    state_.timerRunning = false;
    state_.inInterrupt = false;
    state_.cyclesUntilCount = 0;

    ports_[0]->write(0x00);
    ports_[1]->write(0xff);
    ports_[2]->write(0xff);

    for (int i = 0; i < ram_.size(); i++) {
        ram_[i] = 0x00;
    }

    emit resetExecuted();
}

int Intel8049::executeSingleInstr() {
//    Disassembler::Line l = disassembler_.disassembleSingleLine(state_.pc);
//    qDebug() << fmt::format("${:04x}", state_.pc).c_str() << ":" << l.disassembly().c_str() << state_.toString().c_str();

    int cycles = 1;
    uint8_t op = fetch();
    tick();
    switch(op) {
    case 0x00: // NOP
        break;

    case 0x02: { // OUTL BUS, A
        cycles++;
        tick();
        ports_[0]->write(state_.a);
    }
    break;

    case 0x03: { // ADD A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        addToAcc(data);
    }
    break;

    case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4: {
        // JMP addr
        uint16_t addr = (state_.dbf << 11 ) | (op & 0xe0) << 3 | fetch();
        cycles++;
        tick();
        state_.pc = addr;
    }
    break;

    case 0x05: { // EN I
        state_.externalInterruptsEnabled = true;
    }
    break;

    case 0x07: { // DEC A
        state_.a--;
    }
    break;

    case 0x08: { // INS A, BUS
        cycles++;
        tick();
        state_.a = ports_[0]->read();
    }
    break;

    case 0x09: case 0x0a: { // IN A, Pp
        cycles++;
        tick();
        int p = op & 0x1;
        state_.a = ports_[p]->read();
    }
    break;

    case 0x0c: case 0x0d: case 0x0e: case 0x0f: {
        // MOVD A, Pp
        cycles++;
        tick();
        int p = op & 0x3;
        int nibble = 0b0000 | p; // READ port p

        emit pinPROGWritten(1);
        ports_[2]->write(nibble, 0x0f);
        emit pinPROGWritten(0); // Address is valid

        state_.a = ports_[2]->read() & 0xf;
        emit pinPROGWritten(1);
    }
    break;

    case 0x10: case 0x11: { // INC @Rr
        uint8_t r = op & 0x1;
        uint16_t pos = readReg(r) & 0x7f;
        ram_[pos]++;
    }
    break;

    case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2: {
        // JBb addr
        uint8_t b = fetch();
        uint16_t addr = ( (state_.pc-1) & 0xf00) | b;
        cycles++;
        tick();
        b = (op >> 5) & 0x7;
        if ((state_.a & 1<<b) > 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0x13: { // ADDC A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        uint8_t carry = (state_.psw & (1<<CY_BIT)) ? 1 : 0;
        addToAcc(carry + data);
    }
    break;

    case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4: {
        // CALL addr
        int addr = (state_.dbf << 11) | (op & 0xe0) << 3 | fetch();
        cycles++;
        tick();
        push();
        state_.pc = addr;
    }
    break;

    case 0x15: { // DIS I
        state_.externalInterruptsEnabled = false;
    }
    break;

    case 0x16: { // JTF addr
        uint8_t b = fetch();
        uint16_t addr = ( (state_.pc-1) & 0xf00) | b;
        cycles++;
        tick();
        if (state_.tf) {
            state_.tf = false;
            state_.pc = addr;
        }
    }
    break;

    case 0x17: { // INC A
        state_.a++;
    }
    break;

    case 0x18: case 0x19: case 0x1a: case 0x1b: case 0x1c: case 0x1d: case 0x1e: case 0x1f: {
        // INC Rr
        uint8_t r = op & 0x7;
        writeReg(r, readReg(r) + 1);
    }
    break;

    case 0x20: case 0x21: { // XCH A, @Rr
        uint8_t pos = readReg(op & 0x1);
        uint8_t tmp = state_.a;
        state_.a = ram_[pos];
        ram_[pos] = tmp;
    }
    break;

    case 0x23: { // MOV A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        state_.a = data;
    }
    break;

    case 0x25: { // EN TCNTI
        state_.tcntInterruptsEnabled = true;
    }
    break;

    case 0x26: { // JNT0 addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (!state_.t0) {
            state_.pc = addr;
        }
    }
    break;

    case 0x27: { // CLR A
        state_.a = 0;
    }
    break;

    case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f: {
        // XCH A, Rr
        uint8_t r  = op & 0x7;
        uint8_t tmp = readReg(r);
        writeReg(r, state_.a);
        state_.a = tmp;
    }
    break;

    case 0x30: case 0x31: { // XCHD A, @R
        uint8_t r = op & 0x1;
        uint8_t pos = readReg(r) & 0x7f;
        uint8_t tmp = ram_[pos] & 0xf;
        ram_[pos] = ram_[pos] & 0xf0 | state_.a & 0x0f;
        state_.a = state_.a & 0xf0 | tmp;
    }
    break;

    case 0x35: { // DIS TCNTI
        state_.tcntInterruptsEnabled = false;
        state_.timerInterruptRequested = false;
    }
    break;


    case 0x36: { // JT0 addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (state_.t0) {
            state_.pc = addr;
        }
    }
    break;

    case 0x37: { // CPL A
        state_.a = ~state_.a;
    }
    break;


    case 0x39: case 0x3a: { // OUTL Pp, A
        cycles++;
        tick();
        uint8_t p = op & 0x1;
        ports_[p]->write(state_.a);
    }
    break;

    case 0x3c: case 0x3d: case 0x3e: case 0x3f: {
        // MOVD Pp, A
        cycles++;
        tick();

        uint8_t p = op & 0x3;
        uint8_t nibble = 0b0100 | p; // WRITE port p

        emit pinPROGWritten(1);
        ports_[2]->write(nibble, 0x0f);
        emit pinPROGWritten(0); // Address is valid

        ports_[2]->write(state_.a & 0xf, 0x0f);
        emit pinPROGWritten(1); // Data is valid
    }
    break;

    case 0x40: case 0x41: { // ORL A, @Rr
        uint16_t pos = readReg(op & 0x1) & 0x7f;
        state_.a |= ram_[pos];
    }
    break;

    case 0x42: { // MOV A, T
        state_.a = state_.t;
    }
    break;

    case 0x43: { // ORL A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        state_.a |= data;
    }
    break;

    case 0x45: { // STRT CNT
        state_.counterRunning = true;
        state_.timerRunning = false;
    }
    break;

    case 0x46: { // JNT1 addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (!state_.t1) {
            state_.pc = addr;
        }
    }
    break;

    case 0x47: { // SWAP A
        int hiNibble = state_.a & 0xf0;
        int loNibble = state_.a & 0x0f;
        state_.a = (loNibble << 4) | (hiNibble >> 4);
    }
    break;

    case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f: {
        // ORL A, Rr
        uint8_t r = op & 0x7;
        state_.a |= readReg(r);
    }
    break;

    case 0x50: case 0x51: { // ANL A, @Rr
        uint8_t pos = readReg(op & 0x1) & 0x7f;
        state_.a &= ram_[pos];
    }
    break;

    case 0x53: { // ANL A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        state_.a &= data;
    }
    break;

    case 0x55: { // STRT T
        state_.counterRunning = false;
        state_.timerRunning = true;
        state_.cyclesUntilCount = 32;
    }
    break;

    case 0x56: { // JT1 addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (state_.t1) {
            state_.pc = addr;
        }
    }
    break;

    case 0x57: { // DA A
        if ( (state_.a & 0x0f) > 9 || getBit(state_.psw, AC_BIT) > 0) {
            state_.a += 9;
        }
        uint8_t hiNibble = (state_.a & 0xf0) >> 4;
        if (hiNibble > 9 || getBit(state_.psw, CY_BIT) > 0) {
            hiNibble += 6;
        }
        state_.a = (hiNibble << 4) | (state_.a & 0xf);
        setCarry(hiNibble > 15);
    }
    break;

    case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f: {
        // ANL A, Rr
        uint8_t r = op & 0x7;
        state_.a &= readReg(r);
    }

    case 0x60: case 0x61: { // ADD A, @Rr
        uint8_t pos = readReg(op & 0x1);
        addToAcc(ram_[pos]);
    }
    break;

    case 0x62: { // MOV T, A
        state_.t = state_.a;
    }
    break;

    case 0x65: { // STOP TCNT
        state_.timerRunning = false;
        state_.counterRunning = false;
    }
    break;

    case 0x67: { // RRC A
        uint8_t newCarry = state_.a & 1;
        state_.a = state_.a >> 1;
        state_.a = setBit(state_.a, 7, getBit(state_.psw, CY_BIT));
        setCarry(newCarry > 0);
    }
    break;

    case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f: {
        // ADD A, Rr
        addToAcc(readReg(op & 0x7));
    }
    break;

    case 0x70: case 0x71: { // ADDC A, @Rr
        uint8_t pos = readReg(op & 0x1) & 0x7f;
        uint8_t carry = getBit(state_.psw, CY_BIT);
        addToAcc(carry + ram_[pos]);
    }
    break;

    case 0x75: { // ENT0 CLK
        std::cerr << "ENT0 CLK is not implemented" << std::endl;
    }

    case 0x76: { // JF1 addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (state_.f1 != 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0x77: { // RR A
        uint8_t a0 = state_.a & 1;
        state_.a >>= 1;
        state_.a = setBit(state_.a, 7, a0);
    }
    break;

    case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f: {
        // ADDC A, Rr
        uint8_t carry = getBit(state_.psw, CY_BIT);
        addToAcc(carry + readReg(op & 0x7));
    }
    break;

    case 0x80: case 0x81: { // MOVX A, @Rr
        uint8_t pos = readReg(op & 0x1);
        emit pinALEWritten(1);
        ports_[0]->write(pos); // emit address to read from
        emit pinALEWritten(0);
        cycles++;
        tick();

        emit pinRDLowActiveWritten(0);
        // Now the 8155 will write data to the bus
        emit pinRDLowActiveWritten(1);
        state_.a = ports_[0]->read();
    }
    break;

    case 0x83: { // RET
        cycles++;
        tick();
        pop(false);
    }
    break;

    case 0x85: { // CLR F0
        state_.psw = setBit(state_.psw, F0_BIT, 0);
    }
    break;

    case 0x86: { // JNI addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (state_.notInt) {
            state_.pc = addr;
        }
    }
    break;

    case 0x88: { // ORL BUS, #data
        cycles++;
        tick();
        uint8_t data = fetch();
        int bus = ports_[0]->read();
        ports_[0]->write((bus | data) & 0xff);
    }
    break;

    case 0x89: case 0x8a: { // ORL Pp, #data
        int p = op & 0x3;
        cycles++;
        tick();
        uint8_t data = fetch();
        ports_[p]->write(ports_[p]->read() | data);
    }
    break;

    case 0x8c: case 0x8d: case 0x8e: case 0x8f: { // ORLD Pp, A
        uint8_t p = op & 0x3;
        cycles++;
        tick();

        int nibble = 0b1000 | p; // OR port p

        emit pinPROGWritten(1);
        ports_[2]->write(nibble, 0xf);
        emit pinPROGWritten(0);// Address is valid

        ports_[2]->write(state_.a & 0xf, 0xf);
        emit pinPROGWritten(1); // Data is valid
    }
    break;

    case 0x90: case 0x91: { // MOVX @R, A
        uint8_t pos = readReg(op & 0x1);
        emit pinALEWritten(1);
        ports_[0]->write(pos); // emit address to write to
        emit pinALEWritten(0);
        cycles++;
        tick();
        emit pinWRLowActiveWritten(0);
        ports_[0]->write(state_.a);
        emit pinWRLowActiveWritten(1);
    }
    break;

    case 0x93: { // RETR
        cycles++;
        tick();
        pop(true);
    }
    break;

    case 0x95: { // CPL F0
        state_.psw = setBit(state_.psw, F0_BIT, 1 - getBit(state_.psw, F0_BIT));
    }
    break;

    case 0x96: { // JNZ addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (state_.a != 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0x97: { // CLR C
        state_.psw = setBit(state_.psw, CY_BIT, 0);
    }
    break;

    case 0x98: { // ANL BUS, #data
        cycles++;
        tick();
        uint8_t data = fetch();
        uint8_t bus = ports_[0]->read();
        ports_[0]->write(bus & data);
    }
    break;

    case 0x99: case 0x9a: { // ANL Pp, #data
        uint8_t p = op & 0x3;
        cycles++;
        tick();
        uint8_t data = fetch();
        ports_[p]->write(ports_[p]->read() & data);
    }
    break;

    case 0x9c: case 0x9d: case 0x9e: case 0x9f: { // ANLD Pp, A
        uint8_t p = op & 0x3;
        cycles++;
        tick();

        uint8_t nibble = 0b1100 | p; // OR port p

        emit pinPROGWritten(1);
        ports_[2]->write(nibble, 0xf);
        emit pinPROGWritten(0); // Address is valid

        ports_[2]->write(state_.a & 0xf, 0xf);
        emit pinPROGWritten(1); // Data is valid
    }
    break;

    case 0xa0: case 0xa1: { // MOV @Rr, A
        uint8_t r = op & 0x1;
        uint8_t pos = readReg(r) & 0x7f;
        ram_[pos] = state_.a;
    }
    break;

    case 0xa3: { // MOVP A, @A
        cycles++;
        tick();
        uint16_t pos = (state_.pc & 0xf00) | state_.a;
        state_.a = rom_[pos];
    }
    break;

    case 0xa5: { // CLR F1
        state_.f1 = 0;
    }
    break;

    case 0xa7: { // CPL C
        state_.psw = setBit(state_.psw, CY_BIT, 1 - getBit(state_.psw, CY_BIT));
    }
    break;

    case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf: {
        // MOV Rr, A
        writeReg(op & 0x7, state_.a);
    }
    break;

    case 0xb0: case 0xb1: { // MOV @Rr, #data
        uint8_t r = op & 0x1;
        uint8_t data = fetch();
        cycles++;
        tick();
        ram_[readReg(r)] = data;
    }
    break;

    case 0xb3: { // JMPP @A
        cycles++;
        tick();
        state_.pc = (state_.pc & 0xff00 ) | rom_[state_.pc & 0xff00] | state_.a;
    }
    break;

    case 0xb5: { // CPL F1
        state_.f1 = 1 - state_.f1;
    }
    break;

    case 0xb6: { // JF0 addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | addr & 0xff;
        cycles++;
        tick();
        if (getBit(state_.psw, F0_BIT) > 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0xb8: case 0xb9: case 0xba: case 0xbb: case 0xbc: case 0xbd: case 0xbe: case 0xbf: {
        // MOV Rr, #data
        uint8_t r = op & 0x7;
        uint8_t data = fetch();
        cycles++;
        tick();
        writeReg(r, data);
    }
    break;

    case 0xc5: { // SEL RB0
        state_.psw = setBit(state_.psw, BS_BIT, 0);
    }
    break;

    case 0xc6: { // JZ addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        if (state_.a == 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0xc7: { // MOV A, psw
        state_.a = state_.psw;
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
        state_.a = state_.a ^ ram_[pos];
    }
    break;

    case 0xd3:{ // XRL A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        state_.a ^= data;
    }
    break;

    case 0xd5: { // SEL RB1
        state_.psw = setBit(state_.psw, BS_BIT, 1);
    }
    break;

    case 0xd7: { // MOV psw, A
        state_.psw = state_.a;
    }
    break;

    case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf: {
        // XRL A, Rr
        int r = op & 0x7;
        state_.a ^= readReg(r);
    }
    break;

    case 0xe3: { // MOVP3 A, @A
        cycles++;
        tick();
        uint16_t pos = 0x300 | state_.a;
        state_.a = rom_[pos];
    }
    break;

    case 0xe5: { // SEL MB0
        state_.dbf = 0;
    }
    break;

    case 0xe6: { // JNC addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        if (getBit(state_.psw, CY_BIT) == 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0xe7: { // RL A
        state_.a = state_.a << 1 | ((state_.a & 0x80) >> 7);
    }
    break;

    case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef: {
        // DJNZ Rr, addr
        uint8_t r = op & 0x7;
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        int val = readReg(r) - 1;
        writeReg(r, val);
        if (val != 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0xf0: case 0xf1: { // MOV A, @Rr
        uint8_t r = op & 0x1;
        uint8_t pos = readReg(r) & 0x7f;
        state_.a = ram_[pos];
    }
    break;

    case 0xf5: { // SEL MB1
        state_.dbf = 1;
    }
    break;

    case 0xf6: { // JC addr
        uint16_t addr = fetch();
        addr = ( (state_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        if (getBit(state_.psw, CY_BIT) != 0) {
            state_.pc = addr;
        }
    }
    break;

    case 0xf7: { // RLC A
        uint8_t newCarry = state_.a & 0x80;
        state_.a = (state_.a << 1) & 0xff;
        if (getBit(state_.psw, CY_BIT) > 0) {
            state_.a |= 1;
        }
        setCarry(newCarry > 0);
    }
    break;

    case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff: {
        // MOV A, Rr
        state_.a = readReg(op & 0x7);
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
        std::cerr << fmt::format("Illegal op-code 0x{:02x} at {:04x}", op, state_.pc - 1) << std::endl;
        break;
    }
    handleInterrupts();
    emit instructionExecuted();
    return cycles;
}

void Intel8049::execute(int cycles) {
    while (cycles > 0) {
        cycles -= executeSingleInstr();
    }
}

uint8_t Intel8049::readReg(uint8_t reg) {
    uint16_t base = (state_.psw & (1<<BS_BIT)) == 0 ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
    return ram_[base + reg];
}

void Intel8049::writeReg(uint8_t reg, uint8_t val) {
    uint16_t base = (state_.psw & (1<<BS_BIT)) == 0 ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
    ram_[base + reg] = val;
}

uint8_t Intel8049::fetch() {
    if (state_.pc >= rom_.size()) {
        std::cerr << fmt::format("PC {:04x} out of range!", state_.pc) << std::endl;
        abort();
    }
    return rom_[state_.pc++];
}

void Intel8049::push() {
    uint8_t sp = state_.psw & 0x7;
    ram_[8+2*sp] = static_cast<uint8_t>(state_.pc & 0xff);
    ram_[9+2*sp] = static_cast<uint8_t>(state_.psw & 0xf0 | (state_.pc >> 8) & 0xff );
    state_.psw = static_cast<uint8_t>(state_.psw & 0xf8 | (sp + 1) & 0x7);
}

void Intel8049::pop(bool restoreState) {
    uint8_t sp = (state_.psw - 1) & 0x7;
    state_.pc = static_cast<uint16_t>(ram_[9+2*sp] & 0xf) << 8 | static_cast<uint16_t>(ram_[8+2*sp]);
    if (restoreState) {
        state_.psw = static_cast<uint8_t>(ram_[9+2*sp] & 0xf0 | 0x8 | (sp & 0x7));
        state_.inInterrupt = false;
    } else {
        state_.psw = state_.psw & 0xf0 | 0x8 | (sp & 0x7);
    }
}

void Intel8049::incCounter() {
    state_.tf = state_.tf || state_.t == 0xff;
    state_.timerInterruptRequested = state_.timerInterruptRequested || state_.t == 0xff;
    state_.t++;
}

void Intel8049::tick() {
    // "executes" another cycle, and performs some periodic stuff (e.g. counting after a STRT T)
    if (state_.timerRunning) {
        state_.cyclesUntilCount--;
        if (state_.cyclesUntilCount == 0) {
            state_.cyclesUntilCount = 32;
            incCounter();
        }
    }
}

uint8_t Intel8049::getBit(uint16_t val, uint8_t bit) {
    uint16_t mask = 1 << bit;
    return (val & mask) > 0 ? 1 : 0;
}

uint16_t Intel8049::setBit(uint16_t val, uint8_t bit, uint8_t bitVal) {
    uint16_t mask = 1 << bit;
    val &= ~mask;
    val |= bitVal * mask;
    return val;
}

void Intel8049::setCarry(bool carry) {
    state_.psw = setBit(state_.psw, CY_BIT, carry ? 1 : 0);
}

void Intel8049::setAuxCarry(bool carry) {
    state_.psw = setBit(state_.psw, AC_BIT, carry ? 1 : 0);
}

void Intel8049::addToAcc(uint8_t value) {
    uint8_t oldA = state_.a;
    uint8_t oldLoNibble = state_.a & 0xf;
    state_.a = state_.a + value;
    uint8_t loNibble = state_.a & 0xf;
    setCarry(oldA > state_.a);
    setAuxCarry(oldLoNibble > loNibble);
}

void Intel8049::handleInterrupts() {
    if (!state_.inInterrupt) {
        // not handling an interrupt, so let's check if we need to.
        if (!state_.notInt && state_.externalInterruptsEnabled) {
            // handle external interrupt
            push();
            state_.pc = 3;
            state_.inInterrupt = true;
        } else if (state_.timerInterruptRequested && state_.tcntInterruptsEnabled) {
            // handle timer interrupt
            state_.timerInterruptRequested = false;
            push();
            state_.pc = 7;
            state_.inInterrupt = true;
        }
    }
}

} // namespace kosmos_cp1::emulation

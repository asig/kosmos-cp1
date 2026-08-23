/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
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

#include "intel8049.h"

#include <iostream>
#include "fmt/format.h"

#include <QDebug>

namespace kosmos_cp1::emulation {

void Intel8049::reset() {
    tf_ = false;
    notInt_ = true;
    t0_ = false;
    t1_ = false;
    timerInterruptRequested_ = false;

    // T is not affected by reset()
    regs_.a = 0;
    regs_.pc = 0;
    regs_.psw = 0x8; // bit 3 is always rom.read as "1"
    regs_.dbf = 0; // memory bank 0
    regs_.f1 = 0;
    externalInterruptsEnabled_ = false;
    tcntInterruptsEnabled_ = false;
    counterRunning_ = false;
    timerRunning_ = false;
    inInterrupt_ = false;
    cyclesUntilCount_ = 0;

    ports_[0]->write(0x00);
    ports_[1]->write(0xff);
    ports_[2]->write(0xff);

    ram_.clear();

    emit resetExecuted();
}

int Intel8049::executeSingleInstr() {
    int cycles = 1;
    uint8_t op = fetch();
    tick();
    switch(op) {
    case 0x00: // NOP
        break;

    case 0x02: { // OUTL BUS, A
        cycles++;
        tick();
        ports_[0]->write(regs_.a);
    }
    break;

    case 0x03: { // ADD A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        addToAcc(data, false);
    }
    break;

    case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4: {
        // JMP addr
        uint16_t addr = (regs_.dbf << 11 ) | (op & 0xe0) << 3 | fetch();
        cycles++;
        tick();
        regs_.pc = addr;
    }
    break;

    case 0x05: { // EN I
        externalInterruptsEnabled_ = true;
    }
    break;

    case 0x07: { // DEC A
        regs_.a--;
    }
    break;

    case 0x08: { // INS A, BUS
        cycles++;
        tick();
        regs_.a = ports_[0]->read();
    }
    break;

    case 0x09: case 0x0a: { // IN A, Pp
        cycles++;
        tick();
        int p = op & 0x1;
        regs_.a = ports_[p]->read();
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

        regs_.a = ports_[2]->read() & 0xf;
        emit pinPROGWritten(1);
    }
    break;

    case 0x10: case 0x11: { // INC @Rr
        uint8_t r = op & 0x1;
        uint16_t pos = readReg(r) & 0x7f;
        ram_.write(pos, ram_.read(pos)+1);
    }
    break;

    case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2: {
        // JBb addr
        uint8_t b = fetch();
        uint16_t addr = ( (regs_.pc-1) & 0xf00) | b;
        cycles++;
        tick();
        b = (op >> 5) & 0x7;
        if ((regs_.a & 1<<b) > 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x13: { // ADDC A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        bool carry = getBit(regs_.psw, CY_BIT);
        addToAcc(data, carry);
    }
    break;

    case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4: {
        // CALL addr
        int addr = (regs_.dbf << 11) | (op & 0xe0) << 3 | fetch();
        cycles++;
        tick();
        push();
        regs_.pc = addr;
    }
    break;

    case 0x15: { // DIS I
        externalInterruptsEnabled_ = false;
    }
    break;

    case 0x16: { // JTF addr
        uint8_t b = fetch();
        uint16_t addr = ( (regs_.pc-1) & 0xf00) | b;
        cycles++;
        tick();
        if (tf_) {
            tf_ = false;
            regs_.pc = addr;
        }
    }
    break;

    case 0x17: { // INC A
        regs_.a++;
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
        uint8_t tmp = regs_.a;
        regs_.a = ram_.read(pos);
        ram_.write(pos, tmp);
    }
    break;

    case 0x23: { // MOV A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        regs_.a = data;
    }
    break;

    case 0x25: { // EN TCNTI
        tcntInterruptsEnabled_ = true;
    }
    break;

    case 0x26: { // JNT0 addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (!t0_) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x27: { // CLR A
        regs_.a = 0;
    }
    break;

    case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f: {
        // XCH A, Rr
        uint8_t r  = op & 0x7;
        uint8_t tmp = readReg(r);
        writeReg(r, regs_.a);
        regs_.a = tmp;
    }
    break;

    case 0x30: case 0x31: { // XCHD A, @R
        uint8_t r = op & 0x1;
        uint8_t pos = readReg(r) & 0x7f;
        uint8_t tmp = ram_.read(pos) & 0xf;
        ram_.write(pos, ram_.read(pos) & 0xf0 | regs_.a & 0x0f);
        regs_.a = regs_.a & 0xf0 | tmp;
    }
    break;

    case 0x35: { // DIS TCNTI
        tcntInterruptsEnabled_ = false;
        timerInterruptRequested_ = false;
    }
    break;


    case 0x36: { // JT0 addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (t0_) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x37: { // CPL A
        regs_.a = ~regs_.a;
    }
    break;


    case 0x39: case 0x3a: { // OUTL Pp, A
        cycles++;
        tick();
        uint8_t p = op & 0x1;
        ports_[p]->write(regs_.a);
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

        ports_[2]->write(regs_.a & 0xf, 0x0f);
        emit pinPROGWritten(1); // Data is valid
    }
    break;

    case 0x40: case 0x41: { // ORL A, @Rr
        uint16_t pos = readReg(op & 0x1) & 0x7f;
        regs_.a |= ram_.read(pos);
    }
    break;

    case 0x42: { // MOV A, T
        regs_.a = regs_.t;
    }
    break;

    case 0x43: { // ORL A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        regs_.a |= data;
    }
    break;

    case 0x45: { // STRT CNT
        counterRunning_ = true;
        timerRunning_ = false;
    }
    break;

    case 0x46: { // JNT1 addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (!t1_) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x47: { // SWAP A
        int hiNibble = regs_.a & 0xf0;
        int loNibble = regs_.a & 0x0f;
        regs_.a = (loNibble << 4) | (hiNibble >> 4);
    }
    break;

    case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f: {
        // ORL A, Rr
        uint8_t r = op & 0x7;
        regs_.a |= readReg(r);
    }
    break;

    case 0x50: case 0x51: { // ANL A, @Rr
        uint8_t pos = readReg(op & 0x1) & 0x7f;
        regs_.a &= ram_.read(pos);
    }
    break;

    case 0x53: { // ANL A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        regs_.a &= data;
    }
    break;

    case 0x55: { // STRT T
        counterRunning_ = false;
        timerRunning_ = true;
        cyclesUntilCount_ = 32;
    }
    break;

    case 0x56: { // JT1 addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (t1_) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x57: { // DA A
        if ( (regs_.a & 0x0f) > 9 || getBit(regs_.psw, AC_BIT) > 0) {
            regs_.a += 6;
        }
        uint8_t hiNibble = (regs_.a & 0xf0) >> 4;
        if (hiNibble > 9 || getBit(regs_.psw, CY_BIT) > 0) {
            hiNibble += 6;
        }
        regs_.a = (hiNibble << 4) | (regs_.a & 0xf);
        setCarry(hiNibble > 15);
    }
    break;

    case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f: {
        // ANL A, Rr
        uint8_t r = op & 0x7;
        regs_.a &= readReg(r);
    }
    break;

    case 0x60: case 0x61: { // ADD A, @Rr
        uint8_t pos = readReg(op & 0x1);
        addToAcc(ram_.read(pos), false);
    }
    break;

    case 0x62: { // MOV T, A
        regs_.t = regs_.a;
    }
    break;

    case 0x65: { // STOP TCNT
        timerRunning_ = false;
        counterRunning_ = false;
    }
    break;

    case 0x67: { // RRC A
        uint8_t newCarry = regs_.a & 1;
        regs_.a = regs_.a >> 1;
        regs_.a = setBit(regs_.a, 7, getBit(regs_.psw, CY_BIT));
        setCarry(newCarry > 0);
    }
    break;

    case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f: {
        // ADD A, Rr
        addToAcc(readReg(op & 0x7), false);
    }
    break;

    case 0x70: case 0x71: { // ADDC A, @Rr
        uint8_t pos = readReg(op & 0x1) & 0x7f;
        bool carry = getBit(regs_.psw, CY_BIT);
        addToAcc(ram_.read(pos), carry);
    }
    break;

    case 0x75: { // ENT0 CLK
        std::cerr << "ENT0 CLK is not implemented" << std::endl;
    }
    break;

    case 0x76: { // JF1 addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (regs_.f1 != 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x77: { // RR A
        uint8_t a0 = regs_.a & 1;
        regs_.a >>= 1;
        regs_.a = setBit(regs_.a, 7, a0);
    }
    break;

    case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f: {
        // ADDC A, Rr
        bool carry = getBit(regs_.psw, CY_BIT);
        addToAcc(readReg(op & 0x7), carry);
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
        regs_.a = ports_[0]->read();
    }
    break;

    case 0x83: { // RET
        cycles++;
        tick();
        pop(false);
    }
    break;

    case 0x85: { // CLR F0
        regs_.psw = setBit(regs_.psw, F0_BIT, 0);
    }
    break;

    case 0x86: { // JNI addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (notInt_) {
            regs_.pc = addr;
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

        ports_[2]->write(regs_.a & 0xf, 0xf);
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
        ports_[0]->write(regs_.a);
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
        regs_.psw = setBit(regs_.psw, F0_BIT, 1 - getBit(regs_.psw, F0_BIT));
    }
    break;

    case 0x96: { // JNZ addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr;
        cycles++;
        tick();
        if (regs_.a != 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0x97: { // CLR C
        regs_.psw = setBit(regs_.psw, CY_BIT, 0);
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

        ports_[2]->write(regs_.a & 0xf, 0xf);
        emit pinPROGWritten(1); // Data is valid
    }
    break;

    case 0xa0: case 0xa1: { // MOV @Rr, A
        uint8_t r = op & 0x1;
        uint8_t pos = readReg(r) & 0x7f;
        ram_.write(pos, regs_.a);
    }
    break;

    case 0xa3: { // MOVP A, @A
        cycles++;
        tick();
        uint16_t pos = (regs_.pc & 0xf00) | regs_.a;
        regs_.a = rom_[pos];
    }
    break;

    case 0xa5: { // CLR F1
        regs_.f1 = 0;
    }
    break;

    case 0xa7: { // CPL C
        regs_.psw = setBit(regs_.psw, CY_BIT, 1 - getBit(regs_.psw, CY_BIT));
    }
    break;

    case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf: {
        // MOV Rr, A
        writeReg(op & 0x7, regs_.a);
    }
    break;

    case 0xb0: case 0xb1: { // MOV @Rr, #data
        uint8_t r = op & 0x1;
        uint8_t data = fetch();
        cycles++;
        tick();
        ram_.write(readReg(r), data);
    }
    break;

    case 0xb3: { // JMPP @A
        cycles++;
        tick();
        regs_.pc = (regs_.pc & 0xff00 ) | rom_[ (regs_.pc & 0xff00) | regs_.a ];
    }
    break;

    case 0xb5: { // CPL F1
        regs_.f1 = 1 - regs_.f1;
    }
    break;

    case 0xb6: { // JF0 addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | addr & 0xff;
        cycles++;
        tick();
        if (getBit(regs_.psw, F0_BIT) > 0) {
            regs_.pc = addr;
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
        regs_.psw = setBit(regs_.psw, BS_BIT, 0);
    }
    break;

    case 0xc6: { // JZ addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        if (regs_.a == 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0xc7: { // MOV A, psw
        regs_.a = regs_.psw;
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
        regs_.a = regs_.a ^ ram_.read(pos);
    }
    break;

    case 0xd3:{ // XRL A, #data
        uint8_t data = fetch();
        cycles++;
        tick();
        regs_.a ^= data;
    }
    break;

    case 0xd5: { // SEL RB1
        regs_.psw = setBit(regs_.psw, BS_BIT, 1);
    }
    break;

    case 0xd7: { // MOV psw, A
        regs_.psw = regs_.a;
    }
    break;

    case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf: {
        // XRL A, Rr
        int r = op & 0x7;
        regs_.a ^= readReg(r);
    }
    break;

    case 0xe3: { // MOVP3 A, @A
        cycles++;
        tick();
        uint16_t pos = 0x300 | regs_.a;
        regs_.a = rom_[pos];
    }
    break;

    case 0xe5: { // SEL MB0
        regs_.dbf = 0;
    }
    break;

    case 0xe6: { // JNC addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        if (getBit(regs_.psw, CY_BIT) == 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0xe7: { // RL A
        regs_.a = regs_.a << 1 | ((regs_.a & 0x80) >> 7);
    }
    break;

    case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef: {
        // DJNZ Rr, addr
        uint8_t r = op & 0x7;
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        int val = readReg(r) - 1;
        writeReg(r, val);
        if (val != 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0xf0: case 0xf1: { // MOV A, @Rr
        uint8_t r = op & 0x1;
        uint8_t pos = readReg(r) & 0x7f;
        regs_.a = ram_.read(pos);
    }
    break;

    case 0xf5: { // SEL MB1
        regs_.dbf = 1;
    }
    break;

    case 0xf6: { // JC addr
        uint16_t addr = fetch();
        addr = ( (regs_.pc-1) & 0xf00) | (addr & 0xff);
        cycles++;
        tick();
        if (getBit(regs_.psw, CY_BIT) != 0) {
            regs_.pc = addr;
        }
    }
    break;

    case 0xf7: { // RLC A
        uint8_t newCarry = regs_.a & 0x80;
        regs_.a = (regs_.a << 1) & 0xff;
        if (getBit(regs_.psw, CY_BIT) > 0) {
            regs_.a |= 1;
        }
        setCarry(newCarry > 0);
    }
    break;

    case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff: {
        // MOV A, Rr
        regs_.a = readReg(op & 0x7);
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
        std::cerr << fmt::format("Illegal op-code 0x{:02x} at {:04x}", op, regs_.pc - 1) << std::endl;
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
    uint16_t base = (regs_.psw & (1<<BS_BIT)) == 0 ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
    return ram_.read(base + reg);
}

void Intel8049::writeReg(uint8_t reg, uint8_t val) {
    uint16_t base = (regs_.psw & (1<<BS_BIT)) == 0 ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
    ram_.write(base + reg, val);
}

uint8_t Intel8049::fetch() {
    if (regs_.pc >= rom_.size()) {
        std::cerr << fmt::format("PC {:04x} out of range!", regs_.pc) << std::endl;
        abort();
    }
    return rom_[regs_.pc++];
}

void Intel8049::push() {
    uint8_t sp = regs_.psw & 0x7;
    ram_.write(8+2*sp, static_cast<uint8_t>(regs_.pc & 0xff));
    ram_.write(9+2*sp, static_cast<uint8_t>(regs_.psw & 0xf0 | (regs_.pc >> 8) & 0xff ));
    regs_.psw = static_cast<uint8_t>(regs_.psw & 0xf8 | (sp + 1) & 0x7);
}

void Intel8049::pop(bool restoreState) {
    uint8_t sp = (regs_.psw - 1) & 0x7;
    regs_.pc = static_cast<uint16_t>(ram_.read(9+2*sp) & 0xf) << 8 | static_cast<uint16_t>(ram_.read(8+2*sp));
    if (restoreState) {
        regs_.psw = static_cast<uint8_t>(ram_.read(9+2*sp) & 0xf0 | 0x8 | (sp & 0x7));
        inInterrupt_ = false;
    } else {
        regs_.psw = regs_.psw & 0xf0 | 0x8 | (sp & 0x7);
    }
}

void Intel8049::incCounter() {
    tf_ = tf_ || regs_.t == 0xff;
    timerInterruptRequested_ = timerInterruptRequested_ || regs_.t == 0xff;
    regs_.t++;
}

void Intel8049::tick() {
    // "executes" another cycle, and performs some periodic stuff (e.g. counting after a STRT T)
    if (timerRunning_) {
        cyclesUntilCount_--;
        if (cyclesUntilCount_ == 0) {
            cyclesUntilCount_ = 32;
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
    regs_.psw = setBit(regs_.psw, CY_BIT, carry ? 1 : 0);
}

void Intel8049::setAuxCarry(bool carry) {
    regs_.psw = setBit(regs_.psw, AC_BIT, carry ? 1 : 0);
}

void Intel8049::addToAcc(uint8_t value, bool carryIn) {
    uint16_t carry = carryIn ? 1 : 0;

    uint16_t res = regs_.a + value + carry;
    setCarry(res > 0xff);

    uint16_t halfRes = (regs_.a & 0x0f) + (value & 0x0f) + carry;
    setAuxCarry(halfRes > 0x0f);

    regs_.a = res & 0xff;
}

void Intel8049::handleInterrupts() {
    if (!inInterrupt_) {
        // not handling an interrupt, so let's check if we need to.
        if (!notInt_ && externalInterruptsEnabled_) {
            // handle external interrupt
            push();
            regs_.pc = 3;
            inInterrupt_ = true;
        } else if (timerInterruptRequested_ && tcntInterruptsEnabled_) {
            // handle timer interrupt
            timerInterruptRequested_ = false;
            push();
            regs_.pc = 7;
            inInterrupt_ = true;
        }
    }
}

} // namespace kosmos_cp1::emulation

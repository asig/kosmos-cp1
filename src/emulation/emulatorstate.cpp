/*
 * Copyright (c) 2026 Andreas Signer <asigner@gmail.com>
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

#include "emulatorstate.h"

#include <limits>
#include <ostream>
#include <stdexcept>
#include <vector>

#include "intel8049.h"
#include "intel8155.h"

using kosmos_cp1::emulation::Intel8049;
using kosmos_cp1::emulation::Intel8155;
using kosmos_cp1::emulation::PortMode;
using kosmos_cp1::emulation::PortCMode;

void write_u8(std::ostream& os, uint8_t val) {
    os << val;
}

void write_u16(std::ostream& os, uint16_t val) {
    write_u8(os, val & 0xff);
    write_u8(os, (val >> 8) & 0xff);
}

void write_u8v(std::ostream& os, const std::vector<uint8_t>& v) {
    if (v.size() > std::numeric_limits<uint16_t>::max()) {
        throw std::length_error("vector length exceeds uint16_t maximum");
    }
    const uint16_t length = static_cast<uint16_t>(v.size());
    write_u16(os, length);
    for (const auto byte : v) {
        write_u8(os, byte);
    }
}

uint8_t read_u8(std::istream& is) {
    return static_cast<uint8_t>(is.get());
}

uint16_t read_u16(std::istream& is) {    
    uint16_t l = read_u8(is);
    uint16_t h = read_u8(is);
    return h << 8 | l;
}

std::vector<uint8_t> read_u8v(std::istream& is) {
    const uint16_t length = read_u16(is);
    std::vector<uint8_t> values;
    values.reserve(length);
    for (uint16_t i = 0; i < length; ++i) {
        values.push_back(read_u8(is));
    }
    return values;
}

std::ostream& operator<<(std::ostream& os, const Intel8049::State& state) {
    write_u8(os, static_cast<uint8_t>(state.tf));
    write_u8(os, static_cast<uint8_t>(state.notInt));
    write_u8(os, static_cast<uint8_t>(state.timerInterruptRequested));
    write_u8(os, static_cast<uint8_t>(state.t0));
    write_u8(os, static_cast<uint8_t>(state.t1));

    const auto& regs = state.regs;
    write_u8(os, regs.t);
    write_u8(os, regs.a);
    write_u16(os, regs.pc);
    write_u8(os, regs.psw);
    write_u8(os, regs.dbf);
    write_u8(os, regs.f1);

    write_u8(os, static_cast<uint8_t>(state.externalInterruptsEnabled));
    write_u8(os, static_cast<uint8_t>(state.tcntInterruptsEnabled));
    write_u8(os, static_cast<uint8_t>(state.counterRunning));
    write_u8(os, static_cast<uint8_t>(state.timerRunning));
    write_u8(os, state.cyclesUntilCount);
    write_u8(os, static_cast<uint8_t>(state.inInterrupt));

    write_u8v(os, state.rom);
    write_u8v(os, state.ram);

    return os;
}

std::ostream& operator<<(std::ostream& os, const Intel8155::State& state) {   
    write_u8(os, state.addressLatch);
    write_u8(os, static_cast<uint8_t>(state.ioValue));
    write_u8(os, static_cast<uint8_t>(state.ceValue));
    write_u8(os, static_cast<uint8_t>(state.aleValue));
    write_u8(os, static_cast<uint8_t>(state.rdValue));
    write_u8(os, static_cast<uint8_t>(state.wrValue));

    write_u8(os, state.paValue);
    write_u8(os, state.pbValue);
    write_u8(os, state.pcValue);

    write_u8(os, static_cast<uint8_t>(state.paMode));
    write_u8(os, static_cast<uint8_t>(state.pbMode));
    write_u8(os, static_cast<uint8_t>(state.pcMode));

    write_u8(os, static_cast<uint8_t>(state.paInterruptEnabled));
    write_u8(os, static_cast<uint8_t>(state.pbInterruptEnabled));

    write_u8v(os, state.ram);

    return os;
}

std::ostream& operator<<(std::ostream& os, const EmulatorState& state) {
    os << state.cpu << state.pid << state.pidExtension;
    return os;
}

std::istream& operator>>(std::istream& is, Intel8049::State& state) {
    state.tf = read_u8(is);
    state.notInt = read_u8(is);
    state.timerInterruptRequested = read_u8(is);
    state.t0 = read_u8(is);
    state.t1 = read_u8(is);

    const auto& regs = state.regs;
    state.regs.t = read_u8(is); 
    state.regs.a = read_u8(is); 
    state.regs.pc = read_u16(is);
    state.regs.psw = read_u8(is);
    state.regs.dbf = read_u8(is);
    state.regs.f1 = read_u8(is);

    state.externalInterruptsEnabled = read_u8(is);
    state.tcntInterruptsEnabled = read_u8(is);
    state.counterRunning = read_u8(is);
    state.timerRunning = read_u8(is);
    state.cyclesUntilCount = read_u8(is);
    state.inInterrupt = read_u8(is);

    state.rom = read_u8v(is);
    state.ram = read_u8v(is);

    return is;
}

std::istream& operator>>(std::istream& is, Intel8155::State& state) {
    state.addressLatch = read_u8(is);

    state.ioValue = read_u8(is);
    state.ceValue = read_u8(is);
    state.aleValue = read_u8(is);
    state.rdValue = read_u8(is);
    state.wrValue = read_u8(is);

    state.paValue = read_u8(is);
    state.pbValue = read_u8(is);
    state.pcValue = read_u8(is);

    state.paMode = static_cast<PortMode>(read_u8(is));
    state.pbMode = static_cast<PortMode>(read_u8(is));
    state.pcMode = static_cast<PortCMode>(read_u8(is));

    state.paInterruptEnabled = read_u8(is);
    state.pbInterruptEnabled = read_u8(is);

    state.ram = read_u8v(is);

    return is;
}

std::istream& operator>>(std::istream& is, EmulatorState& state) {
    is >> state.cpu >> state.pid >> state.pidExtension;
    return is;
}

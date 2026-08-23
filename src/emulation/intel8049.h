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

#pragma once

#include <cstdint>

#include <QObject>
#include <vector>

#include "emulation/dataport.h"
#include "emulation/ram.h"

#include "fmt/format.h"

namespace kosmos_cp1::emulation {

using std::uint8_t;
using std::uint16_t;

// TODO(asigner):
// - ALE should happen once per cycle. This is currently not the case, but done in MOVX directly
// - Signal /RD and /WR in other BUS operations as well.
class Intel8049 : public QObject {
    Q_OBJECT

public:
    struct Registers {
        uint8_t t;
        uint8_t a;
        uint16_t pc;
        uint8_t psw;
        uint8_t dbf;
        uint8_t f1;
    };

    struct State {

        // Interrupt pins and flipflops
        bool tf; // Timer Flag
        bool notInt;
        bool timerInterruptRequested;
        bool t0;
        bool t1;

        Registers regs;

        bool externalInterruptsEnabled;
        bool tcntInterruptsEnabled;
        bool counterRunning;
        bool timerRunning;
        uint8_t cyclesUntilCount;
        bool inInterrupt;

        std::vector<uint8_t> rom;
        std::vector<uint8_t> ram;
        
        // std::string toString() {
        //     return fmt::format("State("
        //                        "tf={}, notInt={}, timerInterruptRequested={}, "
        //                        "t0={}, t1={}, t={}, a={}, pc={}, psw={}, dbf={}, "
        //                        "f1={}, extIE={}, tcntIE={}, counterRunning={}, timerRunning={}, cyclesUntilCount={}, inInterrupt={})",
        //                        tf, notInt, timerInterruptRequested, t0, t1, t, a, pc, psw, dbf,
        //                        f1, externalInterruptsEnabled, tcntInterruptsEnabled, counterRunning, timerRunning, cyclesUntilCount, inInterrupt);
        // }
    };

    Intel8049(const std::vector<uint8_t>& rom, std::shared_ptr<DataPort> bus, std::shared_ptr<DataPort> p1, std::shared_ptr<DataPort> p2) :
        rom_(rom), ram_(128) {
        ports_ = {bus, p1, p2};
        regs_.t = 0; // T is not affected by reset, need to be initialized explicitly
        reset();
    }

    void initFromState(const State& state) {    
        tf_ = state.tf;
        notInt_ = state.notInt;
        timerInterruptRequested_ = state.timerInterruptRequested;
        t0_ = state.t0;
        t1_ = state.t1;
        regs_ = state.regs;
        externalInterruptsEnabled_ = state.externalInterruptsEnabled;
        tcntInterruptsEnabled_ = state.tcntInterruptsEnabled;
        counterRunning_ = state.counterRunning;
        timerRunning_ = state.timerRunning;
        cyclesUntilCount_ = state.cyclesUntilCount;
        inInterrupt_ = state.inInterrupt;

        ram_.copyFrom(state.ram);
        rom_ = state.rom;
    }

    State saveToState() const {
        State state;
        state.tf = tf_;
        state.notInt = notInt_;
        state.timerInterruptRequested = timerInterruptRequested_;

        state.t0 = t0_;
        state.t1 = t1_;
        state.regs = regs_;
        state.externalInterruptsEnabled = externalInterruptsEnabled_;
        state.tcntInterruptsEnabled = tcntInterruptsEnabled_;
        state.counterRunning = counterRunning_;
        state.timerRunning = timerRunning_;
        state.cyclesUntilCount = cyclesUntilCount_;
        state.inInterrupt = inInterrupt_;

        state.ram = ram_.content();
        state.rom = rom_;

        return state;
    }

    uint16_t pc() const {
        return regs_.pc;
    }

    bool inInterrupt() const {
        return inInterrupt_;
    }

    const Registers& regs() const {
        return regs_;
    }

    std::shared_ptr<DataPort> port(int i) {
        return ports_[i];
    }

    void reset();


    const std::vector<std::uint8_t>& rom() {
        return rom_;
    }

    const Ram *ram() {
        return &ram_;
    }

    uint8_t peek() {
        return rom_[regs_.pc];
    }

    int executeSingleInstr();
    void execute(int cycles);

signals:
    void registersChanged(const Registers& regs);
    void instructionExecuted();
    void resetExecuted();

    void pinPROGWritten(uint8_t val);
    void pinALEWritten(uint8_t val);
    void pinRDLowActiveWritten(uint8_t val);
    void pinWRLowActiveWritten(uint8_t val);

private:
    // Bits in PSW
    constexpr static const uint8_t CY_BIT = 7;
    constexpr static const uint8_t AC_BIT = 6;
    constexpr static const uint8_t F0_BIT = 5;
    constexpr static const uint8_t BS_BIT = 4;

    constexpr static const uint16_t REGISTER_BANK_0_BASE = 0;
    constexpr static const uint16_t REGISTER_BANK_1_BASE = 24;


    void handleInterrupts();
    uint8_t readReg(uint8_t reg);
    void writeReg(uint8_t reg, uint8_t val);
    uint8_t fetch();
    void push();
    void pop(bool restoreState);
    void incCounter();
    void tick();
    uint8_t getBit(uint16_t val, uint8_t bit);
    uint16_t setBit(uint16_t val, uint8_t bit, uint8_t bitVal);
    void setCarry(bool carry);
    void setAuxCarry(bool carry);
    void addToAcc(uint8_t value, bool carryIn);

    std::vector<std::shared_ptr<DataPort>> ports_;

    std::vector<uint8_t> rom_;
    Ram ram_;

    // Interrupt pins and flipflops
    bool tf_; // Timer Flag
    bool notInt_;
    bool timerInterruptRequested_;
    bool t0_;
    bool t1_;

    Registers regs_;

    bool externalInterruptsEnabled_;
    bool tcntInterruptsEnabled_;
    bool counterRunning_;
    bool timerRunning_;
    uint8_t cyclesUntilCount_;
    bool inInterrupt_;

};

}

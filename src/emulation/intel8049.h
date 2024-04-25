#pragma once

#include <cstdint>

#include <QObject>

#include "dataport.h"

namespace kosmos_cp1::emulation {

using std::uint8_t;
using std::uint16_t;

// TODO(asigner):
// - ALE should happen once per cycle. This is currently not the case, but done in MOVX directly
// - Signal /RD and /WR in other BUS operations as well.
class Intel8049 : public QObject {
    Q_OBJECT

public:
    struct State {
        // Interrupt pins and flipflops
        bool tf; // Timer Flag
        bool notInt;
        bool timerInterruptRequested;
        bool t0;
        bool t1;

        uint8_t t;
        uint8_t a;
        uint16_t pc;
        uint8_t psw;
        uint8_t dbf;
        uint8_t f1;

        bool externalInterruptsEnabled;
        bool tcntInterruptsEnabled;
        bool counterRunning;
        bool timerRunning;
        uint8_t cyclesUntilCount;
        bool inInterrupt;
    };

    Intel8049(const std::vector<uint8_t>& rom, std::shared_ptr<DataPort> bus, std::shared_ptr<DataPort> p1, std::shared_ptr<DataPort> p2) :
        rom_(rom), ram_(1024,0) {
        ports_ = {bus, p1, p2};
        reset();
    }

    void reset();

    const State& state() const {
        return state_;
    }

    void setT(uint8_t t) {
        if (t != state_.t) {
            state_.t = t;
            emit stateChanged(state_);
        }
    }

    void setA(uint8_t a) {
        if (a != state_.a) {
            state_.a = a;
            emit stateChanged(state_);
        }
    }

    void setPC(uint16_t pc) {
        if (pc != state_.pc) {
            state_.pc = pc;
            emit stateChanged(state_);
        }
    }

    void setPSW(uint8_t psw) {
        if (psw != state_.psw) {
            state_.psw = psw;
            emit stateChanged(state_);
        }
    }

    void setDBF(uint8_t dbf) {
        if (dbf != state_.dbf) {
            state_.psw = dbf;
            emit stateChanged(state_);
        }
    }

    void setF1(uint8_t f1) {
        if (f1 != state_.f1) {
            state_.psw = f1;
            emit stateChanged(state_);
        }
    }

    void writeT1(bool t1) {
        bool oldT1 = state_.t1;
        state_.t1 = t1;
        if (state_.counterRunning && oldT1 && !state_.t1) {
            // high -> low: count
            incCounter();
        }
    }

    uint8_t peek() {
        return rom_[state_.pc];
    }

    int executeSingleInstr();
    void execute(int cycles);

signals:
    void stateChanged(const State& state);
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


    uint8_t readReg(uint8_t reg) {
        uint16_t base = (state_.psw && (1<<BS_BIT)) == 0 ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        return ram_[base + reg];
    }

    void writeReg(uint8_t reg, uint8_t val) {
        uint16_t base = (state_.psw && (1<<BS_BIT)) == 0 ? REGISTER_BANK_0_BASE : REGISTER_BANK_1_BASE;
        ram_[base + reg] = val;
    }

    uint8_t fetch() {
        return rom_[state_.pc++];
    }

    void push() {
        uint8_t sp = state_.psw & 0x7;
        ram_[8+2*sp] = static_cast<uint8_t>(state_.pc & 0xff);
        ram_[9+2*sp] = static_cast<uint8_t>(state_.psw & 0xf0 | (state_.pc >> 8) & 0xff );
        state_.psw = static_cast<uint8_t>(state_.psw & 0xf8 | (sp + 1) & 0x7);
    }

    void pop(bool restoreState) {
        uint8_t sp = (state_.psw - 1) & 0x7;
        state_.pc = static_cast<uint16_t>(ram_[9+2*sp] & 0xf) << 8 | static_cast<uint16_t>(ram_[8+2*sp]);
        if (restoreState) {
            state_.psw = static_cast<uint8_t>(ram_[9+2*sp] & 0xf0 | 0x8 | (sp & 0x7));
            state_.inInterrupt = false;
        } else {
            state_.psw = state_.psw & 0xf0 | 0x8 | (sp & 0x7);
        }
    }

    void incCounter() {
        state_.tf = state_.tf || state_.t == 0xff;
        state_.timerInterruptRequested = state_.timerInterruptRequested || state_.t == 0xff;
        state_.t = state_.t++;
    }

    void tick() {
        // "executes" another cycle, and performs some periodic stuff (e.g. counting after a STRT T)
        if (state_.timerRunning) {
            state_.cyclesUntilCount--;
            if (state_.cyclesUntilCount == 0) {
                state_.cyclesUntilCount = 32;
                incCounter();
            }
        }
    }

    uint8_t getBit(uint16_t val, uint8_t bit) {
        uint16_t mask = 1 << bit;
        return (val & mask) > 0 ? 1 : 0;
    }

    uint16_t setBit(uint16_t val, uint8_t bit, uint8_t bitVal) {
        uint16_t mask = 1 << bit;
        val &= ~mask;
        val |= bitVal * mask;
        return val;
    }

    void setCarry(bool carry) {
        state_.psw = setBit(state_.psw, CY_BIT, carry ? 1 : 0);
    }

    void setAuxCarry(bool carry) {
        state_.psw = setBit(state_.psw, AC_BIT, carry ? 1 : 0);
    }

    void addToAcc(uint8_t value) {
        uint8_t oldA = state_.a;
        uint8_t oldLoNibble = state_.a & 0xf;
        state_.a = state_.a + value;
        uint8_t loNibble = state_.a & 0xf;
        setCarry(oldA > state_.a);
        setAuxCarry(oldLoNibble > loNibble);
    }

    std::vector<uint8_t> rom_;
    std::vector<uint8_t> ram_;
    std::vector<std::shared_ptr<DataPort>> ports_;
    State state_;
//    Disassembler disassembler_;

};

}

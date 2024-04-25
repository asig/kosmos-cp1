#include "intel8155.h"

#include <iostream>

#include "fmt/format.h"

namespace kosmos_cp1::emulation {

Intel8155::Intel8155(const std::string& name, std::shared_ptr<DataPort> bus)
    : name_(name),
    ram_(256,0),
    bus_(bus),
    paMode_(PortMode::INPUT),
    pbMode_(PortMode::INPUT),
    pcMode_(PortCMode::ALT1),
    paInterruptEnabled_(false),
    pbInterruptEnabled_(false)
{
}


void Intel8155::reset() {
    paMode_ = PortMode::INPUT;
    pbMode_ = PortMode::INPUT;
    pcMode_ = PortCMode::ALT1;
    paInterruptEnabled_ = false;
        pbInterruptEnabled_ = false;
        paValue_ = 0;
        pbValue_ = 0;
        pcValue_ = 0;

        ram_.clear();

        emit resetExecuted();
}

void Intel8155::onPinALEWritten(uint8_t cur) {
        uint8_t prev = aleValue_;
        aleValue_ = cur > 0;
        emit pinsChanged();
        if (prev == 1 && cur == 0) {
            addressLatch_ = bus_->read();
            //            log(Level.FINEST, () -> String.format("bus -> address latch: $%02x", addressLatch));
        }
}

void Intel8155::onPinRDLowActiveWritten(uint8_t cur) {
        uint8_t prev = rdValue_;
        rdValue_ = cur > 0;
        emit pinsChanged();
        if (prev == 0 && cur == 1) {
            if (ceValue_) {
//                log(Level.FINEST, () -> String.format("/CE not active, ignoring read from address $%02x", addressLatch));
                return;
            }
            if (ioValue_) {
                uint8_t data;
                switch (addressLatch_ & 3) {
                case 0:
                    data = 0; // TODO(asigner): return REAL data
//                    log(Level.FINEST, () -> String.format("%s: status -> bus: $%02x", data));
                    bus_->write(data);
                    break;
                case 1:
                    data = paValue_;
//                    log(Level.FINEST, () -> String.format("%s: pa -> bus: $%02x", data));
                    bus_->write(data);
                    break;
                case 2:
                    data = pbValue_;
//                    log(Level.FINEST, () -> String.format("%s: pb -> bus: $%02x", data));
                    bus_->write(data);
                    break;
                case 3:
                    data = pcValue_;
//                    log(Level.FINEST, () -> String.format("%s: pc -> bus: $%02x", data));
                    bus_->write(data);
                    break;
                default:
                    std::cerr << fmt::format("{}: Unhandled IO write to address ${:02x}", name_, addressLatch_);
                }
            } else {
                uint8_t data = ram_[addressLatch_];
//                log(Level.FINEST, () -> String.format("mem[$%02x] -> bus: $%02x", addressLatch, data));
                bus_->write(data);
            }
        }
    }

void Intel8155::onPinWRLowActiveWritten(uint8_t cur) {
        uint8_t prev = wrValue_;
        wrValue_ = cur > 0;
        emit pinsChanged();
        if (prev == 0 && cur == 1) {
            if (ceValue_) {
//                log(Level.FINEST, () -> String.format("/CE not active, ignoring write to address $%02x", addressLatch));
                return;
            }
            uint8_t data = bus_->read();
            if (ioValue_) {
                switch(addressLatch_ & 3) {
                case 0:
                    paMode_ = (data & (1 << 0)) > 0 ? PortMode::OUTPUT : PortMode::INPUT;
                    pbMode_ = (data & (1 << 1)) > 0 ? PortMode::OUTPUT : PortMode::INPUT;
                    switch ((data >> 2) & 3) {
                    case 0:
                        pcMode_ = PortCMode::ALT1;
                        break;
                    case 1:
                        pcMode_ = PortCMode::ALT3;
                        break;
                    case 2:
                        pcMode_ = PortCMode::ALT4;
                        break;
                    case 3:
                        pcMode_ = PortCMode::ALT2;
                        break;
                    }
                    paInterruptEnabled_ = (data & (1 << 4)) > 0;
                    pbInterruptEnabled_ = (data & (1 << 5)) > 0;

                    // TODO(asigner): Timer stuff ignored

                    emit commandRegisterWritten();

//                    log(Level.FINEST, () ->
//                                          "Writing to command register: paMode = " + paMode + ", pbMode = " + pbMode + ", pcMode = " + pcMode +
//                                          ", paInterruptEnabled = " + paInterruptEnabled + ", pbInterruptEnabled = " + pbInterruptEnabled);
                    break;
                case 1:
                    paValue_ = data;
                    emit portWritten(Port::A, data);
//                    log(Level.FINEST, () -> String.format("bus -> pa: $%02x", data));
                    break;
                case 2:
                    pbValue_ = data;
                    emit portWritten(Port::B, data);
//                    log(Level.FINEST, () -> String.format("bus -> pb: $%02x", data));
                    break;
                case 3:
                    pcValue_ = data;
                    emit portWritten(Port::C, data);
//                    log(Level.FINEST, () -> String.format("bus -> pc: $%02x", data));
                    break;
                default:
                    std::cerr << fmt::format("{}: Unhandled IO write to address ${:02x}", name_, addressLatch_);

                }
            } else {
                ram_[addressLatch_] = data;
                emit memoryWritten();
//                log(Level.FINEST, () -> String.format("bus -> mem[%02x]: $%02x", addressLatch, data));
            }
        }

}

void Intel8155::onPinResetWritten(uint8_t cur) {
        if (cur == 1) {
            reset();
        }
}

void Intel8155::onPinCELowActiveWritten(uint8_t cur) {
        ceValue_ = cur > 0;
        emit pinsChanged();
}

void Intel8155::onPinIOWritten(uint8_t cur) {
        ioValue_ = cur > 0;
        emit pinsChanged();
}

} // namespace kosmos_cp1::emulation

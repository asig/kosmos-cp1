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

#include "intel8155.h"

#include <iostream>

#include "fmt/format.h"

namespace kosmos_cp1::emulation {

Intel8155::Intel8155(const std::string& name, std::shared_ptr<DataPort> bus)
    : name_(name),
    bus_(bus),
    ram_(256,0),
    ioValue_(false),
    ceValue_(false),
    aleValue_(false),
    rdValue_(false),
    wrValue_(false),
    paMode_(PortMode::INPUT),
    pbMode_(PortMode::INPUT),
    pcMode_(PortCMode::ALT1),
    paValue_(0),
    pbValue_(0),
    pcValue_(0),
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
                return;
            }
            if (ioValue_) {
                uint8_t data;
                switch (addressLatch_ & 3) {
                case 0:
                    data = 0; // TODO(asigner): return REAL data
                    bus_->write(data);
                    break;
                case 1:
                    data = paValue_;
                    bus_->write(data);
                    break;
                case 2:
                    data = pbValue_;
                    bus_->write(data);
                    break;
                case 3:
                    data = pcValue_;
                    bus_->write(data);
                    break;
                default:
                    std::cerr << fmt::format("{}: Unhandled IO write to address ${:02x}", name_, addressLatch_);
                }
            } else {
                uint8_t data = ram_.read(addressLatch_);
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

                    break;
                case 1:
                    paValue_ = data;
                    emit portWritten(Port::A, data);
                    break;
                case 2:
                    pbValue_ = data;
                    emit portWritten(Port::B, data);
                    break;
                case 3:
                    pcValue_ = data;
                    emit portWritten(Port::C, data);
                    break;
                default:
                    std::cerr << fmt::format("{}: Unhandled IO write to address ${:02x}", name_, addressLatch_);

                }
            } else {
                ram_.write(addressLatch_, data);
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

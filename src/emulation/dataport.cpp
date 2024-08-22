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

#include "dataport.h"

namespace kosmos_cp1::emulation {

DataPort::BitWrittenFunc DataPort::bitSignals[8] = {
    &DataPort::bit0Written,
    &DataPort::bit1Written,
    &DataPort::bit2Written,
    &DataPort::bit3Written,
    &DataPort::bit4Written,
    &DataPort::bit5Written,
    &DataPort::bit6Written,
    &DataPort::bit7Written,
};

DataPort::DataPort(const std::string& name, QObject *parent)
    : name_(name), value_(0), QObject{parent}
{
}

uint8_t DataPort::read() const {
    return value_;
}

void DataPort::write(uint8_t value, uint8_t mask) {
    value_ = (value_ & ~mask) | value;
    emit valueWritten(value_);

    // Send single bits to pins, if necessary
    for (int bit = 0; bit < 8; bit++) {
        if (((1 << bit) & mask) > 0) {
            uint8_t newBit = (value_ & (1 << bit)) > 0 ? 1 : 0;
            (*this.*bitSignals[bit])(newBit);
        }
    }
}

} // namespace kosmos_cp1::emulation

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

#include "emulation/ram.h"

#include <QDebug>

namespace kosmos_cp1::emulation {

Ram::Ram(std::uint16_t size, QObject *parent)
    : QObject{parent}    
{
    content_.resize(size);
    qDebug() << "Size is " << content_.size();
}

void Ram::clear() {
    for (int i = 0; i < content_.size(); i++) {
        content_[i] = 0;
    }
    emit memoryCleared();
}

std::uint8_t Ram::read(std::uint16_t addr) const {
    return content_[addr];
}

void Ram::write(std::uint16_t addr, std::uint8_t val) {
    content_[addr] = val;
    emit memoryWritten(addr, val);
}

} // namespace kosmos_cp1::emulation

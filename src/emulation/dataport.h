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

#include <string>
#include <cstdint>

#include <QObject>

namespace kosmos_cp1::emulation {

using std::uint8_t;

class DataPort : public QObject
{
    Q_OBJECT
public:
    explicit DataPort(const std::string& name, QObject *parent = nullptr);

    uint8_t read() const;
    void write(uint8_t value, uint8_t mask = 0xff);

signals:
    void bit0Written(uint8_t val);
    void bit1Written(uint8_t val);
    void bit2Written(uint8_t val);
    void bit3Written(uint8_t val);
    void bit4Written(uint8_t val);
    void bit5Written(uint8_t val);
    void bit6Written(uint8_t val);
    void bit7Written(uint8_t val);

    void valueWritten(uint8_t newValue);

private:
    std::string name_;
    uint8_t value_;

    typedef void (DataPort::*BitWrittenFunc)(uint8_t);
    static BitWrittenFunc bitSignals[8];

};

}

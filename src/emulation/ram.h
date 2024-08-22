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

#include <stdint.h>
#include <vector>

#include <QObject>

namespace kosmos_cp1::emulation {

class Ram : public QObject
{
    Q_OBJECT
public:
    explicit Ram(std::uint16_t size, QObject *parent = nullptr);

    void clear();

    int size() const {
        return content_.size();
    }

    std::uint8_t read(std::uint16_t addr) const;
    void write(std::uint16_t addr, std::uint8_t val);

signals:
    void memoryWritten(std::uint16_t addr, std::uint8_t val);
    void memoryCleared();

private:
    std::vector<std::uint8_t> content_;
};

} // namespace kosmos_cp1::emulation

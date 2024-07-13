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

namespace kosmos_cp1 {

using std::uint64_t;

class PerformanceMeasurer : public QObject {
    Q_OBJECT
public:
    explicit PerformanceMeasurer(QObject *parent = nullptr);

    void reset();
    void registerExecution(uint32_t cycles);
    bool isUpdateDue();

    double getPerformance();

private:
    uint64_t startNanos_;
    uint64_t lastCheckNanos_;
    uint64_t cycles_;
    uint64_t cyclesTotal_;
};

}

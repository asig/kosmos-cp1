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

#include "performancemeasurer.h"

#include <chrono>

namespace kosmos_cp1 {

PerformanceMeasurer::PerformanceMeasurer(QObject *parent)
    : QObject{parent}
{
    reset();
}

void PerformanceMeasurer::reset() {
    std::chrono::nanoseconds ns = std::chrono::high_resolution_clock::now().time_since_epoch();
    startNanos_ = ns.count();
    cycles_ = 0;
    cyclesTotal_ = 0;
}

void PerformanceMeasurer::registerExecution(uint32_t cycles) {
    cycles_ += cycles;
    cyclesTotal_ += cycles;
}

bool PerformanceMeasurer::isUpdateDue() {
    if (cycles_ > 10000) {
        cycles_ = 0;
        std::chrono::nanoseconds ns = std::chrono::high_resolution_clock::now().time_since_epoch();
        lastCheckNanos_ = ns.count() - startNanos_;
        return lastCheckNanos_ > 500000000L;
    }
    return false;
}

double PerformanceMeasurer::getPerformance() {
    double expectedCycles = lastCheckNanos_ / 2500.0;  // 400 kHz -> 2.5 μs per cycle = 2500 ns per cycle
    double performance = cyclesTotal_ / expectedCycles;
    reset();
    return performance;
}

}

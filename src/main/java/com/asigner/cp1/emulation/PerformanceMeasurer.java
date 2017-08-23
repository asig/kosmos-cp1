/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
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

package com.asigner.cp1.emulation;

public class PerformanceMeasurer {

    private long startNanos;
    private long lastCheckNanos;
    private long cycles;
    private long cyclesTotal;

    public PerformanceMeasurer() {
        reset();
    }

    public void reset() {
        startNanos = System.nanoTime();
        cycles = 0;
        cyclesTotal = 0;
    }

    public void register(int executedCycles) {
        cycles += executedCycles;
        cyclesTotal += executedCycles;
    }

    public boolean isUpdateDue() {
        if (cycles > 10_000) {
            cycles = 0;
            lastCheckNanos = System.nanoTime() - startNanos;
            return lastCheckNanos > 500_000_000L;
        }
        return false;
    }

    public double getPerformance() {
        double expectedCycles = lastCheckNanos / 2500.0;  // 400 kHz -> 2.5 μs per cycle = 2500 ns per cycle
        double performance = cyclesTotal / expectedCycles;
        reset();
        return performance;
    }
}

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

import com.asigner.cp1.ui.Main;

public class SpeedTest {

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Rom rom = new Rom(Main.class.getResourceAsStream("/com/asigner/cp1/CP1.bin"));
            Ram ram8049 = new Ram(128);
            Ram ram8155 = new Ram(256);
            DataPort bus = new DataPort("BUS");
            DataPort p1 = new DataPort("P1");
            DataPort p2 = new DataPort("P2");
            Intel8049 cpu = new Intel8049(ram8049, rom, bus, p1, p2);
            Intel8155 pid = new Intel8155(bus, ram8155);

            // Connect the relevant pins
            cpu.pinALE.connectTo(pid.pinALE);
            cpu.pinRDLowActive.connectTo(pid.pinRDLowActive);
            cpu.pinWRLowActive.connectTo(pid.pinWRLowActive);
            p2.connectBitTo(6, pid.pinReset);
            p2.connectBitTo(5, pid.pinIO);
            p2.connectBitTo(4, pid.pinCELowActive);

            for (;;) {
                int cycles = 1_000_000;
                long start = System.nanoTime();
                cpu.execute(cycles);
                long end = System.nanoTime();
                long dt = end - start;
                double perCycle = ((double) dt) / cycles;
                System.err.println("Executing " + cycles + " cycles took " + dt + " ns, " + perCycle + " ns per cycle" + (perCycle > 2_500_000 ? ", TOO SLOW!" : ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

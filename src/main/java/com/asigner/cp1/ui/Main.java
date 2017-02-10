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

package com.asigner.cp1.ui;

import com.asigner.cp1.emulation.DataPort;
import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.emulation.Rom;
import org.eclipse.swt.widgets.Display;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class Main {

    static {
        final LogManager logManager = LogManager.getLogManager();
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            logManager.readConfiguration(is);
        } catch (IOException e) {
            System.err.println("Can't configure logger");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Rom rom = new Rom(Main.class.getResourceAsStream("/com/asigner/cp1/CP1.bin"));
            DataPort bus = new DataPort("BUS");
            DataPort p1 = new DataPort("P1");
            DataPort p2 = new DataPort("P2");
            Intel8049 cpu = new Intel8049(rom, bus, p1, p2);
            Intel8155 pid = new Intel8155("internal", bus);
            Intel8155 pidExtension = new Intel8155("extension", bus);
            ExecutorThread executorThread = new ExecutorThread(cpu, pid, pidExtension);

            // Connect the relevant pins to the main unit's 8155
            cpu.pinALE.connectTo(pid.pinALE);
            cpu.pinRDLowActive.connectTo(pid.pinRDLowActive);
            cpu.pinWRLowActive.connectTo(pid.pinWRLowActive);
            p2.connectBitTo(4, pid.pinCELowActive);
            p2.connectBitTo(6, pid.pinReset);
            p2.connectBitTo(7, pid.pinIO);

            // Connect the relevant pins to the CP5 Memory Extension's 8155
            cpu.pinALE.connectTo(pidExtension.pinALE);
            cpu.pinRDLowActive.connectTo(pidExtension.pinRDLowActive);
            cpu.pinWRLowActive.connectTo(pidExtension.pinWRLowActive);
            p2.connectBitTo(5, pidExtension.pinCELowActive);
            p2.connectBitTo(6, pidExtension.pinReset);
            p2.connectBitTo(7, pidExtension.pinIO);

            // Now, reset the CPU
            cpu.reset();

            WindowManager windowManager = new WindowManager();
            CpuWindow cpuWindow = new CpuWindow(windowManager, cpu, pid, pidExtension, executorThread);
            KosmosPanelWindow panelWindow = new KosmosPanelWindow(windowManager, cpu, pid, pidExtension, executorThread);
            AssemblerWindow assemblerWindow = new AssemblerWindow(windowManager, pid, pidExtension, executorThread);
            windowManager.addWindow(cpuWindow);
            windowManager.addWindow(panelWindow);
            windowManager.addWindow(assemblerWindow);

            cpuWindow.open();
            panelWindow.open();
            executorThread.start();

            Display display = Display.getDefault();
            while (windowManager.getOpenCount() > 0) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            executorThread.postCommand(ExecutorThread.Command.QUIT);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

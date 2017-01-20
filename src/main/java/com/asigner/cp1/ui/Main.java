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
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

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
            Ram ram8049 = new Ram(128);
            Ram ram8155 = new Ram(256);
            DataPort bus = new DataPort("BUS");
            DataPort p1 = new DataPort("P1");
            DataPort p2 = new DataPort("P2");
            Intel8049 cpu = new Intel8049(ram8049, rom, bus, p1, p2);
            Intel8155 pid = new Intel8155(bus, ram8155);
            ExecutorThread executorThread = new ExecutorThread(cpu, pid);

            // Connect the relevant pins
            cpu.pinALE.connectTo(pid.pinALE);
            cpu.pinRDLowActive.connectTo(pid.pinRDLowActive);
            cpu.pinWRLowActive.connectTo(pid.pinWRLowActive);
            p2.connectBitTo(6, pid.pinReset);
            p2.connectBitTo(5, pid.pinIO);
            p2.connectBitTo(4, pid.pinCELowActive);

            CpuWindow cpuWindow = new CpuWindow(cpu, pid, executorThread);
            KosmosPanelWindow panelWindow = new KosmosPanelWindow(cpu, pid);

            cpuWindow.open();
            panelWindow.open();
            executorThread.start();

            Display display = Display.getDefault();
            while (!cpuWindow.isDisposed() || !panelWindow.isDisposed()) {
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

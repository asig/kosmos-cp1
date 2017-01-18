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

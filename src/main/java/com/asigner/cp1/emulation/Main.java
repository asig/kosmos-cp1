package com.asigner.cp1.emulation;

import com.asigner.cp1.emulation.ui.CpuUI;

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

            // Connect the relevant pins
            cpu.pinALE.connectTo(pid.pinALE);
            cpu.pinRDLowActive.connectTo(pid.pinRDLowActive);
            cpu.pinWRLowActive.connectTo(pid.pinWRLowActive);
            p2.connectBitTo(6, pid.pinReset);
            p2.connectBitTo(5, pid.pinIO);
            p2.connectBitTo(4, pid.pinCELowActive);

            CpuUI window = new CpuUI(cpu, pid);
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

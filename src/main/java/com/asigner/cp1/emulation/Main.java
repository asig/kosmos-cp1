package com.asigner.cp1.emulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import com.asigner.cp1.emulation.ui.CpuUI;

public class Main {

    static {
        final LogManager logManager = LogManager.getLogManager();
        try (final InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
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
            Ram ram = new Ram(256);
            DataPort bus = new DataPort("BUS");
            DataPort p1 = new DataPort("P1");
            DataPort p2 = new DataPort("P2");
            Cpu cpu = new Cpu(ram, rom, bus, p1, p2);
            CpuUI window = new CpuUI(cpu);
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.asigner.cp1.emulation;

import java.io.FileInputStream;

import com.asigner.cp1.emulation.ui.CpuUI;

public class Main {

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Rom rom = new Rom(new FileInputStream("CP1.bin"));
            Ram ram = new Ram(256);
            Cpu cpu = new Cpu(ram, rom);
            CpuUI window = new CpuUI(cpu);
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

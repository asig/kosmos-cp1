// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation;

import java.util.logging.Logger;

public class Intel8155 {

    private static final Logger logger = Logger.getLogger(Intel8155.class.getName());

    // Pins
    public final Pin pinALE = new InputPin("ALE", this::writeALE);
    public final Pin pinRDLowActive = new InputPin("/RD", this::writeRD);
    public final Pin pinWRLowActive = new InputPin("/WR", this::writeWR);

    private final DataPort bus;
    private final Ram ram;

    private int addressLatch;

    public Intel8155(DataPort bus, Ram ram) {
        this.bus = bus;
        this.ram = ram;
    }

    public Ram getRam() {
        return ram;
    }

    private void writeALE(int prev, int cur) {
        if (prev == 1 && cur == 0) {
            addressLatch = bus.read();
            String message = String.format("latching address: 0x%02x", addressLatch);
            logger.fine(message);
        }
    }

    private void writeRD(int prev, int cur) {
        if (prev == 0 && cur == 1) {
            int data = ram.read(addressLatch);
            bus.write(data);
            String message = String.format("Writing data from memory to bus: 0x%02x", data);
            logger.fine(message);
        }
    }

    private void writeWR(int prev, int cur) {
        if (prev == 0 && cur == 1) {
            int data = bus.read();
            ram.write(addressLatch, data);
            String message = String.format("Writing data from bus to memory: 0x%02x", data);
            logger.fine(message);
        }
    }
}

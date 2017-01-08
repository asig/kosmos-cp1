// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.asigner.cp1.emulation.Intel8155.PortCMode.ALT1;

public class Intel8155 {

    public interface StateListener {
        void commandRegisterWritten();
        void portWritten();
        void memoryWritten();
        void pinsChanged();
        void resetExecuted();
    }

    public enum PortMode {
        INPUT, OUTPUT
    }

    public enum PortCMode {
        ALT1, ALT2, ALT3, ALT4
    }

    private static final Logger logger = Logger.getLogger(Intel8155.class.getName());

    private List<StateListener> listeners = new LinkedList<>();

    // Pins
    public final Pin pinALE = new InputPin("ALE", this::writeALE);
    public final Pin pinRDLowActive = new InputPin("/RD", this::writeRD);
    public final Pin pinWRLowActive = new InputPin("/WR", this::writeWR);
    public final Pin pinIO = new InputPin("IO", this::writeIO);
    public final Pin pinReset = new InputPin("Reset", this::writeReset);
    public final Pin pinCELowActive = new InputPin("Reset", this::writeCE);

    private final DataPort bus;
    private final Ram ram;

    private int addressLatch;
    private boolean ioValue = false;
    private boolean ceValue = false;
    private boolean aleValue = false;
    private boolean rdValue = false;
    private boolean wrValue = false;

    private PortMode paMode = PortMode.INPUT;
    private PortMode pbMode = PortMode.INPUT;
    private PortCMode pcMode = ALT1;
    private boolean paInterruptEnabled = false;
    private boolean pbInterruptEnabled = false;

    private int paValue;
    private int pbValue;
    private int pcValue;

    public Intel8155(DataPort bus, Ram ram) {
        this.bus = bus;
        this.ram = ram;
        reset();
    }

    public void addListener(StateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(Intel8155.StateListener listener) {
        listeners.remove(listener);
    }

    public Ram getRam() {
        return ram;
    }


    public void reset() {
        paMode = PortMode.INPUT;
        pbMode = PortMode.INPUT;
        pcMode = ALT1;
        paInterruptEnabled = false;
        pbInterruptEnabled = false;
        paValue = 0;
        pbValue = 0;
        pcValue = 0;
        ioValue = false;
        ceValue = false;
        rdValue = false;
        wrValue = false;

        ram.clear();

        listeners.forEach(StateListener::resetExecuted);
    }

    public boolean isIoValue() {
        return ioValue;
    }

    public boolean isCeValue() {
        return ceValue;
    }

    public boolean isAleValue() {
        return aleValue;
    }

    public boolean isRdValue() {
        return rdValue;
    }

    public boolean isWrValue() {
        return wrValue;
    }

    public PortMode getPaMode() {
        return paMode;
    }

    public PortMode getPbMode() {
        return pbMode;
    }

    public PortCMode getPcMode() {
        return pcMode;
    }

    public boolean isPaInterruptEnabled() {
        return paInterruptEnabled;
    }

    public boolean isPbInterruptEnabled() {
        return pbInterruptEnabled;
    }

    public int getPaValue() {
        return paValue;
    }

    public int getPbValue() {
        return pbValue;
    }

    public int getPcValue() {
        return pcValue;
    }

    private void writeALE(int prev, int cur) {
        aleValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        if (prev == 1 && cur == 0) {
            addressLatch = bus.read();
            String message = String.format("latching address: 0x%02x", addressLatch);
            logger.fine(message);
        }
    }

    private void writeRD(int prev, int cur) {
        rdValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        if (prev == 0 && cur == 1) {
            if (ceValue) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("/CE not active, ignoring read from address " + addressLatch);
                }
                return;
            }
            if (ioValue) {
                int data;
                switch (addressLatch & 3) {
                    case 0:
                        data = 0; // TODO(asigner): return REAL data
                        log(Level.FINEST, () -> String.format("status -> bus: 0x%02x", data));
                        bus.write(data);
                        break;
                    case 1:
                        data = paValue;
                        log(Level.FINEST, () -> String.format("pa -> bus: 0x%02x", data));
                        bus.write(data);
                        break;
                    case 2:
                        data = pbValue;
                        log(Level.FINEST, () -> String.format("pb -> bus: 0x%02x", data));
                        bus.write(data);
                        break;
                    case 3:
                        data = pcValue;
                        log(Level.FINEST, () -> String.format("pc -> bus: 0x%02x", data));
                        bus.write(data);
                        break;
                    default:
                        throw new IllegalStateException("Unhandled IO write to address " + addressLatch);
                }
            } else {
                int data = ram.read(addressLatch);
                log(Level.FINEST, () -> String.format("mem -> bus: 0x%02x", data));
                bus.write(data);
            }
        }
    }

    private void writeWR(int prev, int cur) {
        wrValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        if (prev == 0 && cur == 1) {
            if (ceValue) {
                log(Level.FINEST, () -> "/CE not active, ignoring write to address " + addressLatch);
                return;
            }
            int data = bus.read();
            if (ioValue) {
                switch(addressLatch & 3) {
                    case 0:
                        paMode = (data & (1 << 0)) > 0 ? PortMode.OUTPUT : PortMode.INPUT;
                        pbMode = (data & (1 << 1)) > 0 ? PortMode.OUTPUT : PortMode.INPUT;
                        switch ((data >> 2) & 3) {
                            case 0:
                                pcMode = PortCMode.ALT1;
                                break;
                            case 1:
                                pcMode = PortCMode.ALT2;
                                break;
                            case 2:
                                pcMode = PortCMode.ALT3;
                                break;
                            case 3:
                                pcMode = PortCMode.ALT4;
                                break;
                        }
                        paInterruptEnabled = (data & (1 << 4)) > 0;
                        pbInterruptEnabled = (data & (1 << 5)) > 0;

                        // TODO(asigner): Timer stuff ignored

                        listeners.forEach(StateListener::commandRegisterWritten);

                        log(Level.FINEST, () ->
                                "Writing to command register: paMode = " + paMode + ", pbMode = " + pbMode + ", pcMode = " + pcMode +
                                        ", paInterruptEnabled = " + paInterruptEnabled + ", pbInterruptEnabled = " + pbInterruptEnabled);
                        break;
                    case 1:
                        paValue = data;
                        listeners.forEach(StateListener::portWritten);
                        log(Level.FINEST, () -> String.format("bus -> pa: 0x%02x", data));
                        break;
                    case 2:
                        pbValue = data;
                        listeners.forEach(StateListener::portWritten);
                        log(Level.FINEST, () -> String.format("bus -> pb: 0x%02x", data));
                        break;
                    case 3:
                        pcValue = data;
                        listeners.forEach(StateListener::portWritten);
                        log(Level.FINEST, () -> String.format("bus -> pc: 0x%02x", data));
                        break;
                    default:
                        throw new IllegalStateException("Unhandled IO write to address " + addressLatch);

                }
            } else {
                ram.write(addressLatch, data);
                listeners.forEach(StateListener::memoryWritten);
                log(Level.FINEST, () -> String.format("bus -> mem: 0x%02x", data));
            }
        }
    }

    private void writeReset(int prev, int cur) {
        log(Level.FINEST, () -> String.format("Reset set to: 0x%02x", cur));
        if (cur == 1) {
            reset();
        }
    }

    private void writeCE(int prev, int cur) {
        this.ceValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        log(Level.FINEST, () -> String.format("/CE set to: 0x%02x", cur));
    }

    private void writeIO(int prev, int cur) {
        this.ioValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        log(Level.FINEST, () -> String.format("IO set to: 0x%02x", cur));
    }

    private void log(Level level, Supplier<String> supplier) {
        if (logger.isLoggable(level)) {
            logger.log(level, supplier.get());
        }
    }
}

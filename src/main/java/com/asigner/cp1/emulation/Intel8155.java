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

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.asigner.cp1.emulation.Intel8155.PortCMode.ALT1;

public class Intel8155 {

    public interface StateListener {
        enum Port {
            A, B, C
        }

        default void commandRegisterWritten() {};
        default void portWritten(Port port, int value) {};
        default void memoryWritten() {};
        default void pinsChanged() {};
        default void resetExecuted() {};
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

    private final String name;
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

    public Intel8155(String name, DataPort bus) {
        this.name = name;
        this.bus = bus;
        this.ram = new Ram(256);
        reset();
    }

    public void addListener(StateListener listener) {
        List<StateListener> newListeners = Lists.newLinkedList(listeners);
        newListeners.add(listener);
        listeners = newListeners;
    }

    public void removeListener(Intel8155.StateListener listener) {
        List<StateListener> newListeners = Lists.newLinkedList(listeners);
        newListeners.remove(listener);
        listeners = newListeners;
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
            log(Level.FINEST, () -> String.format("bus -> address latch: $%02x", addressLatch));
        }
    }

    private void writeRD(int prev, int cur) {
        rdValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        if (prev == 0 && cur == 1) {
            if (ceValue) {
                log(Level.FINEST, () -> String.format("/CE not active, ignoring read from address $%02x", addressLatch));
                return;
            }
            if (ioValue) {
                int data;
                switch (addressLatch & 3) {
                    case 0:
                        data = 0; // TODO(asigner): return REAL data
                        log(Level.FINEST, () -> String.format("%s: status -> bus: $%02x", data));
                        bus.write(data);
                        break;
                    case 1:
                        data = paValue;
                        log(Level.FINEST, () -> String.format("%s: pa -> bus: $%02x", data));
                        bus.write(data);
                        break;
                    case 2:
                        data = pbValue;
                        log(Level.FINEST, () -> String.format("%s: pb -> bus: $%02x", data));
                        bus.write(data);
                        break;
                    case 3:
                        data = pcValue;
                        log(Level.FINEST, () -> String.format("%s: pc -> bus: $%02x", data));
                        bus.write(data);
                        break;
                    default:
                        throw new IllegalStateException(String.format("%s: Unhandled IO write to address $%02x", name, addressLatch));
                }
            } else {
                int data = ram.read(addressLatch);
                log(Level.FINEST, () -> String.format("mem[$%02x] -> bus: $%02x", addressLatch, data));
                bus.write(data);
            }
        }
    }

    private void writeWR(int prev, int cur) {
        wrValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        if (prev == 0 && cur == 1) {
            if (ceValue) {
                log(Level.FINEST, () -> String.format("/CE not active, ignoring write to address $%02x", addressLatch));
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
                                pcMode = PortCMode.ALT3;
                                break;
                            case 2:
                                pcMode = PortCMode.ALT4;
                                break;
                            case 3:
                                pcMode = PortCMode.ALT2;
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
                        listeners.forEach(l -> l.portWritten(StateListener.Port.A, data));
                        log(Level.FINEST, () -> String.format("bus -> pa: $%02x", data));
                        break;
                    case 2:
                        pbValue = data;
                        listeners.forEach(l -> l.portWritten(StateListener.Port.B, data));
                        log(Level.FINEST, () -> String.format("bus -> pb: $%02x", data));
                        break;
                    case 3:
                        pcValue = data;
                        listeners.forEach(l -> l.portWritten(StateListener.Port.C, data));
                        log(Level.FINEST, () -> String.format("bus -> pc: $%02x", data));
                        break;
                    default:
                        throw new IllegalStateException("Unhandled IO write to address " + addressLatch);

                }
            } else {
                ram.write(addressLatch, data);
                listeners.forEach(StateListener::memoryWritten);
                log(Level.FINEST, () -> String.format("bus -> mem[%02x]: $%02x", addressLatch, data));
            }
        }
    }

    private void writeReset(int prev, int cur) {
        log(Level.FINEST, () -> String.format("Reset set to: $%02x", cur));
        if (cur == 1) {
            reset();
        }
    }

    private void writeCE(int prev, int cur) {
        this.ceValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        log(Level.FINEST, () -> String.format("/CE set to: $%02x", cur));
    }

    private void writeIO(int prev, int cur) {
        this.ioValue = cur > 0;
        listeners.forEach(StateListener::pinsChanged);
        log(Level.FINEST, () -> String.format("IO set to: $%02x", cur));
    }

    private void log(Level level, Supplier<String> supplier) {
        if (logger.isLoggable(level)) {
            logger.log(level, name + ": " + supplier.get());
        }
    }
}

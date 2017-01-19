package com.asigner.cp1.emulation;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class DataPort {
    private static final Logger logger = Logger.getLogger(DataPort.class.getName());

    public interface Listener {
        void valueChanged(int oldValue, int newValue);
    }

    private final String name;
    private int value;
    private List<Pin>[] sinks = new List[8];

    private final List<Listener> listeners = new LinkedList<Listener>();

    public DataPort(String name) {
        this.name = name;
        this.value = 0;
        for (int i = 0; i < 8; i++) {
            this.sinks[i] = Lists.newArrayList();
        }
    }

    public int read() {
        return value;
    }

    public void write(int value) {
        write(value, 0xff);
    }

    public void write(int value, int mask) {
        value = value & mask;
        if (value != (this.value & mask)) {
            int oldValue = this.value;
            this.value = (this.value & ~mask) | value;
            listeners.forEach(l -> l.valueChanged(oldValue, this.value));

            // Send single bits to pins, if necessary
            for (int bit = 0; bit < 8; bit++) {
                if (((1 << bit) & mask) > 0) {
                    List<Pin> sinks = this.sinks[bit];
                    int oldBit = (oldValue & (1 << bit)) > 0 ? 1 : 0;
                    int newBit = (value & (1 << bit)) > 0 ? 1 : 0;
                    if (oldBit != newBit) {
                        sinks.forEach(s -> s.write(newBit));
                    }
                }
            }
        }
    }

    public void connectBitTo(int bit, Pin other) {
        this.sinks[bit].add(other);
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}

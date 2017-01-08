package com.asigner.cp1.emulation;

import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class DataPort {

    private static final Logger logger = Logger.getLogger(DataPort.class.getName());

    private final String name;
    private int value;
    private List<Pin>[] sinks = new List[8];

    private final List<DataPortListener> listeners = new LinkedList<DataPortListener>();

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
        value = value & 0xff;
        if (value != this.value) {
            int oldValue = this.value;
            this.value = value;
            fireValueChanged(oldValue, value);

            // Send single bits to pins, if necessary
            for (int bit = 0; bit < 8; bit++) {
                List<Pin> sinks = this.sinks[bit];
                int oldBit = (oldValue & (1 << bit)) > 0 ? 1 : 0;
                int newBit = (value & (1 << bit)) > 0 ? 1 : 0;
                if (oldBit != newBit) {
                    sinks.forEach(s -> s.write(newBit));
                }
            }
        }
    }

    public void connectBitTo(int bit, Pin other) {
        this.sinks[bit].add(other);
    }

    public void addListener(DataPortListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(DataPortListener listener) {
        this.listeners.remove(listener);
    }

    private void fireValueChanged(int oldValue, int newValue) {
        for (DataPortListener listener : listeners) {
            listener.valueChanged(oldValue, newValue);
        }
    }
}

package com.asigner.cp1.emulation;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class DataPort {

    private static final Logger logger = Logger.getLogger(DataPort.class.getName());

    private final String name;
    private int value;

    private final List<DataPortListener> listeners = new LinkedList<DataPortListener>();

    public DataPort(String name) {
        this.name = name;
        this.value = 0;
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
        }
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

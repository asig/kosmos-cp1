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
        int res = value & 0xff;
        String binVal = String.format("%8s", Integer.toBinaryString(res)).replace(' ', '0');
        String message = String.format("Port %3s: reading 0x%02x (0b%s)", name, res, binVal);
        logger.info(message);
        return res;
    }

    public void write(int value) {
        String binVal = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
        String message = String.format("Port %3s: writing 0x%02x (0b%s)", name, value, binVal);
        logger.info(message);
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

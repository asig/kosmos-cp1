package com.asigner.cp1.emulation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Ram implements Readable, Writeable {

    private byte[] memory;
    private List<MemoryModifiedListener> listeners = new LinkedList<MemoryModifiedListener>();
    private boolean notifcationsEnabled = true;

    public Ram(int size) {
        memory = new byte[size];
    }

    public void addListener(MemoryModifiedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MemoryModifiedListener listener) {
        listeners.remove(listener);
    }

    public void enableNotifications(boolean enabled) {
        notifcationsEnabled = enabled;
    }

    @Override
    public int size() {
        return memory.length;
    }

    @Override
    public int read(int addr) {
        return memory[addr] & 0xff;
    }

    @Override
    public void write(int addr, int value) {
        memory[addr] = (byte)(value & 0xff);
        fireMemoryWritten(addr, value);
    }

    @Override
    public void clear() {
        Arrays.fill(memory, (byte)0);
        fireMemoryCleared();
    }

    private void fireMemoryWritten(int addr, int value) {
        if (!notifcationsEnabled) {
            return;
        }
        for (MemoryModifiedListener listener : listeners) {
            listener.memoryWritten(addr,  value);
        }
    }

    private void fireMemoryCleared() {
        if (!notifcationsEnabled) {
            return;
        }
        for (MemoryModifiedListener listener : listeners) {
            listener.memoryCleared();
        }
    }
}

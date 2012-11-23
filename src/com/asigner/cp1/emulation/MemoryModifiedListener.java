package com.asigner.cp1.emulation;

public interface MemoryModifiedListener {
    void memoryWritten(int addr, int value);
    void memoryCleared();
}

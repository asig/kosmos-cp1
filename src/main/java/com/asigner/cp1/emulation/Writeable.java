package com.asigner.cp1.emulation;

public interface Writeable {
    int size();
    void write(int addr, int value);
    void clear();
}

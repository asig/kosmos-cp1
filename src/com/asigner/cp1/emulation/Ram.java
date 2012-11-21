package com.asigner.cp1.emulation;

import java.util.Arrays;

public class Ram implements Readable, Writeable {

    private byte[] memory;

    public Ram(int size) {
        memory = new byte[size];
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
    }

    @Override
    public void clear() {
        Arrays.fill(memory, (byte)0);
    }
}

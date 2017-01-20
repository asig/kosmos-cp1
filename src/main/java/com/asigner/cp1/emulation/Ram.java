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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Ram implements Readable, Writeable {

    private byte[] memory;
    private List<MemoryModifiedListener> listeners = new LinkedList<MemoryModifiedListener>();

    public Ram(int size) {
        memory = new byte[size];
    }

    public void addListener(MemoryModifiedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MemoryModifiedListener listener) {
        listeners.remove(listener);
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
        listeners.forEach(l -> l.memoryWritten(addr, value));
    }

    @Override
    public void clear() {
        Arrays.fill(memory, (byte)0);
        listeners.forEach(MemoryModifiedListener::memoryCleared);
    }
}

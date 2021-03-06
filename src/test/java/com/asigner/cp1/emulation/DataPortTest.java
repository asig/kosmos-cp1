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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataPortTest {

    private static class RecordingInputPin extends Pin {

        int value = 0;
        boolean writeCalled = false;

        public RecordingInputPin() {
            super("");
        }

        @Override
        void write(int value) {
            this.value = value;
            this.writeCalled = true;
        }

        void reset() {
            this.value = 0;
            this.writeCalled = false;
        }
    }

    @Test
    public void testConnectedPins() throws Exception {
        DataPort port = new DataPort("test");

        RecordingInputPin p0 = new RecordingInputPin();
        RecordingInputPin p1 = new RecordingInputPin();
        RecordingInputPin p2 = new RecordingInputPin();
        RecordingInputPin p3 = new RecordingInputPin();
        RecordingInputPin p4 = new RecordingInputPin();
        RecordingInputPin p5 = new RecordingInputPin();
        RecordingInputPin p6 = new RecordingInputPin();
        RecordingInputPin p7 = new RecordingInputPin();
        port.connectBitTo(0, p0);
        port.connectBitTo(1, p1);
        port.connectBitTo(2, p2);
        port.connectBitTo(3, p3);
        port.connectBitTo(4, p4);
        port.connectBitTo(5, p5);
        port.connectBitTo(6, p6);
        port.connectBitTo(7, p7);

        port.write(0b00000001);
        assertEquals(0, p7.value);
        assertEquals(0, p6.value);
        assertEquals(0, p5.value);
        assertEquals(0, p4.value);
        assertEquals(0, p3.value);
        assertEquals(0, p2.value);
        assertEquals(0, p1.value);
        assertEquals(1, p0.value);

        port.write(0b00000010);
        assertEquals(0, p7.value);
        assertEquals(0, p6.value);
        assertEquals(0, p5.value);
        assertEquals(0, p4.value);
        assertEquals(0, p3.value);
        assertEquals(0, p2.value);
        assertEquals(1, p1.value);
        assertEquals(0, p0.value);

        port.write(0b00000100);
        assertEquals(0, p7.value);
        assertEquals(0, p6.value);
        assertEquals(0, p5.value);
        assertEquals(0, p4.value);
        assertEquals(0, p3.value);
        assertEquals(1, p2.value);
        assertEquals(0, p1.value);
        assertEquals(0, p0.value);

        port.write(0b00001000);
        assertEquals(0, p7.value);
        assertEquals(0, p6.value);
        assertEquals(0, p5.value);
        assertEquals(0, p4.value);
        assertEquals(1, p3.value);
        assertEquals(0, p2.value);
        assertEquals(0, p1.value);
        assertEquals(0, p0.value);

        port.write(0b00010000);
        assertEquals(0, p7.value);
        assertEquals(0, p6.value);
        assertEquals(0, p5.value);
        assertEquals(1, p4.value);
        assertEquals(0, p3.value);
        assertEquals(0, p2.value);
        assertEquals(0, p1.value);
        assertEquals(0, p0.value);

        port.write(0b00100000);
        assertEquals(0, p7.value);
        assertEquals(0, p6.value);
        assertEquals(1, p5.value);
        assertEquals(0, p4.value);
        assertEquals(0, p3.value);
        assertEquals(0, p2.value);
        assertEquals(0, p1.value);
        assertEquals(0, p0.value);

        port.write(0b01000000);
        assertEquals(0, p7.value);
        assertEquals(1, p6.value);
        assertEquals(0, p5.value);
        assertEquals(0, p4.value);
        assertEquals(0, p3.value);
        assertEquals(0, p2.value);
        assertEquals(0, p1.value);
        assertEquals(0, p0.value);

        port.write(0b10000000);
        assertEquals(1, p7.value);
        assertEquals(0, p6.value);
        assertEquals(0, p5.value);
        assertEquals(0, p4.value);
        assertEquals(0, p3.value);
        assertEquals(0, p2.value);
        assertEquals(0, p1.value);
        assertEquals(0, p0.value);
    }

    @Test
    public void testWritePartial() throws Exception {
        DataPort port = new DataPort("test");
        RecordingInputPin[] p = new RecordingInputPin[8];
        for (int i = 0; i < 8; i++) {
            p[i] = new RecordingInputPin();
            port.connectBitTo(i, p[i]);
            p[i].reset();
        }

        port.write(0xff, 0x7);
        assertFalse(p[7].writeCalled);
        assertFalse(p[6].writeCalled);
        assertFalse(p[5].writeCalled);
        assertFalse(p[4].writeCalled);
        assertFalse(p[3].writeCalled);
        assertTrue(p[2].writeCalled);
        assertTrue(p[1].writeCalled);
        assertTrue(p[0].writeCalled);
        assertEquals(0x7, port.read());
    }

    @Test
    public void testWritePartial_KeepsExistingData() throws Exception {
        DataPort port = new DataPort("test");

        port.write(0b11111111);
        port.write(0b00000000, 0b00000111);
        assertEquals(0b11111000, port.read());
    }
}

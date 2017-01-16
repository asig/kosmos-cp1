package com.asigner.cp1.emulation;

import org.junit.Test;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static sun.java2d.cmm.ColorTransform.In;

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

        port.write(0xff, 0x7);
        assertFalse(p7.writeCalled);
        assertFalse(p6.writeCalled);
        assertFalse(p5.writeCalled);
        assertFalse(p4.writeCalled);
        assertFalse(p3.writeCalled);
        assertTrue(p2.writeCalled);
        assertTrue(p1.writeCalled);
        assertTrue(p0.writeCalled);
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
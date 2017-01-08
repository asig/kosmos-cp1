package com.asigner.cp1.emulation;

import org.junit.Test;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static sun.java2d.cmm.ColorTransform.In;

public class DataPortTest {

    private static class RecordingInputPin extends Pin {

        int value;

        public RecordingInputPin() {
            super("");
        }

        @Override
        void write(int value) {
            this.value = value;
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

}
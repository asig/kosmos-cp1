package com.asigner.cp1.emulation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Rom implements Readable {

    private final byte[] rom;

    public Rom(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int read;
        while ( (read = is.read(buf)) > 0) {
            os.write(buf, 0, read);
        }
        rom = os.toByteArray();
    }

    @Override
    public int size() {
        return rom.length;
    }

    @Override
    public int read(int addr) {
        return rom[addr] & 0xff;
    }
}

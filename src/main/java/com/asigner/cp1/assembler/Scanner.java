package com.asigner.cp1.assembler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: asigner
 * Date: 3/28/11
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Scanner {

    private final String text;
    private int curPos;

    public Scanner(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] byteBuf = new byte[10240];
        int read;
        while ( (read = is.read(byteBuf)) > 0) {
            bos.write(byteBuf);
        }
        bos.close();
        this.text = new String(bos.toByteArray());
        curPos = 0;
    }
}

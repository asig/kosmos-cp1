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

package com.asigner.cp1.assembler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssemblerTest {

    @Test
    public void testBasics() {
        String source =
                " LDA 100 ; 05.100 Inhalt von Zelle 100 in den Akku laden                  \n" +
                " ANZ     ; 02.000; Akku-Inhalt anzeigen                                   \n" +
                " VZG 250 ; 03.250 250 ms verzögern                                        \n" +
                " LDA 101 ; 05.101 Inhalt von Zelle 101 in den Akku laden                  \n" +
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n" +
                " VZG 250 ; 03.250 250 ms verzögern                                        \n" +
                " LDA 102 ; 05.102 Inhalt von Zelle 102 in den Akku laden                  \n" +
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n" +
                " VZG 250 ; 03.250 250 ms verzögern                                        \n" +
                " LDA 103 ; 05.103 Inhalt von Zelle 103 in den Akku laden                  \n" +
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n" +
                " VZG 250 ; 03.250 250 ms verzögern                                        \n" +
                " LDA 104 ; 05.104 Inhalt von Zelle 104 in den Akku laden                  \n" +
                " ANZ     ; 02.000 Akku-Inhalt anzeigen                                    \n" +
                " VZG 250 ; 03.250 250 ms verzögern                                        \n" +
                " HLT     ; 01.000 Anhalten                                                \n" +
                " .DB 11  ; 00.011                                                         \n" +
                " .DB 22  ; 00.022                                                         \n" +
                " .DB 33  ; 00.033 Zahlenwerte, die der Reihe nach angezeigt werden sollen \n" +
                " .DB 44  ; 00.044                                                         \n" +
                " .DB 55  ; 00.055                                                         \n";

        Assembler assembler = new Assembler(source);
        assembler.assemble();
        assertEquals(0, assembler.getErrors().size());
        byte[] code = assembler.getCode();
        assertEquals(256, code.length);
        checkContent(code,
                5,100,
                2, 0,
                3,250,
                5,101,
                2,0,
                3,250,
                5,102,
                2,0,
                3,250,
                5,103,
                2,0,
                3,250,
                5,104,
                2,0,
                3,250,
                1,0,
                0,11,
                0,22,
                0,33,
                0,44,
                0,55);
    }

    @Test
    public void testForwardReference() {
        String source =
                "    LDA L \n" +
                "    AKO L \n" +
                "L   HLT \n";

        Assembler assembler = new Assembler(source);
        assembler.assemble();
        assertEquals(0, assembler.getErrors().size());
        byte[] code = assembler.getCode();
        assertEquals(256, code.length);
        checkContent(code,
                5,2,
                4, 2,
                1,0);
    }

    @Test
    public void testBackwardReference() {
        String source =
                "    .ORG 5 \n" +
                "    AKO 0 \n" +
                "L   AKO 0 \n" +
                "    AKO 0 \n" +
                "    SPU  L \n";

        Assembler assembler = new Assembler(source);
        assembler.assemble();
        assertEquals(0, assembler.getErrors().size());
        byte[] code = assembler.getCode();
        assertEquals(256, code.length);
        checkContent(code,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                4, 0,
                4, 0,
                4, 0,
                9, 6);
    }

    @Test
    public void testRaw() {
        String source =
                "    .RAW 12.123, 23.234, 22.222 \n" +
                "    .RAW 11.111 \n";

        Assembler assembler = new Assembler(source);
        assembler.assemble();
        assertEquals(0, assembler.getErrors().size());
        byte[] code = assembler.getCode();
        assertEquals(256, code.length);
        checkContent(code,
                12, 123,
                23, 234,
                22, 222,
                11, 111);
    }
    
    @Test
    public void testOrg() {
        String source =
                " .ORG 5 \n" +
                " LDA 100 \n" +
                ".ORG 10 \n" +
                " VZG 250\n";

        Assembler assembler = new Assembler(source);
        assembler.assemble();
        assertEquals(0, assembler.getErrors().size());
        byte[] code = assembler.getCode();
        assertEquals(256, code.length);
        checkContent(code,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                5,100,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                3,250);
    }

    @Test
    public void testEqu() {
        String source =
                " .EQU foo, 250  \n" +
                " VZG foo \n";

        Assembler assembler = new Assembler(source);
        assembler.assemble();
        assertEquals(0, assembler.getErrors().size());
        byte[] code = assembler.getCode();
        assertEquals(256, code.length);
        checkContent(code,
                3,250
        );
    }

    private void checkContent(byte[] code, int ... values) {
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], code[i] & 0xff);
        }
    }

}

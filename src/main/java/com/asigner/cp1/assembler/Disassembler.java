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

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Disassembler {

    private static class OpDesc {
        String mnemonic;
        int params;

        public OpDesc(String mnemonic, int params) {
            this.mnemonic = mnemonic;
            this.params = params;
        }
    }

    private Map<Integer, OpDesc> ops = ImmutableMap.<Integer, OpDesc>builder()
            .put(1, new OpDesc("HLT", 0))
            .put(2, new OpDesc("ANZ", 0))
            .put(3, new OpDesc("VZG", 1))
            .put(4, new OpDesc("AKO", 1))
            .put(5, new OpDesc("LDA", 1))
            .put(6, new OpDesc("ABS", 1))
            .put(7, new OpDesc("ADD", 1))
            .put(8, new OpDesc("SUB", 1))
            .put(9, new OpDesc("SPU", 1))
            .put(10, new OpDesc("VGL", 1))
            .put(11, new OpDesc("SPB", 1))
            .put(12, new OpDesc("VGR", 1))
            .put(13, new OpDesc("VKL", 1))
            .put(14, new OpDesc("NEG", 0))
            .put(15, new OpDesc("UND", 1))
            .put(16, new OpDesc("P1E", 1))
            .put(17, new OpDesc("P1A", 1))
            .put(18, new OpDesc("P2A", 1))
            .put(19, new OpDesc("LIA", 1))
            .put(20, new OpDesc("AIS", 1))
            .put(21, new OpDesc("SIU", 1))
            .put(22, new OpDesc("P3E", 1))
            .put(23, new OpDesc("P4A", 1))
            .put(24, new OpDesc("P5A", 1))
            .build();

}

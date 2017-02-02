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
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Assembler {

    private interface ParamHandler {
        void generate(int lineNum, int opcode, List<String> params);
    }

    private static class OpDesc {
        private final int opCode;
        private final ParamHandler handler;

        public OpDesc(int opCode, ParamHandler handler) {
            this.opCode = opCode;
            this.handler = handler;
        }

    }

    private ParamHandler nullHandler = new ParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            if (params.size() > 0) {
                error("Line " + lineNum + ": No parameters expected");
            }
            memory[pc++] = opcode << 8 | 0;
        }
    };

    private ParamHandler dataHandler = new ParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            for (String param : params) {
                try {
                    int val;
                    if (param.startsWith("$")) {
                        val = Integer.parseInt(param.substring(1, 16));
                    } else {
                        val = Integer.parseInt(param);
                    }
                    if (val < 0 || val > 255) {
                        error("Line " + lineNum + ": " + param + " is out of range");
                        val = 0;
                    }
                    memory[pc++] = val;
                } catch(NumberFormatException e) {
                    error("Line " + lineNum + ": " + param + " is not a valid number");
                }
            }
        }
    };

    private static ParamHandler addressHandler = new ParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
        }
    };
    private static ParamHandler constHandler = new ParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
        }
    };
    private static ParamHandler optConstHandler = new ParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
        }
    };

    private Map<String, OpDesc> ops = ImmutableMap.<String, OpDesc>builder()
            .put(".DB", new OpDesc(0, dataHandler))
            .put("HLT", new OpDesc(1, nullHandler))
            .put("ANZ", new OpDesc(2, nullHandler))
            .put("VZG", new OpDesc(3, constHandler))
            .put("AKO", new OpDesc(4, constHandler))
            .put("LDA", new OpDesc(5, addressHandler))
            .put("ABS", new OpDesc(6, addressHandler))
            .put("ADD", new OpDesc(7, addressHandler))
            .put("SUB", new OpDesc(8, addressHandler))
            .put("SPU", new OpDesc(9, addressHandler))
            .put("VGL", new OpDesc(10, addressHandler))
            .put("SPB", new OpDesc(11, addressHandler))
            .put("VGR", new OpDesc(12, addressHandler))
            .put("VKL", new OpDesc(13, addressHandler))
            .put("NEG", new OpDesc(14, nullHandler))
            .put("UND", new OpDesc(15, addressHandler))
            .put("P1E", new OpDesc(16, optConstHandler))
            .put("P1A", new OpDesc(17, optConstHandler))
            .put("P2A", new OpDesc(18, optConstHandler))
            .put("LIA", new OpDesc(19, addressHandler))
            .put("AIS", new OpDesc(20, addressHandler))
            .put("SIU", new OpDesc(21, addressHandler))
            .put("P3E", new OpDesc(22, optConstHandler))
            .put("P4A", new OpDesc(23, optConstHandler))
            .put("P5A", new OpDesc(24, optConstHandler))
            .build();

    private final int memory[] = new int[256];
    private int pc = 0;

    private Map<String, Integer> labels = new HashMap<String, Integer>();
    private Map<String, List<Integer>> pendingReferences = new HashMap<String, List<Integer>>();

    private List<String> text;
    private String outputName;

    private Assembler(String inputName, String outputName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(inputName));
        text = new LinkedList<String>();
        String line = in.readLine();
        while(line != null) {
            text.add(line.trim());
            line = in.readLine();
        }
    }

    private void go() {
        for (int i = 0; i < text.size(); i++) {
            handleLine(i+1, text.get(i));
        }
    }

    private void handleLine(int lineNum, String line) {
        // Format: [label] [op/directive] [param] {"," param }

        // remove comments
        int idx = line.indexOf(";");
        if (idx >= 0) {
            line = line.substring(idx);
        }
        if (line.isEmpty()) {
            return;
        }

        int curPos = 0;

        // label
        String label = "";
        if (Character.isLetter(label.charAt(curPos))) {
            while (Character.isLetterOrDigit(line.charAt(curPos))) {
                label += line.charAt(curPos++);
            }
        }

        // skip whitespace
        while (Character.isWhitespace(line.charAt(curPos++))) ;

        // opcode
        String opcode = "";
        while (Character.isLetterOrDigit(line.charAt(curPos))) {
            opcode += line.charAt(curPos++);
        }

        // skip whitespace
        while (Character.isWhitespace(line.charAt(curPos++))) ;

        // parameters
        List<String> params = Lists.newArrayList(line.substring(curPos).split(",")).stream().map(String::trim).collect(Collectors.toList());

        // add and resolve label
        if (!label.isEmpty()) {
            addLabel(label, pc);
        }

        // find opcode
        OpDesc op = ops.get(opcode);
        if (op == null) {
            error("Unknown mnemonic");
        }
        op.handler.generate(lineNum, op.opCode, params);
    }

    private void addLabel(String label, int address) {
        labels.put(label, address);
        List<Integer> pendingRefs = pendingReferences.get(label);
        if (pendingRefs != null) {
            for (int i : pendingRefs) {
                memory[i] = memory[i] & 0xff00 | (0xff & address);
            }
            pendingReferences.remove(label);
        }
    }

    private void error(String msg) {
        System.err.println(msg);
    }

    private static void usage() {
        System.err.println("Usage: Assembler <input-file> <output-file>");
    }

    public static void main(String args[]) {

        String input = " \t    MOV a   b";
        String parts[] = input.split("\\s+");
        for(String p : parts) {
            System.err.println("Prt: " + p);
        }
        if (args.length != 2) {
            usage();
            return;
        }
        try {
            new Assembler(args[0], args[1]).go();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

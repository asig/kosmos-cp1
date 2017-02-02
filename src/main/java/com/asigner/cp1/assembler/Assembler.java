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
import java.io.Reader;
import java.io.StringReader;
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

    private abstract class BasicParamHandler implements ParamHandler {
        protected boolean checkParamSize(int lineNo, int expectedParams, List<String> params) {
            if (params.size() != expectedParams) {
                error(String.format("Line %d: %d params expected, but %d params encountered.", lineNo, expectedParams, params.size()));
                return false;
            }
            return true;
        }

        protected Integer parseInt(int lineNo, String s) {
            Integer i = parseOptInt(lineNo, s);
            if (i == null) {
                error("Line " + lineNo + ": " + s + " is not a valid number");
                i = 0;
            }
            return i;
        }

        protected Integer parseOptInt(int lineNo, String s) {
            int i;
            try {
                if (s.startsWith("$")) {
                    i = Integer.parseInt(s.substring(1, 16));
                } else {
                    i = Integer.parseInt(s);
                }
                if (i < 0 || i > 255) {
                    error("Line " + lineNo + ": " + s + " is out of range");
                    i = 0;
                }
            } catch (NumberFormatException e) {
                Integer c = consts.get(s);
                if (c == null) {
                    return null;
                }
                i = c;
            }
            return i;
        }
    }

    private ParamHandler nullHandler = new BasicParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            if (params.size() > 0) {
                error("Line " + lineNum + ": No parameters expected");
            }
            memory[pc++] = opcode << 8 | 0;
        }
    };

    private ParamHandler orgHandler = new BasicParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            if (!checkParamSize(lineNum, 1, params)) {
                return;
            }
            pc = parseInt(lineNum, params.get(0));
        }
    };

    private ParamHandler dataHandler = new BasicParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            for (String param : params) {
                int val = parseInt(lineNum, param);
                memory[pc++] = val;
            }
        }
    };

    private ParamHandler addressHandler = new BasicParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            if (!checkParamSize(lineNum, 1, params)) {
                return;
            }
            String param = params.get(0);
            Integer val = parseOptInt(lineNum, param);
            if (val == null) {
                Integer i = labels.get(param);
                if (i == null) {
                    addPendingReference(param, pc);
                    val = 0;
                } else {
                    val = i;
                }
            }
            memory[pc++] = opcode << 8 | val;
        }
    };

    private ParamHandler constHandler = new BasicParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            if (!checkParamSize(lineNum, 1, params)) {
                return;
            }
            int i = parseInt(lineNum, params.get(0));
            memory[pc++] = opcode << 8 | i;
        }
    };

    private ParamHandler optConstHandler = new BasicParamHandler() {
        @Override
        public void generate(int lineNum, int opcode, List<String> params) {
            int i = 0;
            if (params.size() != 0) {
                if (!checkParamSize(lineNum, 1, params)) {
                    return;
                }
                i = parseInt(lineNum, params.get(0));
            }
            memory[pc++] = opcode << 8 | i;
        }
    };

    private Map<String, OpDesc> ops = ImmutableMap.<String, OpDesc>builder()
            .put(".ORG", new OpDesc(0, orgHandler))
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
    private Map<String, Integer> consts = new HashMap<String, Integer>();
    private Map<String, List<Integer>> pendingReferences = new HashMap<String, List<Integer>>();
    private List<String> errors;

    private List<String> text;
    private String outputName;

    public Assembler(Reader input) throws IOException {
        loadSource(input);
    }

    public Assembler(String source) {
        try {
            loadSource(new StringReader(source));
        } catch (IOException e) {
            throw new RuntimeException("Can't happen");
        }
    }

    private void loadSource(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        text = new LinkedList<String>();
        String line = in.readLine();
        while(line != null) {
            text.add(line);
            line = in.readLine();
        }
    }

    public void assemble() {
        labels = new HashMap<String, Integer>();
        consts = new HashMap<String, Integer>();
        pendingReferences = new HashMap<String, List<Integer>>();
        errors = Lists.newArrayList();
        pc = 0;

        for (int i = 0; i < text.size(); i++) {
            handleLine(i+1, text.get(i));
        }
        if (pendingReferences.size() > 0) {
            for (String label : pendingReferences.keySet()) {
                error("Unresolved label " + label);
            }
        }
        if (pc > 256) {
            error("Program too large");
        }
    }

    private void handleLine(int lineNum, String line) {
        // Format: [label] [op/directive] [param] {"," param }

        // remove comments
        int idx = line.indexOf(";");
        if (idx >= 0) {
            line = line.substring(0, idx);
        }
        if (line.trim().isEmpty()) {
            return;
        }

        int curPos = 0;

        // label
        String label = "";
        if (Character.isLetter(line.charAt(curPos))) {
            while (Character.isLetterOrDigit(line.charAt(curPos))) {
                label += line.charAt(curPos++);
            }
        }

        // skip whitespace
        while (Character.isWhitespace(line.charAt(curPos))) {
            curPos++;
        };

        // opcode
        String opcode = "" + line.charAt(curPos++);
        while (Character.isLetterOrDigit(line.charAt(curPos))) {
            opcode += line.charAt(curPos++);
        }

        // skip whitespace
        while (curPos < line.length() && Character.isWhitespace(line.charAt(curPos))) {
            curPos++;
        }

        // parameters
        List<String> params;
        String rawParams = line.substring(curPos).trim();
        if (rawParams.isEmpty()) {
            params = Lists.newArrayList();
        } else {
            params = Lists.newArrayList(rawParams.split(",")).stream()
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

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

    private void addPendingReference(String label, int address) {
        List<Integer> pendingRefs = pendingReferences.get(label);
        if (pendingRefs == null) {
            pendingRefs = Lists.newLinkedList();
        }
        pendingRefs.add(address);
        pendingReferences.put(label, pendingRefs);
    }

    private void error(String msg) {
        errors.add(msg);
    }

    public byte[] getCode() {
        int size = pc > 127 ? 512 : 256;
        byte[] code = new byte[size];
        for (int i = 0; i < pc; i++) {
            code[2 * i + 0] = (byte)((memory[i] >> 8) & 0xff);
            code[2 * i + 1] = (byte)(memory[i] & 0xff);
        }
        return code;
    }

    public List<String> getErrors() {
        return errors;
    }

    private static void usage() {
        System.err.println("Usage: Assembler <input-file> <output-file>");
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            usage();
            return;
        }
        try {
            Assembler assembler = new Assembler(new FileReader(args[0]));
            assembler.assemble();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

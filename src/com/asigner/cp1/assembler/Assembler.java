package com.asigner.cp1.assembler;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: asigner
 * Date: 3/28/11
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Assembler {

    private interface ParamHandler {


    }

    private static ParamHandler nullHandler = new ParamHandler() {};
    private static ParamHandler addressHandler = new ParamHandler() {};
    private static ParamHandler constHandler = new ParamHandler() {};
    private static ParamHandler optConstHandler = new ParamHandler() {};

    private static enum Mnemonic {
        HLT(1, nullHandler),
        ANZ(2, nullHandler),
        VZG(3, constHandler),
        AKO(4, constHandler),
        LDA(5, addressHandler),
        ABS(6, addressHandler),
        ADD(7, addressHandler),
        SUB(8, addressHandler),
        SPU(9, addressHandler),
        VGL(10, addressHandler),
        SPB(11, addressHandler),
        VGR(12, addressHandler),
        VKL(13, addressHandler),
        NEG(14, nullHandler),
        UND(15, addressHandler),
        P1E(16, optConstHandler),
        P1A(17, optConstHandler),
        P2A(18, optConstHandler),
        LIA(19, addressHandler),
        AIS(20, addressHandler),
        SIU(21, addressHandler),
        P3E(22, optConstHandler),
        P4A(23, optConstHandler),
        P5A(24, optConstHandler);

        private final int opCode;
        private final ParamHandler paramHandler;

        private Mnemonic(int opCode, ParamHandler paramHandler) {
            this.opCode = opCode;
            this.paramHandler = paramHandler;
        }
    }

    private final Map<String, Mnemonic> ops = new HashMap<String, Mnemonic>();
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

        for(Mnemonic m : Mnemonic.values()) {
            ops.put(m.name(), m);
        }
    }

    private void go() {
        for (int i = 0; i < text.size(); i++) {
            handleLine(text.get(i));
        }
    }

    private void handleLine(String line) {
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

        // parameter
        String param = "";
        while (!Character.isWhitespace(line.charAt(curPos))) {
            param += line.charAt(curPos++);
        }

        // add and resolve label
        if (!label.isEmpty()) {
            addLabel(label, pc);
        }

        // find opcode
        Mnemonic m = ops.get(opcode);
        if (m == null) {
            error("Unknown mnemonic");
        }

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

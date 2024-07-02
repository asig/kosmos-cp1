#pragma once

#include <functional>
#include <vector>
#include <map>

#include <QString>

namespace kosmos_cp1::assembler {

class Assembler
{
public:    
    Assembler(const QString& source);

    std::vector<QString> assemble();
    std::vector<std::uint8_t> code();

private:
    typedef void (Assembler::*ParamHandler)(int lineNo, std::uint8_t opcode, const std::vector<QString> params);

    struct OpDesc {
        std::uint8_t opcode;
        ParamHandler paramHandler;
    };

    void handleLine(int lineNum, QString line);

    void addLabel(const std::string& label, int address);
    void addPendingReference(const std::string& label, int address);
    void error(const QString& err);
    void error(int lineNo, const QString& err);
    bool isIdentStart(QChar c);
    bool isIdentPart(QChar c);

    bool checkParamSize(int lineNo, int expectedParams, const std::vector<QString> params);
    std::uint8_t parseIntOrLabel(int lineNo, const QString s);
    std::uint8_t parseIntOrLabelOrUnknown(int lineNo, const QString s);
    std::uint8_t parseIntOrLabelOrUnknownInternal(int lineNo, const QString s, bool allowUnknown);

    void orgHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void dataHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void equHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void rawHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void nullHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void constHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void addressHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);
    void optConstHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params);

    std::unordered_map<std::string, OpDesc> ops_;

    std::vector<QString> text_;

    std::unordered_map<std::string, int> labels_;
    std::unordered_map<std::string, int> consts_;
    std::unordered_map<std::string, std::vector<int>> pendingReferences_;
    std::vector<QString> errors_;
    std::vector<std::uint16_t> memory_;
    int pc_;
};

} // namespace kosmos_cp1::assembler



//    private abstract class BasicParamHandler implements ParamHandler {
//        protected boolean checkParamSize(int lineNo, int expectedParams, List<String> params) {
//            if (params.size() != expectedParams) {
//                error(String.format("Line %d: %d params expected, but %d params encountered.", lineNo, expectedParams, params.size()));
//                return false;
//            }
//            return true;
//        }

//        protected Integer parseIntOrLabel(int lineNo, String s) {
//            return parseIntOrLabelOrUnknownInternal(lineNo, s, false);
//        }

//        protected Integer parseIntOrLabelOrUnknown(int lineNo, String s) {
//            return parseIntOrLabelOrUnknownInternal(lineNo, s, true);
//        }

//        protected Integer parseIntOrLabelOrUnknownInternal(int lineNo, String s, boolean allowUnknown) {
//            int i;
//            try {
//                if (s.startsWith("$")) {
//                    i = Integer.parseInt(s.substring(1, 16));
//                } else {
//                    i = Integer.parseInt(s);
//                }
//                if (i < 0 || i > 255) {
//                    error("Line " + lineNo + ": " + s + " is out of range");
//                    i = 0;
//                }
//            } catch (NumberFormatException e) {
//                if (allowUnknown && "?".equals(s)) {
//                    // Treat "unknown" as 0
//                    return 0;
//                }
//                Integer c = consts.get(s);
//                if (c == null) {
//                    c = labels.get(s);
//                    if (c == null) {
//                        addPendingReference(s, pc);
//                        c = 0;
//                    }
//                }
//                i = c;
//            }
//            return i;
//        }
//    }

//    private ParamHandler nullHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            if (params.size() > 0) {
//                error("Line " + lineNum + ": No parameters expected");
//            }
//            memory[pc++] = opcode << 8 | 0;
//        }
//    };

//    private ParamHandler orgHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            if (!checkParamSize(lineNum, 1, params)) {
//                return;
//            }
//            pc = parseIntOrLabel(lineNum, params.get(0));
//        }
//    };

//    private ParamHandler equHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            if (!checkParamSize(lineNum, 2, params)) {
//                return;
//            }
//            String name = params.get(0);
//            if (consts.containsKey(name)) {
//                error(String.format("Line %d: name %s is already used.", lineNum, name));
//                return;
//            }
//            int val = parseIntOrLabel(lineNum, params.get(1));
//            consts.put(name, val);
//        }
//    };

//    private ParamHandler dataHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            for (String param : params) {
//                int val = parseIntOrLabelOrUnknown(lineNum, param);
//                memory[pc++] = val;
//            }
//        }
//    };

//    private ParamHandler rawHandler = new BasicParamHandler() {
//        private final Pattern rawPattern = Pattern.compile("([0-9]{2})\\.([0-9]{3})");

//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            for (String param : params) {
//                Matcher m = rawPattern.matcher(param);
//                if (!m.matches()) {
//                    error("Line " + lineNum + ": " + param + " is not a valid raw value.");
//                    continue;
//                }
//                int msb = Integer.valueOf(m.group(1));
//                int lsb = Integer.valueOf(m.group(2));
//                if (msb > 255 || lsb > 255) {
//                    error("Line " + lineNum + ": " + param + " is not a valid raw value.");
//                    continue;
//                }
//                memory[pc++] = (msb << 8) | lsb;
//            }
//        }
//    };

//    private ParamHandler addressHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            if (!checkParamSize(lineNum, 1, params)) {
//                return;
//            }
//            String param = params.get(0);
//            Integer val = parseIntOrLabel(lineNum, param);
//            memory[pc++] = opcode << 8 | val;
//        }
//    };

//    private ParamHandler constHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            if (!checkParamSize(lineNum, 1, params)) {
//                return;
//            }
//            int i = parseIntOrLabel(lineNum, params.get(0));
//            memory[pc++] = opcode << 8 | i;
//        }
//    };

//    private ParamHandler optConstHandler = new BasicParamHandler() {
//        @Override
//        public void generate(int lineNum, int opcode, List<String> params) {
//            int i = 0;
//            if (params.size() != 0) {
//                if (!checkParamSize(lineNum, 1, params)) {
//                    return;
//                }
//                i = parseIntOrLabel(lineNum, params.get(0));
//            }
//            memory[pc++] = opcode << 8 | i;
//        }
//    };

//    private Map<String, OpDesc> ops = ImmutableMap.<String, OpDesc>builder()
//            .put(".ORG", new OpDesc(0, orgHandler))
//            .put(".DB", new OpDesc(0, dataHandler))
//            .put(".EQU", new OpDesc(0, equHandler))
//            .put(".RAW", new OpDesc(0, rawHandler))
//            .put("HLT", new OpDesc(1, nullHandler))
//            .put("ANZ", new OpDesc(2, nullHandler))
//            .put("VZG", new OpDesc(3, constHandler))
//            .put("AKO", new OpDesc(4, constHandler))
//            .put("LDA", new OpDesc(5, addressHandler))
//            .put("ABS", new OpDesc(6, addressHandler))
//            .put("ADD", new OpDesc(7, addressHandler))
//            .put("SUB", new OpDesc(8, addressHandler))
//            .put("SPU", new OpDesc(9, addressHandler))
//            .put("VGL", new OpDesc(10, addressHandler))
//            .put("SPB", new OpDesc(11, addressHandler))
//            .put("VGR", new OpDesc(12, addressHandler))
//            .put("VKL", new OpDesc(13, addressHandler))
//            .put("NEG", new OpDesc(14, nullHandler))
//            .put("UND", new OpDesc(15, addressHandler))
//            .put("P1E", new OpDesc(16, optConstHandler))
//            .put("P1A", new OpDesc(17, optConstHandler))
//            .put("P2A", new OpDesc(18, optConstHandler))
//            .put("LIA", new OpDesc(19, addressHandler))
//            .put("AIS", new OpDesc(20, addressHandler))
//            .put("SIU", new OpDesc(21, addressHandler))
//            .put("P3E", new OpDesc(22, optConstHandler))
//            .put("P4A", new OpDesc(23, optConstHandler))
//            .put("P5A", new OpDesc(24, optConstHandler))
//            .build();

//    private final int memory[] = new int[256];
//    private int pc = 0;

//    private Map<String, Integer> labels = new HashMap<String, Integer>();
//    private Map<String, Integer> consts = new HashMap<String, Integer>();
//    private Map<String, List<Integer>> pendingReferences = new HashMap<String, List<Integer>>();
//    private List<String> errors;

//    private List<String> text;
//    private String outputName;

//    public Assembler(Reader input) throws IOException {
//        loadSource(input);
//    }

//    public Assembler(String source) {
//        try {
//            loadSource(new StringReader(source));
//        } catch (IOException e) {
//            throw new RuntimeException("Can't happen");
//        }
//    }

//    private void loadSource(Reader reader) throws IOException {
//        text = IOUtils.readLines(reader);
//    }

//    public void assemble() {
//        labels = new HashMap<String, Integer>();
//        consts = new HashMap<String, Integer>();
//        pendingReferences = new HashMap<String, List<Integer>>();
//        errors = Lists.newArrayList();
//        pc = 0;

//        for (int i = 0; i < text.size(); i++) {
//            handleLine(i+1, text.get(i));
//        }
//        if (pendingReferences.size() > 0) {
//            for (String label : pendingReferences.keySet()) {
//                error("Unresolved label " + label);
//            }
//        }
//        if (pc > 256) {
//            error("Program too large");
//        }
//    }

//    private void handleLine(int lineNum, String line) {
//        // Format: [label] [op/directive] [param] {"," param }

//        // remove comments
//        int idx = line.indexOf(";");
//        if (idx >= 0) {
//            line = line.substring(0, idx);
//        }
//        if (line.trim().isEmpty()) {
//            return;
//        }

//        int curPos = 0;

//        // label
//        String label = "";
//        if (isIdentStart(line.charAt(curPos))) {
//            while (curPos < line.length() && isIdentPart(line.charAt(curPos))) {
//                label += line.charAt(curPos++);
//            }
//        }

//        // skip whitespace
//        while (curPos < line.length() && Character.isWhitespace(line.charAt(curPos))) {
//            curPos++;
//        }

//        // add and resolve label
//        if (!label.isEmpty()) {
//            addLabel(label, pc);
//        }

//        if (curPos >= line.length()) {
//            // No content, move on
//            return;
//        }

//        // opcode
//        String opcode = "" + line.charAt(curPos++);
//        while (curPos < line.length() && Character.isLetterOrDigit(line.charAt(curPos))) {
//            opcode += line.charAt(curPos++);
//        }

//        // skip whitespace
//        while (curPos < line.length() && Character.isWhitespace(line.charAt(curPos))) {
//            curPos++;
//        }

//        // parameters
//        List<String> params;
//        String rawParams = line.substring(curPos).trim();
//        if (rawParams.isEmpty()) {
//            params = Lists.newArrayList();
//        } else {
//            params = Lists.newArrayList(rawParams.split(",")).stream()
//                    .map(String::trim)
//                    .collect(Collectors.toList());
//        }

//        // find opcode
//        OpDesc op = ops.get(opcode);
//        if (op == null) {
//            error("Line " + lineNum + ": " + opcode + " is an unknown mnemonic");
//        } else {
//            op.handler.generate(lineNum, op.opCode, params);
//        }
//    }

//    private boolean isIdentStart(char c) {
//        return Character.isLetter(c) || c == '_';
//    }

//    private boolean isIdentPart(char c) {
//        return Character.isLetterOrDigit(c) || c == '_';
//    }

//    private void addLabel(String label, int address) {
//        labels.put(label, address);
//        List<Integer> pendingRefs = pendingReferences.get(label);
//        if (pendingRefs != null) {
//            for (int i : pendingRefs) {
//                memory[i] = memory[i] & 0xff00 | (0xff & address);
//            }
//            pendingReferences.remove(label);
//        }
//    }

//    private void addPendingReference(String label, int address) {
//        List<Integer> pendingRefs = pendingReferences.get(label);
//        if (pendingRefs == null) {
//            pendingRefs = Lists.newLinkedList();
//        }
//        pendingRefs.add(address);
//        pendingReferences.put(label, pendingRefs);
//    }

//    private void error(String msg) {
//        errors.add(msg);
//    }

//    public byte[] getCode() {
//        int size = pc > 127 ? 512 : 256;
//        byte[] code = new byte[size];
//        for (int i = 0; i < pc; i++) {
//            code[2 * i + 0] = (byte)((memory[i] >> 8) & 0xff);
//            code[2 * i + 1] = (byte)(memory[i] & 0xff);
//        }
//        return code;
//    }

//    public List<String> getErrors() {
//        return errors;
//    }

//    private static void usage() {
//        System.err.println("Usage: Assembler <input-file> <output-file>");
//    }

//    public static void main(String args[]) {
//        if (args.length != 2) {
//            usage();
//            return;
//        }
//        try {
//            Assembler assembler = new Assembler(new FileReader(args[0]));
//            assembler.assemble();
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//    }
//}

#pragma once

#include <cstdint>

#include <QObject>

#include "fmt/format.h"

namespace kosmos_cp1 {
namespace util {

using std::uint16_t;
using std::uint8_t;

class Disassembler : public QObject
{
    Q_OBJECT
public:
    struct Line {
        uint16_t addr;
        std::vector<uint8_t> bytes;
        std::string opcode;
        std::vector<std::string> args;

        std::string disassembly() const {
            std::string hexDump = "";
            for (int i = 0; i < bytes.size(); i++) {
                hexDump += (i > 0 ? " " : "");
                hexDump += fmt::format("{:02x}", bytes[i]);
            }
            while (hexDump.size() < 2*2 + 1) {
                hexDump += " ";
            }

            std::string res = "[ " + hexDump + " ] " + fmt::format("{: <4} ", opcode);
            for (int i = 0; i < args.size(); i++) {
                res += (i == 0 ? "" : ", ");
                res += args[i];
            }
            return res;
        }
    };

    explicit Disassembler(const std::vector<uint8_t>& rom, QObject *parent = nullptr);

    std::vector<Line> disassemble() const;
    std::vector<Line> disassemble(uint16_t from, uint16_t to) const;
    Line disassembleSingleLine(uint16_t start) const;

private:
    const std::vector<uint8_t> rom_;

    Line line(uint16_t curPos, int bytes, const std::string& op, const std::string& arg) const;
    Line line(uint16_t curPos, int bytes, const std::string& op, const std::string& arg1, const std::string& arg2) const;
    Line line(uint16_t curPos, int bytes, const std::string& op, const std::vector<std::string>& args) const;

};

} // namespace util
} // namespace kosmos_cp1




//public static void main(String ... args) {
//    try {
//        Rom rom = new Rom(Disassembler.class.getResourceAsStream("/com/asigner/cp1/CP1.bin"));
//        List<Line> lines = new Disassembler(rom).disassemble(0, 0x800);
//        for(Line line : lines) {
//            System.out.printf("$%04x: [ %s ] %s\n", line.getAddress(), line.getBytes(), line.getDisassembly());
//        }
//    } catch (IOException e) {
//        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    }
//}

//}

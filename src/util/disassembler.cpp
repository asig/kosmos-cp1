#include "disassembler.h"

#include <fmt/format.h>

namespace kosmos_cp1 {
namespace util {

namespace {

std::string formatAddr(uint16_t addr) {
    return fmt::format("${:04x}", addr);
}

std::string formatConst(uint8_t val) {
    return fmt::format("#${:02x}", val);
}

}

Disassembler::Disassembler(const std::vector<uint8_t>& rom, QObject *parent)
    : rom_(rom), QObject{parent}
{
}

Disassembler::Line Disassembler::disassembleSingleLine(uint16_t start) const {
    return disassemble(start, start + 1)[0];
}

std::vector<Disassembler::Line> Disassembler::disassemble() const {
    return disassemble(0, rom_.size());
}

std::vector<Disassembler::Line> Disassembler::disassemble(uint16_t from, uint16_t to) const {
    std::vector<Line> lines;
    uint16_t pos = from;
    while (pos < to) {
        uint16_t curPos = pos;
        uint8_t op = rom_[pos++];
        uint16_t addr;
        int r, p, b;
        int data;
        int ofs;

        switch(op) {
        case 0x00:
            lines.push_back(line(curPos, 1, "NOP", std::vector<std::string>{}));
            break;

        case 0x02:
            lines.push_back(line(curPos, 1, "OUTL", "BUS", "A"));
            break;

        case 0x03:
            data = rom_[pos++];
            lines.push_back(line(curPos, 2, "ADD", "A", formatConst(data)));
            break;

        case 0x04: case 0x24: case 0x44: case 0x64: case 0x84: case 0xa4: case 0xc4: case 0xe4:
            addr = (op & 0xe0) << 3 | rom_[pos++];
            lines.push_back(line(curPos, 2, "JMP", formatAddr(addr)));
            break;

        case 0x05:
            lines.push_back(line(curPos, 1, "EN", "I"));
            break;

        case 0x07:
            lines.push_back(line(curPos, 1, "DEC", "A"));
            break;

        case 0x08:
            lines.push_back(line(curPos, 1, "INS", "A", "BUS"));
            break;

        case 0x09: case 0x0a:
            p = op & 0x3;
            lines.push_back(line(curPos, 1, "IN", "A", fmt::format("P{}",p)));
            break;

        case 0x0c: case 0x0d: case 0x0e: case 0x0f:
            p = op & 0x3;
            lines.push_back(line(curPos, 1, "MOVD", "A", fmt::format("P{}",4 + p)));
            break;

        case 0x10: case 0x11:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "INC",  fmt::format("@R{}",r)));
            break;

        case 0x12: case 0x32: case 0x52: case 0x72: case 0x92: case 0xb2: case 0xd2: case 0xf2:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            b = (op >> 5) & 0x7;
            lines.push_back(line(curPos, 2, fmt::format("JB{}",b), formatAddr(addr)));
            break;

        case 0x13:
            data = rom_[pos++] & 0xff;
            lines.push_back(line(curPos, 2, "ADDC", "A", formatConst(data)));
            break;

        case 0x14: case 0x34: case 0x54: case 0x74: case 0x94: case 0xb4: case 0xd4: case 0xf4:
            addr = (op & 0xe0) << 3 | rom_[pos++];
            lines.push_back(line(curPos, 2, "CALL", formatAddr(addr)));
            break;

        case 0x15:
            lines.push_back(line(curPos, 1, "DIS", "I"));
            break;

        case 0x16:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JTF", formatAddr(addr)));
            break;

        case 0x17:
            lines.push_back(line(curPos, 1, "INC", "A"));
            break;

        case 0x18: case 0x19: case 0x1a: case 0x1b: case 0x1c: case 0x1d: case 0x1e: case 0x1f:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "INC", fmt::format("R{}",r)));
            break;

        case 0x20: case 0x21:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "XCH", "A", fmt::format("@R{}", r)));
            break;

        case 0x23:
            data = rom_[pos++] & 0xff;
            lines.push_back(line(curPos, 2, "MOV", "A", formatConst(data)));
            break;

        case 0x25:
            lines.push_back(line(curPos, 1, "EN", "TCNTI"));
            break;

        case 0x26:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JNT0", formatAddr(addr)));
            break;

        case 0x27:
            lines.push_back(line(curPos, 1, "CLR", "A"));
            break;

        case 0x28: case 0x29: case 0x2a: case 0x2b: case 0x2c: case 0x2d: case 0x2e: case 0x2f:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "XCH", "A", fmt::format("R{}",r)));
            break;

        case 0x30: case 0x31:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "XCHD", "A", fmt::format("@R{}",r)));
            break;

        case 0x35:
            lines.push_back(line(curPos, 1, "DIS", "TCNTI"));
            break;

        case 0x36:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JT0", formatAddr(addr)));
            break;

        case 0x37:
            lines.push_back(line(curPos, 1, "CPL", "A"));
            break;

        case 0x39: case 0x3a:
            p = op & 0x3;
            lines.push_back(line(curPos, 1, "OUTL", fmt::format("P{}",p), "A"));
            break;

        case 0x3c: case 0x3d: case 0x3e: case 0x3f:
            p = op & 0x3;
            lines.push_back(line(curPos, 1, "MOVD", fmt::format("P{}",4+p), "A"));
            break;

        case 0x40: case 0x41:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "ORL", "A", fmt::format("@R{}",r)));
            break;

        case 0x42:
            lines.push_back(line(curPos, 1, "MOV", "A", "T"));
            break;

        case 0x43:
            data = rom_[pos++] & 0xff;
            lines.push_back(line(curPos, 2, "ORL", "A", formatConst(data)));
            break;

        case 0x45:
            lines.push_back(line(curPos, 1, "STRT", "CNT"));
            break;

        case 0x46:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JNT1", formatAddr(addr)));
            break;

        case 0x47:
            lines.push_back(line(curPos, 1, "SWAP", "A"));
            break;

        case 0x48: case 0x49: case 0x4a: case 0x4b: case 0x4c: case 0x4d: case 0x4e: case 0x4f:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "ORL", "A", fmt::format("R{}",r)));
            break;

        case 0x50: case 0x51:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "ANL", "A", fmt::format("@R{}",r)));
            break;

        case 0x53:
            data = rom_[pos++];
            lines.push_back(line(curPos, 2, "ANL", "A", formatConst(data)));
            break;

        case 0x55:
            lines.push_back(line(curPos, 1, "STRT", "T"));
            break;

        case 0x56:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JT1", formatAddr(addr)));
            break;

        case 0x57:
            lines.push_back(line(curPos, 1, "DA", "A"));
            break;

        case 0x58: case 0x59: case 0x5a: case 0x5b: case 0x5c: case 0x5d: case 0x5e: case 0x5f:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "ANL", "A", fmt::format("R{}",r)));
            break;

        case 0x60: case 0x61:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "ADD", "A", fmt::format("@R{}",r)));
            break;

        case 0x62:
            lines.push_back(line(curPos, 1, "MOV", "T", "A"));
            break;

        case 0x65:
            lines.push_back(line(curPos, 1, "STOP", "TCNT"));
            break;

        case 0x67:
            lines.push_back(line(curPos, 1, "RRC", "A"));
            break;

        case 0x68: case 0x69: case 0x6a: case 0x6b: case 0x6c: case 0x6d: case 0x6e: case 0x6f:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "ADD", "A", fmt::format("R{}",r)));
            break;

        case 0x70: case 0x71:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "ADDC", "A", fmt::format("@R{}",r)));
            break;

        case 0x75:
            lines.push_back(line(curPos, 1, "ENT0", "CLK"));
            break;

        case 0x76:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JF1", formatAddr(addr)));
            break;

        case 0x77:
            lines.push_back(line(curPos, 1, "RR", "A"));
            break;

        case 0x78: case 0x79: case 0x7a: case 0x7b: case 0x7c: case 0x7d: case 0x7e: case 0x7f:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "ADDC", "A", fmt::format("R{}",r)));
            break;

        case 0x80: case 0x81:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "MOVX", "A", fmt::format("@R{}",r)));
            break;

        case 0x83:
            lines.push_back(line(curPos, 1, "RET", std::vector<std::string>{}));
            break;

        case 0x85:
            lines.push_back(line(curPos, 1, "CLR", "F0"));
            break;

        case 0x86:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JNI", formatAddr(addr)));
            break;

        case 0x88:
            data = rom_[pos++];
            lines.push_back(line(curPos, 2, "ORL", "BUS", formatConst(data)));
            break;

        case 0x89: case 0x8a:
            data = rom_[pos++];
            p = op & 0x3;
            lines.push_back(line(curPos, 2, "ORL", fmt::format("P{}",p), formatConst(data)));
            break;

        case 0x8c: case 0x8d: case 0x8e: case 0x8f:
            p = op & 0x3;
            lines.push_back(line(curPos, 1, "ORLD", fmt::format("P{}",4+p), "A"));
            break;

        case 0x90: case 0x91:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "MOVX", fmt::format("@R{}",r), "A"));
            break;

        case 0x93:
            lines.push_back(line(curPos, 1, "RETR", std::vector<std::string>{}));
            break;

        case 0x95:
            lines.push_back(line(curPos, 1, "CPL", "F0"));
            break;

        case 0x96:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JNZ", formatAddr(addr)));
            break;

        case 0x97:
            lines.push_back(line(curPos, 1, "CLR", "C"));
            break;

        case 0x98:
            data = rom_[pos++];
            lines.push_back(line(curPos, 2, "ANL", "BUS", formatConst(data)));
            break;

        case 0x99: case 0x9a:
            data = rom_[pos++];
            p = op & 0x3;
            lines.push_back(line(curPos, 2, "ANL", fmt::format("P{}",p), formatConst(data)));
            break;

        case 0x9c: case 0x9d: case 0x9e: case 0x9f:
            p = op & 0x3;
            lines.push_back(line(curPos, 1, "ANLD", fmt::format("P{}",4+p), "A"));
            break;

        case 0xa0: case 0xa1:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "MOV", fmt::format("@R{}",r), "A"));
            break;

        case 0xa3:
            lines.push_back(line(curPos, 1, "MOVP", "A", "@A"));
            break;

        case 0xa5:
            lines.push_back(line(curPos, 1, "CLR", "F1"));
            break;

        case 0xa7:
            lines.push_back(line(curPos, 1, "CPL", "C"));
            break;

        case 0xa8: case 0xa9: case 0xaa: case 0xab: case 0xac: case 0xad: case 0xae: case 0xaf:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "MOV", fmt::format("R{}",r), "A"));
            break;

        case 0xb0: case 0xb1:
            data = rom_[pos++];
            r = op & 0x1;
            lines.push_back(line(curPos, 2, "MOV", fmt::format("@R{}",r), formatConst(data)));
            break;

        case 0xb3:
            lines.push_back(line(curPos, 1, "JMPP", "@A"));
            break;

        case 0xb5:
            lines.push_back(line(curPos, 1, "CPL", "F1"));
            break;

        case 0xb6:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JF0", formatAddr(addr)));
            break;

        case 0xb8: case 0xb9: case 0xba: case 0xbb: case 0xbc: case 0xbd: case 0xbe: case 0xbf:
            data = rom_[pos++];
            r = op & 0x7;
            lines.push_back(line(curPos, 2, "MOV", fmt::format("R{}",r), formatConst(data)));
            break;

        case 0xc5:
            lines.push_back(line(curPos, 1, "SEL", "RB0"));
            break;

        case 0xc6:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JZ", formatAddr(addr)));
            break;

        case 0xc7:
            lines.push_back(line(curPos, 1, "MOV", "A", "PSW"));
            break;

        case 0xc8: case 0xc9: case 0xca: case 0xcb: case 0xcc: case 0xcd: case 0xce: case 0xcf:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "DEC", fmt::format("R{}",r)));
            break;

        case 0xd0: case 0xd1:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "XRL", "A", fmt::format("@R{}",r)));
            break;

        case 0xd3:
            data = rom_[pos++];
            lines.push_back(line(curPos, 2, "XRL", "A", formatConst(data)));
            break;

        case 0xd5:
            lines.push_back(line(curPos, 1, "SEL", "RB1"));
            break;

        case 0xd7:
            lines.push_back(line(curPos, 1, "MOV", "PSW", "A"));
            break;

        case 0xd8: case 0xd9: case 0xda: case 0xdb: case 0xdc: case 0xdd: case 0xde: case 0xdf:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "XRL", "A", fmt::format("R{}",r)));
            break;

        case 0xe3:
            lines.push_back(line(curPos, 1, "MOVP3", "A", "@A"));
            break;

        case 0xe5:
            lines.push_back(line(curPos, 1, "SEL", "MB0"));
            break;

        case 0xe6:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JNC", formatAddr(addr)));
            break;

        case 0xe7:
            lines.push_back(line(curPos, 1, "RL", "A"));
            break;

        case 0xe8: case 0xe9: case 0xea: case 0xeb: case 0xec: case 0xed: case 0xee: case 0xef:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            r = op & 0x7;
            lines.push_back(line(curPos, 2, "DJNZ", fmt::format("R{}",r), formatAddr(addr)));
            break;

        case 0xf0: case 0xf1:
            r = op & 0x1;
            lines.push_back(line(curPos, 1, "MOV", "A", fmt::format("@R{}",r)));
            break;

        case 0xf5:
            lines.push_back(line(curPos, 1, "SEL", "MB1"));
            break;

        case 0xf6:
            ofs = rom_[pos++];
            addr = (curPos & 0xf00) | (ofs & 0xff);
            lines.push_back(line(curPos, 2, "JC", formatAddr(addr)));
            break;

        case 0xf7:
            lines.push_back(line(curPos, 1, "RLC", "A"));
            break;

        case 0xf8: case 0xf9: case 0xfa: case 0xfb: case 0xfc: case 0xfd: case 0xfe: case 0xff:
            r = op & 0x7;
            lines.push_back(line(curPos, 1, "MOV", "A", fmt::format("R{}",r)));
            break;

        case 0x01:
        case 0x06:
        case 0x0b:
        case 0x22:
        case 0x33:
        case 0x38:
        case 0x3b:
        case 0x63:
        case 0x66:
        case 0x73:
        case 0x82:
        case 0x87:
        case 0x8b:
        case 0x9b:
        case 0xa2:
        case 0xa6:
        case 0xb7:
        case 0xc0:
        case 0xc1:
        case 0xc2:
        case 0xc3:
        case 0xd6:
        case 0xe0:
        case 0xe1:
        case 0xe2:
        case 0xf3:
            lines.push_back(line(curPos, 1, ".DB", formatConst(op)));
            break;
        }
    }
    return lines;
}

Disassembler::Line Disassembler::line(uint16_t curPos, int bytes, const std::string& op, const std::string& arg) const {
    return line(curPos, bytes, op, std::vector<std::string>{arg});
}

Disassembler::Line Disassembler::line(uint16_t curPos, int bytes, const std::string& op, const std::string& arg1, const std::string& arg2) const {
    return line(curPos, bytes, op, std::vector<std::string>{arg1, arg2});
}

Disassembler::Line Disassembler::line(uint16_t curPos, int bytes, const std::string& op, const std::vector<std::string>& args) const {
    Line l;

    for (int i = 0; i < bytes; i++) {
        uint8_t v = rom_[curPos + i];
        l.bytes.push_back(v);
    }
    l.addr = curPos;
    l.opcode = op;
    l.args = args;
    return l;
}

} // namespace util
} // namespace kosmos_cp1

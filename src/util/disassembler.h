/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
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

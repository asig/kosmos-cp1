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


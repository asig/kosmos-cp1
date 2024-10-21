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

#include "assembler.h"

#include <QStringList>
#include <QRegularExpression>
#include <QRegularExpressionMatch>

namespace kosmos_cp1::assembler {

Assembler::Assembler(const QString& source) {

    ops_ = {
        {".ORG", {0, &Assembler::orgHandler}},
        {".DB" , {0, &Assembler::dataHandler}},
        {".EQU", {0, &Assembler::equHandler}},
        {".RAW", {0, &Assembler::rawHandler}},
        {"HLT", {1, &Assembler::nullHandler}},
        {"ANZ", {2, &Assembler::nullHandler}},
        {"VZG", {3, &Assembler::constHandler}},
        {"AKO", {4, &Assembler::constHandler}},
        {"LDA", {5, &Assembler::addressHandler}},
        {"ABS", {6, &Assembler::addressHandler}},
        {"ADD", {7, &Assembler::addressHandler}},
        {"SUB", {8, &Assembler::addressHandler}},
        {"SPU", {9, &Assembler::addressHandler}},
        {"VGL", {10, &Assembler::addressHandler}},
        {"SPB", {11, &Assembler::addressHandler}},
        {"VGR", {12, &Assembler::addressHandler}},
        {"VKL", {13, &Assembler::addressHandler}},
        {"NEG", {14, &Assembler::nullHandler}},
        {"UND", {15, &Assembler::addressHandler}},
        {"P1E", {16, &Assembler::optConstHandler}},
        {"P1A", {17, &Assembler::optConstHandler}},
        {"P2A", {18, &Assembler::optConstHandler}},
        {"LIA", {19, &Assembler::addressHandler}},
        {"AIS", {20, &Assembler::addressHandler}},
        {"SIU", {21, &Assembler::addressHandler}},
        {"P3E", {22, &Assembler::optConstHandler}},
        {"P4A", {23, &Assembler::optConstHandler}},
        {"P5A", {24, &Assembler::optConstHandler}},
    };

    QStringList lines = source.split('\n');
    text_.assign(lines.begin(), lines.end());
}


std::vector<QString> Assembler::assemble() {
    memory_.clear();
    errors_.clear();
    labels_.clear();
    consts_.clear();
    pendingReferences_.clear();

    pc_ = 0;
    int i = 1;
    for (auto line : text_) {
        handleLine(i++, line);
    }
    if (pendingReferences_.size() > 0) {
        for (auto tp : pendingReferences_) {
            error(QString::fromStdString("Unresolved label " + tp.first));
        }
    }
    if (pc_ > 256) {
        error(QString("Program too large"));
    }
    return errors_;
}

std::vector<std::uint8_t> Assembler::code() {
    std::vector<std::uint8_t> code;
    code.resize(pc_ > 127 ? 512 : 256);
    for (int i = 0; i < pc_; i++) {
        code[2 * i + 0] = (memory_[i] >> 8) & 0xff;
        code[2 * i + 1] = memory_[i] & 0xff;
    }
    return code;
}

void Assembler::addLabel(const std::string& label, int address) {
    if (labels_.find(label) != labels_.end()) {
        error(QString::asprintf("Label %s already defined", label.c_str()));
    }
    labels_[label] = address;
    std::vector<int> pendingRefs = pendingReferences_[label];
    for (int i : pendingRefs) {
        memory_[i] = memory_[i] & 0xff00 | (0xff & address);
    }
    pendingReferences_.erase(label);
}

void Assembler::addPendingReference(const std::string& label, int address) {
    std::vector<int> pendingRefs = pendingReferences_[label];
    pendingRefs.push_back(address);
    pendingReferences_[label] = pendingRefs;
}


void Assembler::error(const QString& err) {
    errors_.push_back(err);
}

void Assembler::error(int lineNo, const QString& err) {
    errors_.push_back(QString::asprintf("Line %d: %s", lineNo, err.toStdString().c_str()));
}

void Assembler::handleLine(int lineNo, QString line) {
    // Format: [label] [op/directive] [param] {"," param }

    // remove comments
    int idx = line.indexOf(";");
    if (idx >= 0) {
        line = line.left(idx);
    }
    if (line.trimmed().isEmpty()) {
        return;
    }

    int curPos = 0;

    // label
    QString label = "";
    if (isIdentStart(line.at(curPos))) {
        while (curPos < line.length() && isIdentPart(line.at(curPos))) {
            label += line.at(curPos++);
        }
    }

    // skip whitespace
    while (curPos < line.length() && line.at(curPos).isSpace()) {
        curPos++;
    }

    // add and resolve label
    if (!label.isEmpty()) {
        addLabel(label.toStdString(), pc_);
    }

    if (curPos >= line.length()) {
        // No content, move on
        return;
    }

    // opcode
    QString opcode = line.at(curPos++);
    while (curPos < line.length() && line.at(curPos).isLetterOrNumber()) {
        opcode += line.at(curPos++);
    }

    // skip whitespace
    while (curPos < line.length() && line.at(curPos).isSpace()) {
        curPos++;
    }

    // parameters
    std::vector<QString> params;
    QString rawParams = line.sliced(curPos).trimmed();

    if (!rawParams.isEmpty()) {
        auto split = rawParams.split(',');
        for (auto s : split) {
            params.push_back(s.trimmed());
        }
    }

    // find opcode
    auto it = ops_.find(opcode.toStdString());
    if (it == ops_.end()) {
        error(QString::asprintf("Line %d: %s is an unknown mnemonic", lineNo, opcode.toStdString().c_str()));
    } else {
        auto handler = it->second.paramHandler;
        std::invoke(handler, this, lineNo, it->second.opcode, params);
    }
}

bool Assembler::isIdentStart(QChar c) {
    return c.isLetter() || c == '_';
}

bool Assembler::isIdentPart(QChar c) {
    return c.isLetterOrNumber() || c == '_';
}

bool Assembler::checkParamSize(int lineNo, int expectedParams, const std::vector<QString> params) {
    if (params.size() != expectedParams) {
        error(lineNo, QString::asprintf("%d params expected, but %zu params encountered.", expectedParams, params.size()));
        return false;
    }
    return true;
}

std::uint8_t Assembler::parseIntOrLabel(int lineNo, const QString s) {
    return parseIntOrLabelOrUnknownInternal(lineNo, s, false);
}

std::uint8_t Assembler::parseIntOrLabelOrUnknown(int lineNo, const QString s) {
    return parseIntOrLabelOrUnknownInternal(lineNo, s, true);
}

std::uint8_t Assembler::parseIntOrLabelOrUnknownInternal(int lineNo, const QString s, bool allowUnknown) {
    int i;
    bool ok;
    if (s.startsWith("$")) {
        i = s.right(s.length()-1).toInt(&ok, 16);
    } else {
        i = s.toInt(&ok);
    }
    if (ok) {
        if (i < 0 || i > 255) {
            error(lineNo, s + " is out of range");
            i = 0;
        }
        return i;
    }

    if (allowUnknown && s == "?") {
        // Treat "unknown" as 0
        return 0;
    }

    // Try consts
   auto it = consts_.find(s.toStdString());
   if (it != consts_.end()) {
       return it->second;
   }

   // Not a const, try labels_
   it = labels_.find(s.toStdString());
   if (it != labels_.end()) {
       return it->second;
   }

    // Also not a label, assume it is just unresolved.
    addPendingReference(s.toStdString(), pc_);
    return 0;
}

void Assembler::orgHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    if (!checkParamSize(lineNo, 1, params)) {
        return;
    }
    pc_ = parseIntOrLabel(lineNo, params[0]);
    memory_.resize(pc_);
}

void Assembler::dataHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    for (auto param : params) {
        int val = parseIntOrLabelOrUnknown(lineNo, param);
        memory_.push_back(val);
        pc_++;
    }
}

void Assembler::equHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    if (!checkParamSize(lineNo, 2, params)) {
        return;
    }
    QString name = params[0];
    if (consts_.find(name.toStdString()) != consts_.end()) {
        error(lineNo, QString::asprintf("Name %s is already used.", name.toStdString().c_str()));
        return;
    }
    int val = parseIntOrLabel(lineNo, params[1]);
    consts_[name.toStdString()] = val;
}

void Assembler::rawHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    static QRegularExpression re("([0-9]{2})\\.([0-9]{3})");
    for (QString param : params) {
        QRegularExpressionMatch match = re.match(param);
        if (!match.hasMatch()) {
            error(lineNo, param + " is not a valid raw value.");
            continue;
        }
        int msb = match.captured(1).toInt();
        int lsb = match.captured(2).toInt();
        if (msb > 255 || lsb > 255) {
            error(lineNo, param + " is not a valid raw value.");
            continue;
        }
        memory_.push_back((msb << 8) | lsb);
        pc_++;
    }
}

void Assembler::nullHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    if (params.size() > 0) {
        error(lineNo, "No parameters expected");
    }
    memory_.push_back(opcode << 8 | 0);
    pc_++;
}

void Assembler::constHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    if (!checkParamSize(lineNo, 1, params)) {
        return;
    }
    int i = parseIntOrLabel(lineNo, params[0]);
    memory_.push_back(opcode << 8 | i);
    pc_++;
}

void Assembler::addressHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    if (!checkParamSize(lineNo, 1, params)) {
        return;
    }
    auto param = params[0];
    std::uint8_t val = parseIntOrLabel(lineNo, param);
    memory_.push_back(opcode << 8 | val);
    pc_++;
}

void Assembler::optConstHandler(int lineNo, std::uint8_t opcode, const std::vector<QString> params) {
    int i = 0;
    if (params.size() != 0) {
        if (!checkParamSize(lineNo, 1, params)) {
            return;
        }
        i = parseIntOrLabel(lineNo, params[0]);
    }
    memory_.push_back(opcode << 8 | i);
    pc_++;
}

} // namespace kosmos_cp1::assembler

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


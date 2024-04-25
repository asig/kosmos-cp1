#pragma once

#include <string>
#include <cstdint>

#include <QObject>

namespace kosmos_cp1::emulation {

using std::uint8_t;

class DataPort : public QObject
{
    Q_OBJECT
public:
    explicit DataPort(const std::string& name, QObject *parent = nullptr);

    uint8_t read() const;
    void write(uint8_t value, uint8_t mask = 0xff);

signals:
    void bit0Written(uint8_t val);
    void bit1Written(uint8_t val);
    void bit2Written(uint8_t val);
    void bit3Written(uint8_t val);
    void bit4Written(uint8_t val);
    void bit5Written(uint8_t val);
    void bit6Written(uint8_t val);
    void bit7Written(uint8_t val);

    void valueChange(uint8_t oldValue, uint8_t newValue);

private:
    std::string name_;
    uint8_t value_;

    typedef void (DataPort::*BitWrittenFunc)(uint8_t);
    static BitWrittenFunc bitSignals[8];

};

}

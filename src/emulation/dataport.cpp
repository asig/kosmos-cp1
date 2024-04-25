#include "dataport.h"

namespace kosmos_cp1::emulation {

DataPort::BitWrittenFunc DataPort::bitSignals[8] = {
    &DataPort::bit0Written,
    &DataPort::bit1Written,
    &DataPort::bit2Written,
    &DataPort::bit3Written,
    &DataPort::bit4Written,
    &DataPort::bit5Written,
    &DataPort::bit6Written,
    &DataPort::bit7Written,
};

DataPort::DataPort(const std::string& name, QObject *parent)
    : name_(name), value_(0), QObject{parent}
{
}

uint8_t DataPort::read() const {
    return value_;
}

void DataPort::write(uint8_t value, uint8_t mask) {
    value = value & mask;
    if (value != (value_ & mask)) {
        uint8_t oldVal = value_;
        value_ = (value_ & ~mask) | value;
        emit valueChange(oldVal, value_);

        // Send single bits to pins, if necessary
        for (int bit = 0; bit < 8; bit++) {
            if (((1 << bit) & mask) > 0) {
                uint8_t oldBit = (oldVal & (1 << bit)) > 0 ? 1 : 0;
                uint8_t newBit = (value_ & (1 << bit)) > 0 ? 1 : 0;
                if (oldBit != newBit) {
                    (*this.*bitSignals[bit])(newBit);
                }
            }
        }
    }
}

} // namespace kosmos_cp1::emulation

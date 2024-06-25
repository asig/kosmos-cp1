#include "emulation/ram.h"

#include <QDebug>

namespace kosmos_cp1::emulation {

Ram::Ram(std::uint16_t size, QObject *parent)
    : QObject{parent}    
{
    content_.resize(size);
    qDebug() << "Size is " << content_.size();
}

void Ram::clear() {
    for (int i = 0; i < content_.size(); i++) {
        content_[i] = 0;
    }
    emit memoryCleared();
}

std::uint8_t Ram::read(std::uint16_t addr) const {
    return content_[addr];
}

void Ram::write(std::uint16_t addr, std::uint8_t val) {
    content_[addr] = val;
    emit memoryWritten(addr, val);
}

} // namespace kosmos_cp1::emulation

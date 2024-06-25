#pragma once

#include <stdint.h>
#include <vector>

#include <QObject>

namespace kosmos_cp1::emulation {

class Ram : public QObject
{
    Q_OBJECT
public:
    explicit Ram(std::uint16_t size, QObject *parent = nullptr);

    void clear();

    int size() const {
        return content_.size();
    }

    std::uint8_t read(std::uint16_t addr) const;
    void write(std::uint16_t addr, std::uint8_t val);

signals:
    void memoryWritten(std::uint16_t addr, std::uint8_t val);
    void memoryCleared();

private:
    std::vector<std::uint8_t> content_;
};

} // namespace kosmos_cp1::emulation

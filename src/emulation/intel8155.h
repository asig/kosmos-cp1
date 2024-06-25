#pragma once

#include <cstdint>
#include <memory>

#include <QObject>

#include "emulation/dataport.h"
#include "emulation/ram.h"

namespace kosmos_cp1::emulation {

using std::uint8_t;
using std::uint16_t;

enum class Port {
    A, B, C
};

enum class PortMode {
    INPUT, OUTPUT
};

enum class PortCMode {
    ALT1, ALT2, ALT3, ALT4
};

// TODO(asigner):
// - ALE should happen once per cycle. This is currently not the case, but done in MOVX directly
// - Signal /RD and /WR in other BUS operations as well.
class Intel8155 : public QObject {
    Q_OBJECT

public:
    Intel8155(const std::string& name, std::shared_ptr<DataPort> bus);

    void reset();

    bool ioValue() const {
        return ioValue_;
    }

    bool ceValue() const {
        return ceValue_;
    }
    bool aleValue() const {
        return aleValue_;
    }
    bool rdValue() const {
        return rdValue_;
    }

    bool wrValue() const {
        return wrValue_;
    }

    PortMode paMode() const {
        return paMode_;
    }

    PortMode pbMode() const {
        return pbMode_;
    }

    PortCMode pcMode() const {
        return pcMode_;
    }

    bool paInterruptEnabled() const {
        return paInterruptEnabled_;
    }

    bool pbInterruptEnabled() const {
        return pbInterruptEnabled_;
    }

    uint8_t paValue() const {
        return paValue_;
    }

    uint8_t pbValue() const {
        return pbValue_;
    }

    uint8_t pcValue() const {
        return pcValue_;
    }

    const Ram *ram() {
        return &ram_;
    }

public slots:
    void onPinALEWritten(uint8_t val);
    void onPinRDLowActiveWritten(uint8_t val);
    void onPinWRLowActiveWritten(uint8_t val);
    void onPinIOWritten(uint8_t val);
    void onPinResetWritten(uint8_t val);
    void onPinCELowActiveWritten(uint8_t val);

signals:
    void commandRegisterWritten();
    void portWritten(Port port, uint8_t value);
    void memoryWritten(uint16_t addr, uint8_t value);
    void pinsChanged();
    void resetExecuted();

private:
    std::string name_;
    std::shared_ptr<DataPort> bus_;
    Ram ram_;

    uint8_t addressLatch_;
    bool ioValue_;
    bool ceValue_;
    bool aleValue_;
    bool rdValue_;
    bool wrValue_;

    PortMode paMode_ = PortMode::INPUT;
    PortMode pbMode_ = PortMode::INPUT;
    PortCMode pcMode_ = PortCMode::ALT1;
    bool paInterruptEnabled_ = false;
    bool pbInterruptEnabled_ = false;

    uint8_t paValue_;
    uint8_t pbValue_;
    uint8_t pcValue_;
};

} // namespace

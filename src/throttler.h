#pragma once

#include <cstdint>

#include <QObject>

namespace kosmos_cp1 {

using std::uint32_t;

class Throttler : public QObject
{
    Q_OBJECT
public:
    explicit Throttler(uint32_t millis, QObject *parent = nullptr);

    void throttle();

private:
    uint32_t millis_;
    uint32_t next_;
};

} // namespace kosmos_cp1


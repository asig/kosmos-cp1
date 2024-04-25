#pragma once

#include <cstdint>

#include <QObject>

namespace kosmos_cp1 {

using std::uint64_t;

class PerformanceMeasurer : public QObject {
    Q_OBJECT
public:
    explicit PerformanceMeasurer(QObject *parent = nullptr);

    void reset();
    void registerExecution(uint32_t cycles);
    bool isUpdateDue();

    double getPerformance() const;

private:
    uint64_t startNanos_;
    uint64_t lastCheckNanos_;
    uint64_t cycles_;
    uint64_t cyclesTotal_;
};

}

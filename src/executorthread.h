#pragma once

#include <deque>
#include <thread>
#include <cstdint>
#include <set>
#include <condition_variable>

#include <QObject>

#include "emulation/intel8049.h"
#include "emulation/intel8155.h"

#include "performancemeasurer.h"
#include "throttler.h"

namespace kosmos_cp1 {

using emulation::Intel8049;
using emulation::Intel8155;

using std::uint16_t;

class ExecutorThread : public QObject {
    Q_OBJECT

public:

    enum class Command {
        NIL,
        SINGLE_STEP,
        START,
        STOP,
        RESET,
        QUIT
    };

    explicit ExecutorThread(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, QObject *parent = nullptr);

    void start();
    void join();
    void postCommand(Command cmd);

signals:
    void executionStarted();
    void executionStopped();
    void resetExecuted();
    void breakpointHit(uint16_t addr);
    void performanceUpdate(double performance);

private:
    std::mutex commandsMutex_;
    std::condition_variable commandsCv_;
    std::deque<Command> commands_;
    std::thread thread_;

    bool breakOnMovx_;
    bool isThrottled_;
    bool isRunning_;
    uint32_t  interruptsSeen_;
    std::set<uint16_t> breakpoints_;
    PerformanceMeasurer performanceMeasurer_;
    Throttler throttler_;

    Intel8049 *cpu_;
    Intel8155 *pid_;
    Intel8155 *pidExtension_;

    void threadLoop();

    Command fetchCommand();

    void startExecution();
    void stopExecution();
    void singleStep();
    uint32_t executeInstr();
        void reset();


};

} // namespace

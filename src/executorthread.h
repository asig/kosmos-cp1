#pragma once

#include <deque>
#include <cstdint>
#include <set>
#include <condition_variable>

#include <QObject>
#include <QThread>

#include "emulation/intel8049.h"
#include "emulation/intel8155.h"

#include "performancemeasurer.h"
#include "throttler.h"

namespace kosmos_cp1 {

using emulation::Intel8049;
using emulation::Intel8155;

using std::uint16_t;

class ExecutorThread : public QThread {
    Q_OBJECT

public:
    explicit ExecutorThread(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, QObject *parent = nullptr);

    void singleStep();
    void startExecution();
    void stopExectuion();
    void reset();
    void quit();
    void enableBreakpoint(uint16_t addr, bool enabled);

    bool isRunning() const;

signals:
    void executionStarted();
    void executionStopped();
    void resetExecuted();
    void breakpointHit(uint16_t addr);
    void performanceUpdate(double performance);

protected:
    void run() override;

private:
    enum class Op {
        NIL,
        SINGLE_STEP,
        START,
        STOP,
        RESET,
        QUIT,
        ADD_BP,
        REMOVE_BP,
    };

    struct Command {
        Op op;
        uint32_t param;
    };

    std::mutex commandsMutex_;
    std::condition_variable commandsCv_;
    std::deque<Command> commands_;

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

    void postCommand(Command cmd);
    Command fetchCommand();

    void doStartExecution();
    void doStopExecution();
    void doSingleStep();
    uint32_t doExecuteInstr();
    void doReset();
};

} // namespace

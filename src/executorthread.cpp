#include "executorthread.h"

#include <mutex>

namespace {

    // Throttling happens on timer interrupts. Timer interrupts happen every 2560 μs, so let's just throttle
    // every other interrupt, and approximate the delay with 5 ms (instead of 5120 μs) so that we can use the fast
    // currentTimeMillis() method.
    constexpr const int32_t THROTTLE_INTERVAL_MS = 5;
    constexpr const int32_t THROTTLE_INTERRUPTS = 2;
}

namespace kosmos_cp1 {

ExecutorThread::ExecutorThread(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, QObject *parent)
    : QThread{parent},
    cpu_(cpu),  pid_(pid), pidExtension_(pidExtension), breakOnMovx_(false), isThrottled_(true), isRunning_(false), interruptsSeen_(0), throttler_(THROTTLE_INTERVAL_MS)
{
    setObjectName("ExecutorThread");
}

void ExecutorThread::postCommand(Command cmd) {
    std::unique_lock<std::mutex> lock(commandsMutex_);
    commands_.push_back(cmd);
    commandsCv_.notify_one();
}

void ExecutorThread::run() {
    for(;;) {
        Command command = fetchCommand();
        switch(command) {
        case Command::NIL: {
            bool wasInInterrupt = cpu_->state().inInterrupt;
            uint32_t executed = executeInstr();
            bool isInInterrupt = cpu_->state().inInterrupt;
            performanceMeasurer_.registerExecution(executed);
            if (!wasInInterrupt && isInInterrupt) {
                // Kosmos CP1 uses a timer interrupt that fires every 2560 μs, so let's try and sync
                // on 5 millis every 2 interrupts.
                interruptsSeen_ = (interruptsSeen_ + 1) % THROTTLE_INTERRUPTS;
                if (interruptsSeen_ == 0) {
                    if (isThrottled_) {
                        throttler_.throttle();
                    }
                }
            }
            if (performanceMeasurer_.isUpdateDue()) {
                double performance = performanceMeasurer_.getPerformance();
                emit performanceUpdate(performance);
            }
        }
            break;
        case Command::SINGLE_STEP:
            singleStep();
            break;
        case Command::START:
            startExecution();
            break;
        case Command::STOP:
            stopExecution();
            break;
        case Command::RESET:
            reset();
            break;
        case Command::QUIT:
            return;
        default:
            break;
        }
    }
}

void ExecutorThread::singleStep() {
    stopExecution();
    executeInstr();
}

void ExecutorThread::startExecution() {
    if (!isRunning_) {
        interruptsSeen_ = 0;
        isRunning_ = true;
        performanceMeasurer_.reset();
        emit executionStarted();    }
}

void ExecutorThread::stopExecution() {
    if (isRunning_) {
        isRunning_ = false;
        emit executionStopped();
    }
}

void ExecutorThread::reset() {
    stopExecution();
    // TODO(asigner): Reset should be done by setting the reset line.
    cpu_->reset();
    pid_->reset();
    pidExtension_->reset();
    emit resetExecuted();
}

ExecutorThread::Command ExecutorThread::fetchCommand() {
    Command command = Command::NIL;
    std::unique_lock<std::mutex> lock(commandsMutex_);

    if (isRunning_) {
        if (commands_.size() > 0) {
            command = commands_.front();
            commands_.pop_front();
        }
    } else {
        commandsCv_.wait(lock, [this] { return !commands_.empty(); });
        command = commands_.front();
        commands_.pop_front();
    }
    lock.unlock();
    return command;
}

uint32_t ExecutorThread::executeInstr() {
    uint32_t cycles = cpu_->executeSingleInstr();
    if (breakOnMovx_) {
        int op = cpu_->peek();
        if (op == 0x80 || op == 0x81 || op == 0x90 || op == 0x91) {
            isRunning_ = false;
                emit breakpointHit(cpu_->state().pc);
            emit executionStopped();
        }
    }
    if (breakpoints_.contains(cpu_->state().pc)) {
        isRunning_ = false;
        emit breakpointHit(cpu_->state().pc);
        emit executionStopped();
    }
    return cycles;
}


}

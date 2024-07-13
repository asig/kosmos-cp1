/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

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

void ExecutorThread::singleStep() {
    postCommand(Command{ .op = Op::SINGLE_STEP });
}

void ExecutorThread::startExecution() {
    postCommand(Command{ .op = Op::START });
}

void ExecutorThread::stopExectuion() {
    postCommand(Command{ .op = Op::STOP });
}

void ExecutorThread::reset() {
    postCommand(Command{ .op = Op::RESET });
}

void ExecutorThread::quit() {
    postCommand(Command{ .op = Op::QUIT });
}

void ExecutorThread::enableBreakpoint(uint16_t addr, bool enabled) {
    postCommand(Command{
                    .op = enabled ? Op::ADD_BP : Op::REMOVE_BP,
                    .param = addr,
                });
}

void ExecutorThread::setBreakOnMovX(bool brk) {
    postCommand(Command{
                    .op = Op::BREAK_ON_MOVX,
                    .param = brk,
                });
}

bool ExecutorThread::isRunning() const {
    return isRunning_;
}

void ExecutorThread::postCommand(Command cmd) {
    std::unique_lock<std::mutex> lock(commandsMutex_);
    commands_.push_back(cmd);
    commandsCv_.notify_one();
}

void ExecutorThread::run() {
    for(;;) {
        Command command = fetchCommand();
        switch(command.op) {
        case Op::NIL: {
            bool wasInInterrupt = cpu_->state().inInterrupt;
            uint32_t executed = doExecuteInstr();
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
        case Op::SINGLE_STEP:
            doSingleStep();
            break;
        case Op::START:
            doStartExecution();
            break;
        case Op::STOP:
            doStopExecution();
            break;
        case Op::RESET:
            doReset();
            break;
        case Op::QUIT:
            return;
        case Op::ADD_BP:
            breakpoints_.insert(command.param);
            break;
        case Op::REMOVE_BP:
            breakpoints_.erase(command.param);
            break;
        case Op::BREAK_ON_MOVX:
            breakOnMovx_ = command.param;
            break;
        default:
            break;
        }
    }
}

void ExecutorThread::doSingleStep() {
    doStopExecution();
    doExecuteInstr();
}

void ExecutorThread::doStartExecution() {
    if (!isRunning_) {
        interruptsSeen_ = 0;
        isRunning_ = true;
        performanceMeasurer_.reset();
        emit executionStarted();    }
}

void ExecutorThread::doStopExecution() {
    if (isRunning_) {
        isRunning_ = false;
        emit executionStopped();
    }
}

void ExecutorThread::doReset() {
    doStopExecution();
    // TODO(asigner): Reset should be done by setting the reset line.
    cpu_->reset();
    pid_->reset();
    pidExtension_->reset();
    emit resetExecuted();
}

ExecutorThread::Command ExecutorThread::fetchCommand() {
    Command command{.op = Op::NIL};
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

uint32_t ExecutorThread::doExecuteInstr() {
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

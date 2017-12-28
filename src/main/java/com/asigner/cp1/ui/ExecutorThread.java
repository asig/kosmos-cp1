/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
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

package com.asigner.cp1.ui;

import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.emulation.PerformanceMeasurer;
import com.asigner.cp1.emulation.Throttler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static com.asigner.cp1.ui.ExecutorThread.Command.NIL;

public class ExecutorThread extends Thread {

    // Throttling happens on timer interrupts. Timer interrupts happen every 2560 μs, so let's just throttle
    // every other interrupt, and approximate the delay with 5 ms (instead of 5120 μs) so that we can use the fast
    // currentTimeMillis() method.
    private final static long THROTTLE_INTERVAL_MS = 5;
    private final static int THROTTLE_INTERRUPTS = 2;

    private static final Logger logger = Logger.getLogger(ExecutorThread.class.getName());

    public interface ExecutionListener {
        void executionStarted();
        void executionStopped();
        void resetExecuted();
        void breakpointHit(int addr);
        void performanceUpdate(double performance);
    }

    public enum Command {
        NIL,
        SINGLE_STEP,
        START,
        STOP,
        RESET,
        QUIT
    };

    private final BlockingQueue<Command> commands = new LinkedBlockingQueue<Command>();
    private final Set<Integer> breakpoints = new HashSet<Integer>();
    private final List<ExecutionListener> listeners = new LinkedList<ExecutionListener>();
    private final Intel8049 cpu;
    private final Intel8155 pid;
    private final Intel8155 pidExtension;
    private final PerformanceMeasurer performanceMeasurer = new PerformanceMeasurer();

    // If throttle, then we throttle every 2 timer interrupts
    private final Throttler throttler = new Throttler(THROTTLE_INTERVAL_MS);

    private boolean isRunning = false;
    private boolean breakOnMovx = false;
    private boolean isThrottled = true;
    private int interruptsSeen = 0;

    public ExecutorThread(Intel8049 cpu, Intel8155 pid, Intel8155 pidExtension) {
        this.cpu = cpu;
        this.pid = pid;
        this.pidExtension = pidExtension;
    }

    public void addListener(ExecutionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ExecutionListener listener) {
        listeners.remove(listener);
    }

    public void setBreakOnMovx(boolean enabled) {
        this.breakOnMovx = enabled;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isThrottled() {
        return isThrottled;
    }

    public void setThrottled(boolean throttled) {
        isThrottled = throttled;
    }

    public void enableBreakpoint(int addr, boolean enabled) {
        if (enabled) {
            breakpoints.add(addr);
        } else {
            breakpoints.remove(addr);
        }
    }

    public void postCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void run() {
        for(;;) {
            Command command = fetchCommand();
            switch(command) {
                case NIL:
                    boolean wasInInterrupt = cpu.isInInterrupt();
                    int executed = executeInstr();
                    boolean isInInterrupt = cpu.isInInterrupt();
                    performanceMeasurer.register(executed);
                    if (!wasInInterrupt && isInInterrupt) {
                        // Kosmos CP1 uses a timer interrupt that fires every 2560 μs, so let's try and sync
                        // on 5 millis every 2 interrupts.
                        interruptsSeen = (interruptsSeen + 1) % THROTTLE_INTERRUPTS;
                        if (interruptsSeen == 0) {
                            if (isThrottled) {
                                throttler.throttle();
                            }
                        }
                    }
                    if (performanceMeasurer.isUpdateDue()) {
                        double performance = performanceMeasurer.getPerformance();
                        listeners.forEach(l -> l.performanceUpdate(performance));
                    }
                    break;
                case SINGLE_STEP:
                    singleStep();
                    break;
                case START:
                    startExecution();
                    break;
                case STOP:
                    stopExecution();
                    break;
                case RESET:
                    reset();
                    break;
                case QUIT:
                    return;
                default:
                    break;
            }
            yield();
        }
    }

    private void singleStep() {
        stopExecution();
        executeInstr();
    }

    private void startExecution() {
        if (!isRunning) {
            interruptsSeen = 0;
            isRunning = true;
            performanceMeasurer.reset();
            listeners.forEach(ExecutionListener::executionStarted);
        }
    }

    private void stopExecution() {
        if (isRunning) {
            isRunning = false;
            listeners.forEach(ExecutionListener::executionStopped);
        }
    }

    private void reset() {
        stopExecution();
        // TODO(asigner): Reset should be done by setting the reset line.
        cpu.reset();
        pid.reset();
        pidExtension.reset();
        listeners.forEach(ExecutionListener::resetExecuted);
    }

    private Command fetchCommand() {
        Command command = NIL;
        try {
            if (isRunning) {
                command = commands.peek();
                if (command != null) {
                    commands.take(); // also remove it
                } else {
                    command = NIL;
                }
            } else {
                command = commands.take();
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted while waiting for command");
        }
        return command;
    }

    private int executeInstr() {
        int cycles = cpu.executeSingleInstr();
        if (breakOnMovx) {
            int op = cpu.peek();
            if (op == 0x80 || op == 0x81 || op == 0x90 || op == 0x91) {
                isRunning = false;
                listeners.forEach(l -> l.breakpointHit(cpu.getPC()));
                listeners.forEach(ExecutionListener::executionStopped);
            }
        }
        if (breakpoints.contains(cpu.getPC())) {
            isRunning = false;
            listeners.forEach(l -> l.breakpointHit(cpu.getPC()));
            listeners.forEach(ExecutionListener::executionStopped);
        }
        return cycles;
    }
}

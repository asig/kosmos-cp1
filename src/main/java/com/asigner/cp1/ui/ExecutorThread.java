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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class ExecutorThread extends Thread {

    private static final Logger logger = Logger.getLogger(ExecutorThread.class.getName());

    public interface ExecutionListener {
        void executionStarted();
        void executionStopped();
        void resetExecuted();
        void breakpointHit(int addr);
        void performanceUpdate(double performance);
    }

    public enum Command {
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
    private boolean isRunning = false;
    private boolean breakOnMovx = false;

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
        PerformanceMeasurer performanceMeasurer = new PerformanceMeasurer();
        for(;;) {
            Command command = fetchCommand();
            if (command == null) {
                // can only happen if we're in state "running";
                int executed = executeInstr();

                performanceMeasurer.register(executed);
                if (performanceMeasurer.isUpdateDue()) {
                    double performance = performanceMeasurer.getPerformance();
                    listeners.forEach(l -> l.performanceUpdate(performance));
                }
            } else {
                switch(command) {
                    case SINGLE_STEP:
                        if (isRunning) {
                            stopExecution();
                            listeners.forEach(ExecutionListener::executionStopped);
                        }
                        executeInstr();
                        break;
                    case START:
                        startExecution();
                        performanceMeasurer.reset();
                        listeners.forEach(ExecutionListener::executionStarted);
                        break;
                    case STOP:
                        stopExecution();
                        listeners.forEach(ExecutionListener::executionStopped);
                        break;
                    case RESET:
                        stopExecution();
                        // TODO(asigner): Reset should be done by setting the reset line.
                        cpu.reset();
                        pid.reset();
                        pidExtension.reset();
                        listeners.forEach(ExecutionListener::resetExecuted);
                        break;
                    case QUIT:
                        return;
                    default:
                        break;
                }
            }
            yield();
        }
    }

    private void stopExecution() {
        isRunning = false;
    }

    private void startExecution() {
        isRunning = true;
    }

    private Command fetchCommand() {
        Command command = null;
        try {
            if (isRunning) {
                command = commands.peek();
                if (command != null) {
                    commands.take(); // also remove it
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

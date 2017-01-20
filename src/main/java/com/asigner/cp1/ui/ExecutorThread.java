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
    private boolean isRunning = false;
    private boolean breakOnMovx = false;

    public ExecutorThread(Intel8049 cpu, Intel8155 pid) {
        this.cpu = cpu;
        this.pid = pid;
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
        long start = 0;
        long cycles = 0;
        for(;;) {
            Command command = fetchCommand();
            if (command == null) {
                // can only happen if we're in state "running";
                cycles += executeInstr();
                if (cycles >= 2_000_000) {
                    long dt = System.nanoTime() - start;
                    double expectedCycles = dt / 2500.0; // 400 kHz -> 2.5 μs per cycle = 2500 ns per cycle
                    double ratio = cycles / expectedCycles;
                    logger.finest("Performance: effective cycles = " + cycles + " took " + dt + " nanos, expected cycles = " + expectedCycles + ", performance = " + (ratio * 100) + "%");
                    start = System.nanoTime();
                    cycles =0;
                }
            } else {
                switch(command) {
                    case SINGLE_STEP:
                        if (isRunning) {
                            stopExecution();
                            listeners.forEach(ExecutionListener::executionStarted);
                        }
                        executeInstr();
                        break;
                    case START:
                        startExecution();
                        start = System.nanoTime();
                        cycles = 0;
                        listeners.forEach(ExecutionListener::executionStarted);
                        break;
                    case STOP:
                        stopExecution();
                        listeners.forEach(ExecutionListener::executionStopped);
                        break;
                    case RESET:
                        stopExecution();
                        cpu.reset();
                        pid.reset();
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

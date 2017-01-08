package com.asigner.cp1.emulation.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.Intel8155;

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
        for(;;) {
            Command command = fetchCommand();
            if (command == null) {
                // can only happen if we're in state "running";
                executeInstr();
            } else {
                switch(command) {
                    case SINGLE_STEP:
                        stopExecution();
                        executeInstr();
                        break;
                    case START:
                        startExecution();
                        executeInstr();
                        fireExecutionStarted();
                        break;
                    case STOP:
                        stopExecution();
                        fireExecutionStopped();
                        break;
                    case RESET:
                        stopExecution();
                        cpu.reset();
                        pid.reset();
                        fireResetExecuted();
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

    private void executeInstr() {
        cpu.executeSingleInstr();
        fireSingleStepped();
        if (breakOnMovx) {
            int op = cpu.peek();
            if (op == 0x80 || op == 0x81 || op == 0x90 || op == 0x91) {
                isRunning = false;
                fireBreakpointHit(cpu.getPC());
                fireExecutionStopped();
            }
        }
        if (breakpoints.contains(cpu.getPC())) {
            isRunning = false;
            fireBreakpointHit(cpu.getPC());
            fireExecutionStopped();
        }
    }

    private void fireExecutionStarted() {
        for (ExecutionListener listener : listeners) {
            listener.executionStarted();
        }
    }

    private void fireExecutionStopped() {
        for (ExecutionListener listener : listeners) {
            listener.executionStopped();
        }
    }

    private void fireSingleStepped() {
        for (ExecutionListener listener : listeners) {
            listener.singleStepped();
        }
    }

    private void fireResetExecuted() {
        for (ExecutionListener listener : listeners) {
            listener.resetExecuted();
        }
    }


    private void fireBreakpointHit(int addr) {
        for (ExecutionListener listener : listeners) {
            listener.breakpointHit(addr);
        }
    }
}

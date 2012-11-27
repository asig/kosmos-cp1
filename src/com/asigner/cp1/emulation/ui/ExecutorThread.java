package com.asigner.cp1.emulation.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.asigner.cp1.emulation.Cpu;

public class ExecutorThread extends Thread {

    private static final Logger logger = Logger.getLogger(ExecutorThread.class.getName());

    public enum Command {
        SINGLE_STEP,
        START,
        STOP,
        RESET
    };

    private final BlockingQueue<Command> commands = new LinkedBlockingQueue<Command>();
    private final Set<Integer> breakpoints = new HashSet<Integer>();
    private final List<BreakpointHitListener> listeners = new LinkedList<BreakpointHitListener>();
    private final Cpu cpu;
    private boolean isRunning = false;

    public ExecutorThread(Cpu cpu) {
        this.cpu = cpu;
    }

    public void addListener(BreakpointHitListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BreakpointHitListener listener) {
        listeners.remove(listener);
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
                    break;
                case STOP:
                    stopExecution();
                    break;
                case RESET:
                    stopExecution();
                    cpu.reset();
                    break;
                default:
                    break;
                }
            }
            yield();
        }
    }

    private void stopExecution() {
        isRunning = false;
        cpu.enableNotifications(true);
    }

    private void startExecution() {
        isRunning = true;
        cpu.enableNotifications(false);
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
        if (breakpoints.contains(cpu.getPC())) {
            cpu.enableNotifications(true);
            isRunning = false;
            fireBreakpointHit(cpu.getPC());
        }
    }

    private void fireBreakpointHit(int addr) {
        for (BreakpointHitListener listener : listeners) {
            listener.breakpointHit(addr);
        }
    }
}

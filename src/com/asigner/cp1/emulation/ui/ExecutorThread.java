package com.asigner.cp1.emulation.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.asigner.cp1.emulation.Cpu;

public class ExecutorThread extends Thread {

    public enum Command {
        SINGLE_STEP,
        START,
        STOP
    };

    private final Queue<Command> commands = new ConcurrentLinkedQueue<Command>();
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
            Command command = commands.poll();
            if (command != null) {
                switch(command) {
                case SINGLE_STEP:
                    isRunning = false;
                    executeInstr();
                    break;
                case START:
                    isRunning = true;
                    cpu.enableNotifications(false);
                    executeInstr();
                    break;
                case STOP:
                    isRunning = false;
                    cpu.enableNotifications(true);
                    break;
                }
            } else {
                if (isRunning) {
                    executeInstr();
                }
            }
            yield();
        }
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

package com.asigner.cp1.emulation.ui;

public interface ExecutionListener {
    void executionStarted();
    void executionStopped();
    void singleStepped();
    void resetExecuted();
    void breakpointHit(int addr);
}

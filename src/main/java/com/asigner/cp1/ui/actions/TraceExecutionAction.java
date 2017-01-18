package com.asigner.cp1.ui.actions;

import com.asigner.cp1.ui.CpuWindow;
import com.asigner.cp1.ui.ExecutorThread;

public class TraceExecutionAction extends BaseAction {

    private final CpuWindow cpuWindow;

    public TraceExecutionAction(ExecutorThread executorThread, CpuWindow cpuWindow) {
        super(executorThread, "Trace execution");
        this.cpuWindow = cpuWindow;
        this.setChecked(cpuWindow.isTraceExecution());
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        cpuWindow.setTraceExecution(checked);
    }
}

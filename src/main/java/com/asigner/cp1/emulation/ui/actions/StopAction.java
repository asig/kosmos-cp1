package com.asigner.cp1.emulation.ui.actions;

import com.asigner.cp1.ui.SWTResources;

import com.asigner.cp1.emulation.ui.CpuWindow;
import com.asigner.cp1.emulation.ui.ExecutorThread;

public class StopAction extends BaseAction {

    private SingleStepAction singleStepAction;
    private RunAction runAction;

    public StopAction(ExecutorThread executorThread, CpuWindow cpuWindow) {
        super(executorThread, "Stop", SWTResources.getImage("/com/asigner/cp1/emulation/ui/actions/control-stop-square.png"));
    }

    public void setDependentActions(SingleStepAction singleStepAction, RunAction runAction) {
        this.singleStepAction = singleStepAction;
        this.runAction = runAction;
    }

    @Override
    public void run() {
        this.singleStepAction.setEnabled(true);
        this.runAction.setEnabled(true);
        this.setEnabled(false);
        executorThread.postCommand(ExecutorThread.Command.STOP);
    }
}

package com.asigner.cp1.emulation.ui.actions;

import com.asigner.cp1.emulation.ui.SWTResources;

import com.asigner.cp1.emulation.ui.ExecutorThread;

public class RunAction extends BaseAction {

    private SingleStepAction singleStepAction;
    private StopAction stopAction;

    public RunAction(ExecutorThread executorThread) {
        super(executorThread, "Start", SWTResources.getImage("/com/asigner/cp1/emulation/ui/actions/control.png"));
    }

    public void setDependentActions(SingleStepAction singleStepAction, StopAction stopAction) {
        this.singleStepAction = singleStepAction;
        this.stopAction = stopAction;
    }

    @Override
    public void run() {
        this.singleStepAction.setEnabled(false);
        this.stopAction.setEnabled(true);
        this.setEnabled(false);
        executorThread.postCommand(ExecutorThread.Command.START);
    }
}

package com.asigner.cp1.emulation.ui.actions;

import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.ui.ExecutorThread;

public class RunAction extends BaseAction {

    private SingleStepAction singleStepAction;
    private StopAction stopAction;

    public RunAction(ExecutorThread executorThread) {
        super(executorThread, "Start", SWTResourceManager.getImage(RunAction.class, "control.png"));
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

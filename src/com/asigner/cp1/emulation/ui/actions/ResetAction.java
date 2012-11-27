package com.asigner.cp1.emulation.ui.actions;

import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.ui.ExecutorThread;

public class ResetAction extends BaseAction {

    private SingleStepAction singleStepAction;
    private RunAction runAction;
    private StopAction stopAction;

    public ResetAction(ExecutorThread executorThread) {
        super(executorThread, "Reset", SWTResourceManager.getImage(RunAction.class, "arrow-circle-135-left.png"));
    }

    public void setDependentActions(SingleStepAction singleStepAction, RunAction runAction, StopAction stopAction) {
        this.singleStepAction = singleStepAction;
        this.runAction = runAction;
        this.stopAction = stopAction;
    }

    @Override
    public void run() {
        singleStepAction.setEnabled(true);
        runAction.setEnabled(true);
        stopAction.setEnabled(false);
        executorThread.postCommand(ExecutorThread.Command.RESET);
    }
}

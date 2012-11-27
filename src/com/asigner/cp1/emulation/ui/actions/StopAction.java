package com.asigner.cp1.emulation.ui.actions;

import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.ui.CpuUI;
import com.asigner.cp1.emulation.ui.ExecutorThread;

public class StopAction extends BaseAction {

    private SingleStepAction singleStepAction;
    private RunAction runAction;
    private CpuUI cpuUi;

    public StopAction(ExecutorThread executorThread, CpuUI cpuUi) {
        super(executorThread, "Stop", SWTResourceManager.getImage(StopAction.class, "control-stop-square.png"));
        this.cpuUi = cpuUi;
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
        cpuUi.updateView();
    }
}

package com.asigner.cp1.emulation.ui.actions;

import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.ui.ExecutorThread;

public class SingleStepAction extends BaseAction {

    public SingleStepAction(ExecutorThread executorThread) {
        super(executorThread, "Step", SWTResourceManager.getImage(SingleStepAction.class, "arrow-step-over.png"));
    }

    @Override
    public void run() {
        executorThread.postCommand(ExecutorThread.Command.SINGLE_STEP);
    }
}

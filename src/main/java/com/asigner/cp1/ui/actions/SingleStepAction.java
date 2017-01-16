package com.asigner.cp1.ui.actions;

import com.asigner.cp1.ui.SWTResources;

import com.asigner.cp1.ui.ExecutorThread;

public class SingleStepAction extends BaseAction {

    public SingleStepAction(ExecutorThread executorThread) {
        super(executorThread, "Step", SWTResources.getImage("/com/asigner/cp1/ui/actions/arrow-step-over.png"));
    }

    @Override
    public void run() {
        executorThread.postCommand(ExecutorThread.Command.SINGLE_STEP);
    }
}

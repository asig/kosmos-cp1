// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.actions;

import com.asigner.cp1.ui.ExecutorThread;

public class BreakOnMovxAction extends BaseAction {

    public BreakOnMovxAction(ExecutorThread executorThread) {
        super(executorThread, "Break on MOVX");
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        executorThread.setBreakOnMovx(checked);
    }
}

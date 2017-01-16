package com.asigner.cp1.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.asigner.cp1.emulation.Intel8049;

public class SaveStateAction extends Action {

    private final Intel8049 cpu;

    public SaveStateAction(Intel8049 cpu) {
        super("Save state...");
        this.cpu = cpu;
    }

    @Override
    public void run() {
        MessageBox mbox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_INFORMATION);
        mbox.setMessage("Not implemented yet");
        mbox.open();
    }
}

package com.asigner.cp1.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class AboutAction extends Action {

    public AboutAction() {
        super("About");
    }

    @Override
    public void run() {
        MessageBox mbox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_INFORMATION);
        mbox.setMessage("Not implemented yet");
        mbox.open();
    }

}

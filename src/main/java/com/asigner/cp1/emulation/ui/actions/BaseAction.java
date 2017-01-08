package com.asigner.cp1.emulation.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.asigner.cp1.emulation.ui.ExecutorThread;

public class BaseAction extends Action {

    protected final ExecutorThread executorThread;

    public BaseAction(ExecutorThread executorThread, String text, Image image) {
        super(text, ImageDescriptor.createFromImage(image));
        this.executorThread = executorThread;
    }

    public BaseAction(ExecutorThread executorThread, String text) {
        super(text);
        this.executorThread = executorThread;
    }
}

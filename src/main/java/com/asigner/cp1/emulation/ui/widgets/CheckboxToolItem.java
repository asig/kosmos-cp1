// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation.ui.widgets;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class CheckboxToolItem extends ToolItem {

    public CheckboxToolItem(ToolBar parent, final Action action) {
        super(parent, SWT.SEPARATOR);
        Button ctrl = new Button(parent, SWT.CHECK);
        ctrl.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        ctrl.setText(action.getText());
        ctrl.pack();
        ctrl.setSelection(action.isChecked());
        ctrl.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.setChecked(ctrl.getSelection());
                action.run();
            }});
        this.setControl(ctrl);
        this.setWidth(ctrl.computeSize(-1, -1).x);
        this.setEnabled(action.isEnabled());
        action.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.ENABLED.equals(evt.getProperty())) {
                    setEnabled((Boolean)evt.getNewValue());
                }
            }});
    }

    @Override
    protected void checkSubclass() {
    }

}

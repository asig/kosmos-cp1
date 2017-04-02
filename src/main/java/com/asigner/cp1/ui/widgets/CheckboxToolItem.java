/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.asigner.cp1.ui.widgets;

import org.eclipse.jface.action.Action;
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

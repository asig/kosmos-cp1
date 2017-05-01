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
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class CheckboxContributionItem extends ControlContribution {

    private final Action action;

    public CheckboxContributionItem(Action action) {
        super(action.getId());
        this.action = action;
    }

    @Override
    protected Control createControl(Composite parent) {

        // At least on Linux, SWT draws a (partial?) border around the checkbox unless it is embedded in another
        // widget...
        final Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new FillLayout());

        Button ctrl = new Button(composite, SWT.CHECK);
        ctrl.setText(action.getText());
        ctrl.pack();
        ctrl.setSelection(action.isChecked());
        ctrl.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.setChecked(ctrl.getSelection());
                action.run();
            }
        });
        action.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.ENABLED.equals(evt.getProperty())) {
                    ctrl.setEnabled((Boolean) evt.getNewValue());
                }
            }
        });
        return composite;
    }
}

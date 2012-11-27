package com.asigner.cp1.emulation.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ActionButton extends Button {

    public ActionButton(Composite parent, int style, final Action action) {
        super(parent, style);
        this.setImage(action.getImageDescriptor().createImage());
        this.setText(action.getText());
        this.setEnabled(action.isEnabled());
        this.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.run();
            }});
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

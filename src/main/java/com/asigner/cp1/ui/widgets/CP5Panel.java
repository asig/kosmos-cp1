package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class CP5Panel extends Composite {
    /**
     *
     * Create the composite.
     * @param parent
     * @param style
     */
    public CP5Panel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);
        setBackground(CP1Colors.GREEN);

        Composite leds = new CP5Leds(this, SWT.NONE);

        Label label = new Label(this, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Composite composite2 = new CP5Switches(this, SWT.NONE);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

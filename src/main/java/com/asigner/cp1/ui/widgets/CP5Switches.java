package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CP5Switches extends Composite {
    /**
     *
     * Create the composite.
     * @param parent
     * @param style
     */
    public CP5Switches(Composite parent, int style) {
        super(parent, style);

        GridLayout layout1 = new GridLayout(8, false);
        layout1.horizontalSpacing = 5;
        setLayout(layout1);
        setBackground(CP1Colors.GREEN);

        GridLayout layout2 = new GridLayout(8, false);
        layout2.horizontalSpacing = 0;
        layout2.marginLeft = 3;
        layout2.marginRight = 3;
        setLayout(layout2);
        setBackground(CP1Colors.SWITCH_BG);
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(this, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

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

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout layout1 = new GridLayout(8, false);
        layout1.horizontalSpacing = 5;
        composite.setLayout(layout1);
        composite.setBackground(CP1Colors.GREEN);

        CP5Led p5Led1 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led2 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led3 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led4 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led5 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led6 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led7 = new CP5Led(composite, SWT.NONE);
        CP5Led p5Led8 = new CP5Led(composite, SWT.NONE);

        Label label = new Label(this, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Composite composite2 = new Composite(this, SWT.NONE);
        GridLayout layout2 = new GridLayout(8, false);
        layout2.horizontalSpacing = 0;
        layout2.marginLeft = 3;
        layout2.marginRight = 3;
        composite2.setLayout(layout2);
        composite2.setBackground(CP1Colors.SWITCH_BG);
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
            GridData gd_p5Switch1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_p5Switch1.heightHint = 60;
            p5Switch1.setLayoutData(gd_p5Switch1);
        }
        {
            CP5Switch p5Switch1 = new CP5Switch(composite2, SWT.NONE);
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

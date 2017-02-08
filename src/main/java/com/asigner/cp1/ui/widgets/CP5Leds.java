package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CP5Leds extends Composite {
    /**
     *
     * Create the composite.
     * @param parent
     * @param style
     */
    public CP5Leds(Composite parent, int style) {
        super(parent, style);

        GridLayout layout1 = new GridLayout(8, false);
        layout1.horizontalSpacing = 5;
        setLayout(layout1);
        setBackground(CP1Colors.GREEN);

        CP5Led p5Led1 = new CP5Led(this, SWT.NONE);
        CP5Led p5Led2 = new CP5Led(this, SWT.NONE);
        CP5Led p5Led3 = new CP5Led(this, SWT.NONE);
        p5Led3.setOn(true);
        CP5Led p5Led4 = new CP5Led(this, SWT.NONE);
        CP5Led p5Led5 = new CP5Led(this, SWT.NONE);
        CP5Led p5Led6 = new CP5Led(this, SWT.NONE);
        CP5Led p5Led7 = new CP5Led(this, SWT.NONE);
        CP5Led p5Led8 = new CP5Led(this, SWT.NONE);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

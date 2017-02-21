package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CP5Leds extends Composite {

    private CP5Led[] leds = new CP5Led[8];

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

        // Note: LSB is on the left-most position
        for (int i = 0; i < 8; i++) {
            leds[i] = new CP5Led(this, SWT.NONE);
            leds[i].setOn(false);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setValue(int value) {
        this.getDisplay().syncExec(() -> {
            for (int i = 0; i < 8; i++) {
                leds[i].setOn((value & (1 << i)) > 0);
            }});
    }
}

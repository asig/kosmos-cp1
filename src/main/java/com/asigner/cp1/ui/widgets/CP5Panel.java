package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CP5Panel extends Composite {

    private final CP5Leds leds;
    private final CP5Switches switches;

    /**
     *
     * Create the composite.
     * @param parent
     * @param style
     */
    public CP5Panel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(4, false);
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        setBackground(CP1Colors.GREEN);

        Label placeholder1 = new Label(this, SWT.NONE);
        placeholder1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        leds = new CP5Leds(this, SWT.NONE);
        leds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        Label placeholder2 = new Label(this, SWT.NONE);
        placeholder2.setBackground(CP1Colors.GREEN);
        placeholder2.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).hint(50,-1).create());

        switches = new CP5Switches(this, SWT.NONE);
        switches.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    }

    public void addSwitchesListener(CP5Switches.Listener listener) {
        switches.addListener(listener);
    }
    
    public void writeLeds(int value) {
        leds.setValue(value);
    }

    public int readSwitches() {
        return switches.getValue();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

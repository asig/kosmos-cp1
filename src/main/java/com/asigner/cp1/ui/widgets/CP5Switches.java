package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import com.google.common.collect.Lists;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

public class CP5Switches extends Composite {

    public interface Listener {
        void switchesChanged();
    }

    private List<Listener> listeners = Lists.newLinkedList();

    private final CP5Switch[] switches = new CP5Switch[8];

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
        layout2.marginTop = 0;
        layout2.marginBottom = 0;
        layout2.marginRight = 3;
        setLayout(layout2);
        setBackground(CP1Colors.SWITCH_BG);

        // Note that LSB is on the left-most side
        for (int i = 0; i < 8; i++) {
            CP5Switch swtch = new CP5Switch(this, SWT.NONE);
            swtch.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(-1, 60).create());
            swtch.addListener(() -> {
                listeners.forEach(Listener::switchesChanged);
            });
            switches[i] = swtch;

        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }


    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public int getValue() {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            if (switches[i].isOn()) {
                value |= 1 << i;
            }
        }
        return value;
    }

}

// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.util.function.Consumer;

public class InlineEdit extends Text {

    private boolean ok;
    private int rangeLow;
    private int rangeHigh;
    private Consumer<Integer> consumer;

    public InlineEdit(Composite parent, int style) {
        super(parent, style);
    }

    public void init(CLabel label, int rangeLow, int rangeHigh, Consumer<Integer> consumer) {
        init(label.getClientArea(), label.getText(), rangeLow, rangeHigh, consumer);
    }

    public void init(Rectangle area, String text, int rangeLow, int rangeHigh, Consumer<Integer> consumer) {
        this.rangeLow = rangeLow;
        this.rangeHigh = rangeHigh;
        this.consumer = consumer;
        this.setBounds(area);
        this.setText(text);
        this.setSelection(0, this.getText().length());
        this.setFocus();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == '\r') {
                    e.doit = true;
                    ok = true;
                    dispose();
                } else if (e.character == 0x1b) {
                    ok = false;
                    dispose();
                } else {
                    super.keyPressed(e);
                }
            }
        });
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ok = true;
                dispose();
            }
        });
        this.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent disposeEvent) {
                String s = getText();
                try {
                    int i = Integer.valueOf(s, 16);
                    if (i >= rangeLow && i <= rangeHigh) {
                        consumer.accept(i);
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        });
    }

    @Override
    protected void checkSubclass() {
    }

}

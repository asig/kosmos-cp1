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
                if (!ok) {
                    return;
                }
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

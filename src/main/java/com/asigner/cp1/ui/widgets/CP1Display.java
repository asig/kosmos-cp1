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

import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.ui.CP1Colors;
import com.asigner.cp1.ui.util.SWTResources;
import com.google.common.collect.ImmutableMap;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.Map;

import static com.asigner.cp1.emulation.Intel8155.StateListener.Port.A;
import static com.asigner.cp1.emulation.Intel8155.StateListener.Port.C;

public class CP1Display extends Composite {

    private static final int MARGIN_WIDTH = 10;
    private static final double SPACER_PERCENTAGE = 0.15; // Spacer width in percent of digit width

    private final CP1SevenSegmentComposite[] digits = new CP1SevenSegmentComposite[6];

    private final static Map<Character, Integer > charMap = ImmutableMap.<Character, Integer>builder()
            .put('0', 1 << 5 | 1 << 4 | 1 << 3 | 1 << 2 | 1 << 1 | 1 << 0)
            .put('1', 1 << 2 | 1 << 1)
            .put('2', 1 << 6 | 1 << 4 | 1 << 3 | 1 << 1 | 1 << 0)
            .put('3', 1 << 6 | 1 << 3 | 1 << 2 | 1 << 1 | 1 << 0)
            .put('4', 1 << 6 | 1 << 5 | 1 << 2 | 1 << 1)
            .put('5', 1 << 6 | 1 << 5 | 1 << 3 | 1 << 2 | 1 << 0)
            .put('6', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 3 | 1 << 2 | 1 << 0)
            .put('7', 1 << 5 | 1 << 2 | 1 << 1 | 1 << 0)
            .put('8', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 3 | 1 << 2 | 1 << 1 | 1 << 0)
            .put('9', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 2 | 1 << 1 | 1 << 0)
            .put('A', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 2 | 1 << 1 | 1 << 0)
            .put('E', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 3 | 1 << 0)
            .put('P', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 1 | 1 << 0)
            .put('C', 1 << 5 | 1 << 4 | 1 << 3 | 1 << 0)
            .put('u', 1 << 4 | 1 << 3 | 1 << 2 )
            .put('n', 1 << 5 | 1 << 1 | 1 << 0)
            .put(' ', 0)
            .build();

    private Intel8155 pid = null;
    private int activeDigit; // set by writes to Intel 8155's port C
    private Intel8155.StateListener.Port lastPortWritten = null;
    private Intel8155.StateListener pidListener;

    public CP1Display(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout(8, false);
        gridLayout.marginTop = MARGIN_WIDTH;
        gridLayout.marginRight = MARGIN_WIDTH;
        gridLayout.marginLeft = MARGIN_WIDTH;
        gridLayout.marginBottom = MARGIN_WIDTH;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        this.addPaintListener(this::paint);

        digits[0] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[0].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        Label spacer1 = new Label(this, SWT.NONE);
        spacer1.setLayoutData(GridDataFactory.swtDefaults().grab(true,  true).create());

        digits[1] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[1].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[2] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[2].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());
        digits[2].setShowDot(true);

        Label spacer2 = new Label(this, SWT.NONE);
        spacer2.setLayoutData(GridDataFactory.swtDefaults().grab(true,  true).create());

        digits[3] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[3].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[4] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[4].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[5] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[5].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        pidListener = new Intel8155.StateListener() {
            @Override
            public void commandRegisterWritten() {
            }

            @Override
            public void portWritten(Intel8155.StateListener.Port port, int value) {
                if (port == C) {
                    for (int i = 0; i < 8; i++) {
                        if ((value & (1 << i)) == 0) {
                            activeDigit = i;
                            break;
                        }
                    }
                } else if (port == A && lastPortWritten == C) {
                    // Ignore writes to A unless they happen directly after a write to C.
                    // For some reason that I don't fully understand yet, starting at 0x026f
                    // in the ROM port A is cleared, then the line is selected by writing to
                    // Port C, and only then the new value is written, so a digit is empty at
                    // 5/6th of the time...
                    final CP1SevenSegmentComposite digit = digits[5 - activeDigit];
                    digit.setSegments(value);
                    getDisplay().syncExec(() -> {
                        if (!digit.isDisposed()) {
                            digit.redraw();
                        }
                    });
                }
                lastPortWritten = port;
            }

            @Override
            public void memoryWritten() {
            }

            @Override
            public void pinsChanged() {
            }

            @Override
            public void resetExecuted() {
            }
        };
        this.addDisposeListener(ev -> {
            if (this.pid != null) {
                this.pid.removeListener(pidListener);
            }
        });
    }

    public void setPid(Intel8155 pid) {
        if (this.pid != null) {
            this.pid.removeListener(pidListener);
        }
        this.pid = pid;
        this.pid.addListener(pidListener);
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point pt;
        if (wHint > 0) {
            pt = digits[0].computeSize((int)((wHint-2*MARGIN_WIDTH)/(6+2*SPACER_PERCENTAGE)), -1, changed);
        } else if (hHint > 0) {
            pt = digits[0].computeSize(-1, hHint - 2 * MARGIN_WIDTH, changed);
        } else {
            pt = digits[0].computeSize(wHint, hHint, changed);
        }
        pt.x = (int)(pt.x * (6+2*SPACER_PERCENTAGE) + 2 * MARGIN_WIDTH);
        pt.y+= 2 * MARGIN_WIDTH;
        return pt;
    }

    @Override
    public void setSize(Point size) {
        super.setSize(size);
    }

    public void display(String s) {
        s = "      " + s;
        s = s.substring(s.length() - 6);
        for (int i = 0; i < 6; i++) {
            digits[i].setSegments(charMap.getOrDefault(s.charAt(i), 0));
        }
    }

    private void paint(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        Rectangle r = getClientArea();

        // Draw green corners
        gc.setBackground(CP1Colors.GREEN);
        gc.fillRectangle(r.x, r.y, r.width, MARGIN_WIDTH);
        gc.fillRectangle(r.x, r.y + r.height - MARGIN_WIDTH, r.width, MARGIN_WIDTH);
        gc.fillRectangle(r.x, r.y, MARGIN_WIDTH, r.height);
        gc.fillRectangle(r.x + r.width - MARGIN_WIDTH, r.y , MARGIN_WIDTH, r.height);

        gc.setBackground(SWTResources.BLACK);
        gc.fillRoundRectangle(r.x, r.y, r.width, r.height, 10, 10);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}

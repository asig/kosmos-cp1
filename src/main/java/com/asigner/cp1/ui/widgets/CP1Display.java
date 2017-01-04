// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import com.asigner.cp1.ui.SWTResources;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;

import java.util.Map;
import java.util.Set;

public class CP1Display extends Composite {

    private static final int MARGIN_WIDTH = 10;

    private final CP1SevenSegmentComposite[] digits = new CP1SevenSegmentComposite[6];

    private final static Map<Character, Set<Integer> > charMap = ImmutableMap.<Character, Set<Integer>>builder()
            .put('0', Sets.newHashSet(0,1,2,4,5,6))
            .put('1', Sets.newHashSet(2,5))
            .put('2', Sets.newHashSet(0,2,3,4,6))
            .put('3', Sets.newHashSet(0,2,3,5,6))
            .put('4', Sets.newHashSet(1,2,3,5))
            .put('5', Sets.newHashSet(0,1,3,5,6))
            .put('6', Sets.newHashSet(0,1,3,4,5,6))
            .put('7', Sets.newHashSet(0,1,2,5))
            .put('8', Sets.newHashSet(0,1,2,3,4,5,6))
            .put('9', Sets.newHashSet(0,1,2,3,5,6))
            .put('A', Sets.newHashSet(0,1,2,3,4,5))
            .put('E', Sets.newHashSet(0,1,3,4,6))
            .put('P', Sets.newHashSet(0,1,2,3,4))
            .put('C', Sets.newHashSet(0,1,4,6))
            .put('u', Sets.newHashSet(4,5,6))
            .put('ⁿ', Sets.newHashSet(0,1,2))
            .put(' ', Sets.newHashSet())
            .build();

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public CP1Display(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout(6, true);
        gridLayout.marginTop = 10;
        gridLayout.marginRight = 10;
        gridLayout.marginLeft = 10;
        gridLayout.marginBottom = 10;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        this.addPaintListener(this::paint);

        digits[0] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[0].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[1] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[1].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[2] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[2].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());
        digits[2].setShowDot(true);
        digits[2].setDot(true);

        digits[3] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[3].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[4] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[4].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());

        digits[5] = new CP1SevenSegmentComposite(this, SWT.NONE);
        digits[5].setLayoutData(GridDataFactory.swtDefaults().hint(-1,  80).create());
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point pt;
        if (wHint > 0) {
            pt = digits[0].computeSize(wHint/6, -1, changed);
        } else if (hHint > 0) {
            pt = digits[0].computeSize(-1, hHint - 2 * MARGIN_WIDTH, changed);
        } else {
            pt = digits[0].computeSize(wHint, hHint, changed);
        }
        pt.x = pt.x * 6 + 2 * MARGIN_WIDTH;
        pt.y+= 2 * MARGIN_WIDTH;
        return pt;
    }

    public void display(String s) {
        s = "      " + s;
        s = s.substring(s.length() - 6);
        for (int i = 0; i < 6; i++) {
            digits[i].setSegments(charMap.getOrDefault(s.charAt(i), Sets.newHashSet()));
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

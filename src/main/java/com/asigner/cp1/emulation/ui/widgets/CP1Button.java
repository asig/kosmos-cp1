// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation.ui.widgets;

import com.asigner.cp1.emulation.ui.SWTResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class CP1Button extends Canvas {

    private final Font largeFont;
    private final Font smallFont;

    private String text;
    private String subText;

    public CP1Button(Composite parent, int style) {
        super(parent, style);

        largeFont = new Font(parent.getDisplay(), "Pragmatica Black", 26, SWT.NONE);
        smallFont = new Font(parent.getDisplay(), "Pragmatica Black", 12, SWT.NONE);

        this.addPaintListener(this::paint);
    }

    private void paint(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        Rectangle bounds = this.getBounds();

        gc.setForeground(SWTResources.BLACK);
        gc.setBackground(SWTResources.WHITE);

        gc.setLineWidth(3);
        gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width-4-2, bounds.height-4-2, 35,35);
        gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width-4-2, bounds.height-4-2, 35,35);

        gc.setLineWidth(1);
        gc.drawRoundRectangle(bounds.x+6, bounds.y+6, bounds.width-12-4-3, bounds.height-12-4-3, 28,28);

        gc.setFont(largeFont);
        FontMetrics fm = gc.getFontMetrics();
        Point pt = gc.textExtent(text);
        gc.drawString(text, (bounds.x+bounds.width-pt.x)/2,(bounds.y+bounds.height-fm.getHeight())/2 + fm.getAscent(), true);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    @Override
    protected void checkSubclass() {
    }
}

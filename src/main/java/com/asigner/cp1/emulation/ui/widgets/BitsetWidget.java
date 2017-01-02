package com.asigner.cp1.emulation.ui.widgets;

import com.asigner.cp1.emulation.ui.SWTResources;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class BitsetWidget extends Label {

    private static final int BOX_W = 14;
    private static final int BOX_H = 14;

    private static final int INSET = 3;

    private static final int BORDER_TOP = 3;
    private static final int BORDER_BOTTOM = 3;
    private static final int BORDER_LEFT = 3;
    private static final int BORDER_RIGHT = 3;

    private static final int SEPARATOR_WIDTH = 3;

    private final int size;

    private Color bgCol = SWTResources.WHITE;
    private Color fgCol = SWTResources.BLACK;

    private int value = 0;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public BitsetWidget(Composite parent, int size, int style) {
        super(parent, style);
        setForeground(fgCol);
        this.size = size;
        this.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent evt) {
                paint(evt.gc);
            }});
    }

    public void setValue(int value) {
        if (this.value != value) {
            this.value = value;
            redraw();
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        int w = size * BOX_W + (size - 1) * SEPARATOR_WIDTH + BORDER_LEFT + BORDER_RIGHT;
        int h = BOX_H + BORDER_TOP + BORDER_BOTTOM;
        return new Point(w, h);
    }

    private void paint(GC gc) {
        // Fill background
        gc.setBackground(this.getBackground());
        gc.fillRectangle(this.getBounds());

        int mask = 1 << (size-1);
        for (int i = 0; i < size; i++) {
            drawBit(gc, BORDER_LEFT + i * (BOX_W + SEPARATOR_WIDTH), BORDER_TOP, (value & mask) > 0);
            mask >>= 1;
        }
    }

    private void drawBit(GC gc, int x, int y, boolean set) {
        gc.setBackground(bgCol);
        gc.fillArc(x, y, BOX_W, BOX_H, 0, 360);
        gc.drawArc(x, y, BOX_W, BOX_H, 0, 360);
        if (set) {
            gc.setBackground(fgCol);
            gc.fillArc(x+INSET, y+INSET, BOX_W-2*INSET, BOX_H-2*INSET, 0, 360);
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

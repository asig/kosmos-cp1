// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import com.asigner.cp1.ui.SWTResources;
import com.google.common.base.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import java.util.LinkedList;
import java.util.List;

public class CP1Button extends Composite {

    public interface KeyListener {
        void keyPressed(CP1Button btn);
        void keyReleased(CP1Button btn);
    }

    private static final String LARGE_FONT_NAME = "Helvetica Black";
    private static final String SMALL_FONT_NAME = "Helvetica";

    private boolean mousePressed = false;
    private boolean mouseOverControl = false;
    private List<KeyListener> keyListeners = new LinkedList<>();

    private Font largeFont;
    private Font smallFont;

    private String text = "";
    private String subText = "";

    public CP1Button(Composite parent, int style) {
        super(parent, style);

        largeFont = new Font(parent.getDisplay(), LARGE_FONT_NAME, 12, SWT.NONE);
        smallFont = new Font(parent.getDisplay(), SMALL_FONT_NAME, 6, SWT.NONE);

        this.setSize((int)(3.2 * 50), 50);
        this.addPaintListener(this::paint);
        this.setBackground(CP1Colors.PANEL_BACKGROUND);

        this.addMouseTrackListener(new MouseTrackListener() {
            @Override
            public void mouseEnter(MouseEvent mouseEvent) {
                mouseOverControl = true;
                redraw();
            }

            @Override
            public void mouseExit(MouseEvent mouseEvent) {
                mouseOverControl = false;
                redraw();
            }

            @Override
            public void mouseHover(MouseEvent mouseEvent) {
            }
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {
                mousePressed = true;
                keyListeners.forEach(l -> l.keyPressed(CP1Button.this));
                redraw();
            }

            @Override
            public void mouseUp(MouseEvent mouseEvent) {
                if (mouseOverControl) {
                    keyListeners.forEach(l -> l.keyReleased(CP1Button.this));
                }
                mousePressed = false;
                redraw();
            }
        });
    }

    public void addKeyListener(KeyListener l) {
        keyListeners.add(l);
    }

    public void removeKeyListener(KeyListener l) {
        keyListeners.remove(l);
    }

    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {
        return getSize();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        initializeFonts();
    }

    private void initializeFonts() {
        int height = this.getSize().y;
        if (largeFont != null) {
            largeFont.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }

        double largeTextSizeFactor = Strings.isNullOrEmpty(subText) ? 0.5 : 0.375;
        largeFont = new Font(this.getDisplay(), LARGE_FONT_NAME, (int)(height * largeTextSizeFactor), SWT.NONE);
        smallFont = new Font(this.getDisplay(), SMALL_FONT_NAME, (int)(height * 0.15), SWT.NONE);
    }

    @Override
    public void setSize(Point pt) {
        this.setSize(pt.x, pt.y);
    }

    private void paint(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        Rectangle bounds = this.getClientArea();

        Color fg = (mousePressed && mouseOverControl) ? SWTResources.GRAY50 : SWTResources.BLACK;
        gc.setForeground(fg);
        gc.setBackground(SWTResources.WHITE);

        int r = bounds.height/2;

        gc.setLineWidth(1);
        gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width-1, bounds.height-1, r, r);
        gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width-1, bounds.height-1, r, r);

        gc.setLineWidth(1);
        gc.drawRoundRectangle(bounds.x+3, bounds.y+3, bounds.width-6-1, bounds.height-6-1, (int)(r*0.8), (int)(r*0.8));

        gc.setFont(largeFont);
        Point pt = gc.textExtent(text);
        int h = pt.y;
        int largeTextY;
        if (Strings.isNullOrEmpty(subText)) {
            // Only large text, center it.
            largeTextY = bounds.y + (bounds.height - h)/2;
            // Needs some manual adjustment...
            largeTextY -= (int)(bounds.height*0.05);
        } else {
            largeTextY = bounds.y + (int)( (bounds.height - h)/2 * 0.2);
        }
        gc.drawString(text, bounds.x+(bounds.width-pt.x)/2, largeTextY, true);

        if (!Strings.isNullOrEmpty(subText)) {
            gc.setFont(smallFont);
            pt = gc.textExtent(subText);
            int y = bounds.y + bounds.height - 6 - pt.y;
            gc.drawString(subText, bounds.x+(bounds.width-pt.x)/2, y, true);
        }
    }

    public void setText(String text) {
        this.text = text;
        initializeFonts();
    }

    public void setSubText(String subText) {
        this.subText = subText;
        initializeFonts();
    }

    @Override
    protected void checkSubclass() {
    }
}

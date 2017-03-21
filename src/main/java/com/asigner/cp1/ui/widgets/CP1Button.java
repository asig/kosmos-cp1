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

import com.asigner.cp1.ui.CP1Colors;
import com.asigner.cp1.ui.OS;
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
    private static final boolean isMac = OS.isMac();

    private boolean pressed = false;
    private boolean mouseOverControl = false;
    private List<KeyListener> keyListeners = new LinkedList<>();

    private Font largeFont;
    private Font smallFont;

    private String text = "";
    private String subText = "";

    public CP1Button(Composite parent, int style) {
        super(parent, style);

        largeFont = SWTResources.getFont(LARGE_FONT_NAME, scaleFontSize(12), false);
        smallFont = SWTResources.getFont(SMALL_FONT_NAME, scaleFontSize(6), false);

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
                pressed = true;
                keyListeners.forEach(l -> l.keyPressed(CP1Button.this));
                redraw();
            }

            @Override
            public void mouseUp(MouseEvent mouseEvent) {
                if (mouseOverControl) {
                    keyListeners.forEach(l -> l.keyReleased(CP1Button.this));
                }
                pressed = false;
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

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        if (this.pressed != pressed) {
            this.pressed = pressed;
            redraw();
            if (this.pressed) {
                keyListeners.forEach(l -> l.keyPressed(CP1Button.this));
            } else {
                keyListeners.forEach(l -> l.keyReleased(CP1Button.this));
            }
        }
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
        largeFont = new Font(this.getDisplay(), LARGE_FONT_NAME, scaleFontSize(height * largeTextSizeFactor), SWT.NONE);
        smallFont = new Font(this.getDisplay(), SMALL_FONT_NAME, scaleFontSize(height * 0.15), SWT.NONE);
    }

    private int scaleFontSize(double fontSize) {
        return (int)(isMac ? 1.3 * fontSize : fontSize);
    }

    @Override
    public void setSize(Point pt) {
        this.setSize(pt.x, pt.y);
    }

    private void paint(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        Rectangle bounds = this.getClientArea();

        Color fg = (pressed) ? SWTResources.GRAY50 : SWTResources.BLACK;
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

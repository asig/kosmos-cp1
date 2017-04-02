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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;

import java.util.LinkedList;
import java.util.List;

public class CP1Button extends Composite {

    public interface KeyListener {
        void keyPressed(CP1Button btn);
        void keyReleased(CP1Button btn);
    }

    private boolean pressed = false;
    private boolean mouseOverControl = false;
    private List<KeyListener> keyListeners = new LinkedList<>();

    private Image imgNormal;
    private Image imgPressed;
    private int w;
    private int h;

    public CP1Button(Composite parent, String name, int style) {
        super(parent, style | SWT.NO_BACKGROUND);
        this.imgNormal = SWTResources.getImage("/com/asigner/cp1/ui/buttons/"+ name + ".png");
        this.imgPressed = SWTResources.getImage("/com/asigner/cp1/ui/buttons/"+ name + "_pressed.png");
        Rectangle r = this.imgNormal.getBounds();
        this.w = r.width;
        this.h = r.height;
        super.setSize(this.w, this.h);

        this.addPaintListener(this::paint);

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
        super.setSize(w, h);
    }

    @Override
    public void setSize(Point pt) {
        this.setSize(pt.x, pt.y);
    }

    private void paint(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        gc.drawImage(pressed ? imgPressed : imgNormal, 0, 0);
    }

    @Override
    protected void checkSubclass() {
    }
}

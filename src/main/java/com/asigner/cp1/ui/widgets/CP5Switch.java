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
import com.asigner.cp1.ui.util.SWTResources;
import com.google.common.collect.Lists;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

public class CP5Switch extends Composite {

    interface Listener {
        void switchFlipped();
    }

    private static Image ON;
    private static Image OFF;
    private static Image ON_PRESSED;
    private static Image OFF_PRESSED;
    private static int w, h;

    static {
        ON = SWTResources.getImage("/com/asigner/cp1/ui/switch_on.png");
        OFF = SWTResources.getImage("/com/asigner/cp1/ui/switch_off.png");
        ON_PRESSED = SWTResources.getImage("/com/asigner/cp1/ui/switch_on_pressed.png");
        OFF_PRESSED = SWTResources.getImage("/com/asigner/cp1/ui/switch_off_pressed.png");
        Rectangle r = ON.getBounds();
        w = r.width;
        h = r.height;
    }

    private List<Listener> listeners = Lists.newLinkedList();

    private boolean pressed = false;
    private boolean on = false;

    public CP5Switch(Composite parent, int style) {
        super(parent, style);

        this.addPaintListener(this::paint);
        this.setBackground(CP1Colors.GREEN);

        setSize(w, h);
        
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {
                pressed = true;
                redraw();
            }

            @Override
            public void mouseUp(MouseEvent mouseEvent) {
                pressed = false;
                on = !on;
                listeners.forEach(Listener::switchFlipped);
                redraw();
            }
        });
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }
    
    public boolean isOn() {
        return on;
    }

    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) { return getSize(); }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    @Override
    public void setSize(Point pt) {
        this.setSize(pt.x, pt.y);
    }

    private void paint(PaintEvent paintEvent) {
        Image img;
        if (on) {
            img = pressed ? ON_PRESSED : ON;
        } else {
            img = pressed ? OFF_PRESSED : OFF;
        }
        paintEvent.gc.drawImage(img,  0, 0);
    }

    @Override
    protected void checkSubclass() {
    }
}

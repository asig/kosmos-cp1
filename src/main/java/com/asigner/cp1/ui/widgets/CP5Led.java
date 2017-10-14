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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class CP5Led extends Composite {

    private static Image ON;
    private static Image OFF;
    private static int w, h;

    static {
        ON = SWTResources.getImage("/com/asigner/cp1/ui/led_on.png");
        OFF = SWTResources.getImage("/com/asigner/cp1/ui/led_off.png");
        Rectangle r = ON.getBounds();
        w = r.width;
        h = r.height;
    }

    private boolean on = false;

    public void setOn(boolean on) {
        if (this.on != on) {
            this.on = on;
            redraw();
        }
    }

    public CP5Led(Composite parent, int style) {
        super(parent, style);
        this.setSize(w, h);
        this.addPaintListener(this::paint);
        this.setBackground(CP1Colors.GREEN);
    }

    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {
        return getSize();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    @Override
    public void setSize(Point pt) {
        this.setSize(pt.x, pt.y);
    }

    private void paint(PaintEvent paintEvent) {
        paintEvent.gc.drawImage(on ? ON : OFF,  0, 0);
    }
}

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

import com.asigner.cp1.ui.SWTResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class CP1SevenSegmentComposite extends org.eclipse.swt.widgets.Composite {

    //    --a--
    //   |     |
    //   f     b
    //   |     |
    //    --g--
    //   |     |
    //   e     c
    //   |     |
    //    --d--  .dp
    //
    // a == 0, b == 1, ... dp == 7

    private static Image DIGITS_DOT[];
    private static Image DIGITS_NODOT[];
    private static int w, h;

    static {
        DIGITS_NODOT = new Image[128];
        for (int i = 0; i < 128; i++) {
            DIGITS_NODOT[i] = SWTResources.getImage(String.format("/com/asigner/cp1/ui/digits/nodot/%02x.png", i));
        }
        DIGITS_DOT = new Image[256];
        for (int i = 0; i < 256; i++) {
            DIGITS_DOT[i] = SWTResources.getImage(String.format("/com/asigner/cp1/ui/digits/dot/%02x.png", i));
        }
        Rectangle r = DIGITS_DOT[0].getBounds();
        w = r.width;
        h = r.height;
    }

    private boolean showDot = false;
    private int mask = 0;

    public CP1SevenSegmentComposite(org.eclipse.swt.widgets.Composite parent, int style) {
        super(parent, style | SWT.NO_BACKGROUND);
        this.addPaintListener(this::paint);
    }

    public void setShowDot(boolean showDot) {
        if (this.showDot != showDot) {
            this.showDot = showDot;
            redraw();
        }
    }

    public void setSegments(int mask) {
        this.mask = mask | (showDot ? 128 : 0);
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return new Point(w,h);
    }

    @Override
    public Point getSize() {
        return new Point(w,h);
    }

    private void paint(PaintEvent paintEvent) {
        Image imgs[] = showDot ? DIGITS_DOT : DIGITS_NODOT;
        paintEvent.gc.drawImage(imgs[mask], 0, 0);
    }

    @Override
    protected void checkSubclass() {
    }
}

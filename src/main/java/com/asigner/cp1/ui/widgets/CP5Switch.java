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
import com.google.common.collect.Lists;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.jfree.swt.SWTGraphics2D;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class CP5Switch extends Composite {

    interface Listener {
        void switchFlipped();
    }

    private boolean on = false;
    private List<Listener> listeners = Lists.newLinkedList();

    private boolean pressed = false;

    public CP5Switch(Composite parent, int style) {
        super(parent, style);

        this.addPaintListener(this::paint);
        this.setBackground(CP1Colors.GREEN);

        setSize(computeSize(-1, 40, true));
        
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
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {
        if (wHint > 0) {
            double scale = (double) getOrigHeight() / (double) getOrigWidth();
            return new Point(wHint, (int) (wHint * scale));
        } else if (hHint > 0) {
            double scale = (double) getOrigWidth() / (double) getOrigHeight();
            return new Point((int) (hHint * scale), hHint);
        } else {
            return new Point(getOrigWidth(), getOrigHeight());
        }
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
        SWTGraphics2D g2d = new SWTGraphics2D(paintEvent.gc);
        paint(g2d);
    }

    @Override
    protected void checkSubclass() {
    }

    /**
     * Paints the transcoded SVG image on the specified graphics context. You
     * can install a custom transformation on the graphics context to scale the
     * image.
     *
     * @param g Graphics context.
     */
    public void paint(Graphics2D g) {
        Shape shape = null;

        float origAlpha = 1.0f;
        java.awt.Composite origComposite = ((Graphics2D)g).getComposite();
        if (origComposite instanceof AlphaComposite) {
            AlphaComposite origAlphaComposite = (AlphaComposite)origComposite;
            if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                origAlpha = origAlphaComposite.getAlpha();
            }
        }

        java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();

        double scale = (double)getSize().y/(double)getOrigHeight();
        g.scale(scale, scale);

        if (on) {
            //
            transformations.offer(g.getTransform());
            g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 0, -4.0690106E-6f));

            // _0
            transformations.offer(g.getTransform());
            g.transform(new AffineTransform(1, 0, 0, 1, 114.5562f, -488.25507f));

            // _0_0
            transformations.offer(g.getTransform());
            g.transform(new AffineTransform(1, 0, 0, -1, -683.9947f, 655.6378f));
        } else {
            //
            transformations.offer(g.getTransform());
            g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 2.034505E-6f, 0));

            // _0
            transformations.offer(g.getTransform());
            g.transform(new AffineTransform(1, 0, 0, 1, -346.15808f, -473.96936f));

            // _0_0
            transformations.offer(g.getTransform());
            g.transform(new AffineTransform(1, 0, 0, 1, -223.2804f, 423.3723f));
        }

        // _0_0_0

        // _0_0_0_0
        shape = new Rectangle2D.Double(569.4384765625, 50.597049713134766, 57.68383026123047, 116.78571319580078);
        g.setPaint(new Color(0x0E100D));
        g.fill(shape);

        // _0_0_0_1
        shape = new Rectangle2D.Double(574.32861328125, 56.62732696533203, 47.90354919433594, 104.72515869140625);
        g.setPaint(new Color(0x4E4634));
        g.fill(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 305.06607f, -403.05087f));

        // _0_0_0_2

        // _0_0_0_2_0
        shape = new Rectangle2D.Double(277.1428527832031, 476.290771484375, 32.14285659790039, 68.92857360839844);
        g.setPaint(new Color(0x302D1C));
        g.fill(shape);

        // _0_0_0_2_1

        // _0_0_0_2_1_0
        shape = new Rectangle2D.Double(276.4285888671875, 508.8622131347656, 33.57143020629883, 38.92856979370117);
        g.setPaint(new Color(0x151615));
        g.fill(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -5.722046E-6f, -0.03760874f));

        // _0_0_0_2_1_1

        // _0_0_0_2_1_1_0
        shape = new Rectangle2D.Double(278.75390625, 514.7294921875, 28.920806884765625, 2.5575592517852783);
        g.setPaint(new Color(0x303030));
        g.fill(shape);

        // _0_0_0_2_1_1_1
        shape = new Rectangle2D.Double(278.75390625, 519.6718139648438, 28.920806884765625, 2.5575592517852783);
        g.fill(shape);

        // _0_0_0_2_1_1_2
        shape = new Rectangle2D.Double(278.75390625, 524.6141357421875, 28.920806884765625, 2.5575592517852783);
        g.fill(shape);

        // _0_0_0_2_1_1_3
        shape = new Rectangle2D.Double(278.75390625, 529.5564575195312, 28.920806884765625, 2.5575592517852783);
        g.fill(shape);

        // _0_0_0_2_1_1_4
        shape = new Rectangle2D.Double(278.75390625, 534.498779296875, 28.920806884765625, 2.5575592517852783);
        g.fill(shape);

        // _0_0_0_2_1_1_5
        shape = new Rectangle2D.Double(278.75390625, 539.441162109375, 28.920806884765625, 2.5575592517852783);
        g.fill(shape);

        g.setTransform(transformations.pollLast()); // _0_0_0_2_1_1

        if (pressed) {
            // Highlight the knob.
            shape = new Rectangle2D.Double(276.4285888671875, 508.8622131347656, 33.57143020629883, 38.92856979370117);
            g.setPaint(new Color(255, 255, 255, 100));
            g.fill(shape);
        }

        g.setTransform(transformations.pollLast()); // _0_0_0_2

        g.setTransform(transformations.pollLast()); // _0_0_0

        g.setTransform(transformations.pollLast()); // _0_0

        g.setTransform(transformations.pollLast()); // _0

    }

    /**
     * Returns the X of the bounding box of the original SVG image.
     *
     * @return The X of the bounding box of the original SVG image.
     */
    public static int getOrigX() {
        return 0;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     *
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 0;
    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     *
     * @return The width of the bounding box of the original SVG image.
     */
    public static int getOrigWidth() {
        return 62;
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    public static int getOrigHeight() {
        return 125;
    }

}

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
import com.google.common.collect.Sets;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.jfree.swt.SWTGraphics2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Set;


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

    private Set<Integer> segs = Sets.newHashSet();
    private Shape segments[] = new Shape[8];
    private boolean showDot = false;

    private int w, h;

    public CP1SevenSegmentComposite(org.eclipse.swt.widgets.Composite parent, int style) {
        super(parent, style);

        initSegments();
        this.addPaintListener(this::paint);
        this.setBackground(CP1Colors.SEGMENT_BG);
    }

    public void setShowDot(boolean showDot) {
        if (this.showDot != showDot) {
            this.showDot = showDot;
            redraw();
        }
    }

    public void setDot(boolean on) {
        if (on) {
            segs.add(7);
        } else {
            segs.remove(7);
        }
        redraw();
    }

    public void setSegments(int mask) {
        Set<Integer> segs = Sets.newHashSet();
        for (int i = 0; i < 8; i++) {
            if ( (mask & (1 << i)) > 0) {
                segs.add(i);
            }
        }
        setSegments(segs);
    }

    public void setSegments(Set<Integer> segs) {
        boolean hasDot = this.segs.contains(7);
        if (!this.segs.equals(segs)) {
            this.segs = segs;
            if (hasDot) {
                this.segs.add(7);
            }
            redraw();
        }
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

//    @Override
//    public Point getSize() {
//        return new Point(getOrigWidth()/2, getOrigHeight()/2);
//    }

    private void paint(PaintEvent paintEvent) {
        SWTGraphics2D g2d = new SWTGraphics2D(paintEvent.gc);
        paint(g2d);
    }

    private void initSegments() {
        Shape shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(603.99066, 607.36523);
        ((GeneralPath) shape).lineTo(606.76855, 604.58734);
        ((GeneralPath) shape).lineTo(671.67084, 604.33484);
        ((GeneralPath) shape).lineTo(673.69116, 607.61786);
        ((GeneralPath) shape).lineTo(663.33704, 615.69904);
        ((GeneralPath) shape).lineTo(611.3142, 615.69904);
        ((GeneralPath) shape).closePath();
        segments[0] = shape;

        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(588.59406, 669.2681);
        ((GeneralPath) shape).lineTo(586.54047, 666.1431);
        ((GeneralPath) shape).lineTo(596.09406, 613.28595);
        ((GeneralPath) shape).lineTo(599.66547, 610.25024);
        ((GeneralPath) shape).lineTo(606.89764, 618.91095);
        ((GeneralPath) shape).lineTo(600.02264, 660.5181);
        ((GeneralPath) shape).closePath();
        segments[5] = shape;

        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(677.5257, 611.1592);
        ((GeneralPath) shape).lineTo(679.5793, 614.2842);
        ((GeneralPath) shape).lineTo(670.0257, 667.14136);
        ((GeneralPath) shape).lineTo(666.4543, 670.17706);
        ((GeneralPath) shape).lineTo(659.22217, 661.51636);
        ((GeneralPath) shape).lineTo(666.09717, 619.9092);
        ((GeneralPath) shape).closePath();
        segments[1] = shape;

        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(592.4349, 673.07074);
        ((GeneralPath) shape).lineTo(599.1313, 667.7136);
        ((GeneralPath) shape).lineTo(657.3672, 667.67633);
        ((GeneralPath) shape).lineTo(662.08405, 673.23376);
        ((GeneralPath) shape).lineTo(655.917, 678.6062);
        ((GeneralPath) shape).lineTo(597.0777, 678.6062);
        ((GeneralPath) shape).closePath();
        segments[6] = shape;

        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(577.8723, 735.5555);
        ((GeneralPath) shape).lineTo(574.9349, 732.1779);
        ((GeneralPath) shape).lineTo(584.48846, 679.32074);
        ((GeneralPath) shape).lineTo(588.0599, 676.28503);
        ((GeneralPath) shape).lineTo(595.29205, 684.94574);
        ((GeneralPath) shape).lineTo(588.41705, 726.5529);
        ((GeneralPath) shape).closePath();
        segments[4] = shape;

        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(666.1507, 676.87335);
        ((GeneralPath) shape).lineTo(668.2042, 679.99835);
        ((GeneralPath) shape).lineTo(658.6507, 732.8555);
        ((GeneralPath) shape).lineTo(655.0792, 735.89124);
        ((GeneralPath) shape).lineTo(647.84717, 727.2305);
        ((GeneralPath) shape).lineTo(654.7221, 685.62335);
        ((GeneralPath) shape).closePath();
        segments[2] = shape;

        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(583.0599, 741.553);
        ((GeneralPath) shape).lineTo(580.91705, 739.053);
        ((GeneralPath) shape).lineTo(590.82776, 730.0351);
        ((GeneralPath) shape).lineTo(644.59045, 729.6257);
        ((GeneralPath) shape).lineTo(651.18494, 738.0708);
        ((GeneralPath) shape).lineTo(648.05994, 741.8208);
        ((GeneralPath) shape).closePath();
        segments[3] = shape;

        shape = new Ellipse2D.Double(666.5238647460938, 729.7498779296875, 15.447550773620605, 15.447550773620605);
        segments[7] = shape;
    }

    /**
     * Paints the transcoded SVG image on the specified graphics context. You
     * can install a custom transformation on the graphics context to scale the
     * image.
     *
     * @param g Graphics context.
     */
    public void paint(Graphics2D g) {
        java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();

        double scale = (double)getSize().y/(double)getOrigHeight();
        g.scale(scale, scale);

        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 0, 0));

        // _0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -414.92877f, -588.5938f));

        // _0_0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -1.9726563E-6f, 3.7060522E-6f));

        // _0_0_0

        // _0_0_0_0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -148.88152f, 0.97053474f));

        int end = showDot ? 8 : 7;
        for (int i = 0; i < end; i++) {
            g.setPaint(new Color(fromRGB(segs.contains(i) ? CP1Colors.SEGMENT_ON.getRGB() : CP1Colors.SEGMENT_OFF.getRGB())));
            g.fill(segments[i]);
        }

        g.setTransform(transformations.pollLast()); // _0_0_0_1

        g.setTransform(transformations.pollLast()); // _0_0_0

        g.setTransform(transformations.pollLast()); // _0_0

        g.setTransform(transformations.pollLast()); // _0

    }

    private int fromRGB(RGB rgb) {
        return rgb.red << 16 | rgb.green << 8 | rgb.blue;
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
        return 138;
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    public static int getOrigHeight() {
        return 186;
    }

    @Override
    protected void checkSubclass() {
    }
}

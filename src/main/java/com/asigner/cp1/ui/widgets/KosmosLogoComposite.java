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

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.jfree.swt.SWTGraphics2D;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class KosmosLogoComposite extends org.eclipse.swt.widgets.Composite {

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public KosmosLogoComposite(org.eclipse.swt.widgets.Composite parent, int style) {
        super(parent, style);
        this.addPaintListener(this::paint);
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

    private void paint(PaintEvent paintEvent) {
        SWTGraphics2D g2d = new SWTGraphics2D(paintEvent.gc);
        paint(g2d);
    }


    /**
     * Paints the transcoded SVG image on the specified graphics context. You
     * can install a custom transformation on the graphics context to scale the
     * image.
     *
     * @param g Graphics context.
     */
    public void paint(Graphics2D g) {
        double scale = (double)getSize().y/(double)getOrigHeight();
        g.scale(scale, scale);

        Shape shape = null;

        float origAlpha = 1.0f;
        Composite origComposite = ((Graphics2D)g).getComposite();
        if (origComposite instanceof AlphaComposite) {
            AlphaComposite origAlphaComposite = (AlphaComposite)origComposite;
            if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                origAlpha = origAlphaComposite.getAlpha();
            }
        }

        java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();


        //
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 0, 0));

        // _0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -364.5588f, -25.165453f));

        // _0_0

        // _0_0_0

        // _0_0_0_0
        shape = new Rectangle2D.Double(364.5588073730469, 25.16545295715332, 457.51800537109375, 198.1510009765625);
        g.setPaint(new Color(0x3B4237));
        g.fill(shape);

        // _0_0_0_1
        shape = new RoundRectangle2D.Double(365.0588073730469, 25.66545295715332, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_2
        shape = new RoundRectangle2D.Double(365.0588073730469, 55.35252380371094, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.draw(shape);

        // _0_0_0_3
        shape = new RoundRectangle2D.Double(365.0588073730469, 85.03947448730469, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.draw(shape);

        // _0_0_0_4
        shape = new RoundRectangle2D.Double(365.0588073730469, 114.72648620605469, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.draw(shape);

        // _0_0_0_5
        shape = new RoundRectangle2D.Double(365.0588073730469, 144.4134979248047, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.draw(shape);

        // _0_0_0_6
        shape = new RoundRectangle2D.Double(365.0588073730469, 174.1005096435547, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.draw(shape);

        // _0_0_0_7
        shape = new RoundRectangle2D.Double(365.0588073730469, 203.78746032714844, 456.51806640625, 19.02947998046875, 5, 5);
        g.setPaint(new Color(0x2F2F2F));
        g.fill(shape);
        g.setPaint(new Color(0x1E221C));
        g.draw(shape);

        // _0_0_0_8
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(480.5295, 141.8853);
        ((GeneralPath) shape).lineTo(480.5295, 203.04936);
        ((GeneralPath) shape).lineTo(708.926, 203.04936);
        ((GeneralPath) shape).lineTo(708.926, 164.51225);
        ((GeneralPath) shape).lineTo(498.20728, 164.51225);
        ((GeneralPath) shape).lineTo(498.20728, 141.8853);
        ((GeneralPath) shape).lineTo(480.52954, 141.8853);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x3B4237));
        g.fill(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 395.94226f, -619.5918f));

        // _0_0_0_9
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 1.0893772f, 46.750927f));

        // _0_0_0_9_0

        // _0_0_0_9_0_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(87.79573, 771.2311);
        ((GeneralPath) shape).lineTo(88.15287, 719.80347);
        ((GeneralPath) shape).lineTo(93.599304, 719.80347);
        ((GeneralPath) shape).lineTo(93.599304, 752.7499);
        ((GeneralPath) shape).lineTo(96.81359, 752.7499);
        ((GeneralPath) shape).lineTo(107.43859, 738.9106);
        ((GeneralPath) shape).lineTo(114.49216, 738.9106);
        ((GeneralPath) shape).lineTo(102.43858, 753.9106);
        ((GeneralPath) shape).lineTo(115.56358, 771.2735);
        ((GeneralPath) shape).lineTo(108.331436, 771.2419);
        ((GeneralPath) shape).lineTo(96.54573, 755.5177);
        ((GeneralPath) shape).lineTo(93.77787, 755.5177);
        ((GeneralPath) shape).lineTo(93.77787, 771.2572);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x596353));
        g.fill(shape);
        g.draw(shape);

        // _0_0_0_9_0_1
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(191.61577, 771.15564);
        ((GeneralPath) shape).lineTo(191.61577, 740.56177);
        ((GeneralPath) shape).lineTo(193.50964, 739.0989);
        ((GeneralPath) shape).lineTo(209.03928, 739.0989);
        ((GeneralPath) shape).lineTo(210.74376, 740.8798);
        ((GeneralPath) shape).lineTo(211.65881, 740.8848);
        ((GeneralPath) shape).lineTo(211.65881, 739.0984);
        ((GeneralPath) shape).lineTo(229.1773, 739.0984);
        ((GeneralPath) shape).lineTo(230.62927, 740.81573);
        ((GeneralPath) shape).lineTo(230.62927, 771.15234);
        ((GeneralPath) shape).lineTo(224.9477, 771.2786);
        ((GeneralPath) shape).lineTo(224.9477, 744.37756);
        ((GeneralPath) shape).lineTo(214.784, 744.37756);
        ((GeneralPath) shape).lineTo(213.52142, 745.90405);
        ((GeneralPath) shape).lineTo(213.52142, 771.27905);
        ((GeneralPath) shape).lineTo(207.83986, 771.34216);
        ((GeneralPath) shape).lineTo(207.83986, 744.1867);
        ((GeneralPath) shape).lineTo(197.9918, 744.1867);
        ((GeneralPath) shape).lineTo(196.72923, 745.5224);
        ((GeneralPath) shape).lineTo(196.72923, 771.2173);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0036696f, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_0_2
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(123.42805, 739.274);
        ((GeneralPath) shape).lineTo(121.156555, 741.106);
        ((GeneralPath) shape).lineTo(121.156555, 768.9478);
        ((GeneralPath) shape).lineTo(123.64874, 771.28375);
        ((GeneralPath) shape).lineTo(147.79913, 771.34625);
        ((GeneralPath) shape).lineTo(150.32452, 769.07477);
        ((GeneralPath) shape).lineTo(150.32452, 741.231);
        ((GeneralPath) shape).lineTo(148.05109, 739.274);
        ((GeneralPath) shape).lineTo(123.42805, 739.274);
        ((GeneralPath) shape).closePath();
        ((GeneralPath) shape).moveTo(127.469055, 745.0826);
        ((GeneralPath) shape).lineTo(143.31671, 745.0826);
        ((GeneralPath) shape).lineTo(144.19952, 745.9029);
        ((GeneralPath) shape).lineTo(144.19952, 764.59235);
        ((GeneralPath) shape).lineTo(143.0628, 765.6646);
        ((GeneralPath) shape).lineTo(128.16437, 765.6646);
        ((GeneralPath) shape).lineTo(126.58625, 764.33844);
        ((GeneralPath) shape).lineTo(126.64875, 746.02985);
        ((GeneralPath) shape).lineTo(127.46906, 745.0826);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_0_3
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(159.46915, 741.4095);
        ((GeneralPath) shape).lineTo(161.55867, 739.22046);
        ((GeneralPath) shape).lineTo(181.908, 739.22046);
        ((GeneralPath) shape).lineTo(181.9989, 744.63794);
        ((GeneralPath) shape).lineTo(165.16425, 744.63794);
        ((GeneralPath) shape).lineTo(165.16425, 751.4743);
        ((GeneralPath) shape).lineTo(180.64536, 751.4743);
        ((GeneralPath) shape).lineTo(182.05858, 753.28015);
        ((GeneralPath) shape).lineTo(182.05858, 769.46814);
        ((GeneralPath) shape).lineTo(180.13147, 771.274);
        ((GeneralPath) shape).lineTo(159.3829, 771.274);
        ((GeneralPath) shape).lineTo(159.3829, 765.534);
        ((GeneralPath) shape).lineTo(175.37793, 765.534);
        ((GeneralPath) shape).lineTo(175.37793, 757.7947);
        ((GeneralPath) shape).lineTo(161.72693, 757.7947);
        ((GeneralPath) shape).lineTo(159.43156, 756.24414);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0194951f, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_0_4
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(242.85384, 739.274);
        ((GeneralPath) shape).lineTo(240.5804, 741.106);
        ((GeneralPath) shape).lineTo(240.5804, 768.9478);
        ((GeneralPath) shape).lineTo(243.07454, 771.28375);
        ((GeneralPath) shape).lineTo(267.223, 771.34625);
        ((GeneralPath) shape).lineTo(269.74838, 769.07477);
        ((GeneralPath) shape).lineTo(269.74838, 741.231);
        ((GeneralPath) shape).lineTo(267.4769, 739.274);
        ((GeneralPath) shape).lineTo(242.85385, 739.274);
        ((GeneralPath) shape).closePath();
        ((GeneralPath) shape).moveTo(246.89485, 745.0826);
        ((GeneralPath) shape).lineTo(262.74057, 745.0826);
        ((GeneralPath) shape).lineTo(263.62534, 745.9029);
        ((GeneralPath) shape).lineTo(263.62534, 764.59235);
        ((GeneralPath) shape).lineTo(262.48862, 765.6646);
        ((GeneralPath) shape).lineTo(247.58823, 765.6646);
        ((GeneralPath) shape).lineTo(246.0101, 764.33844);
        ((GeneralPath) shape).lineTo(246.0746, 746.02985);
        ((GeneralPath) shape).lineTo(246.89491, 745.0826);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_0_5
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(278.89426, 741.4095);
        ((GeneralPath) shape).lineTo(280.9838, 739.22046);
        ((GeneralPath) shape).lineTo(301.33313, 739.22046);
        ((GeneralPath) shape).lineTo(301.42404, 744.63794);
        ((GeneralPath) shape).lineTo(284.5894, 744.63794);
        ((GeneralPath) shape).lineTo(284.5894, 751.4743);
        ((GeneralPath) shape).lineTo(300.0705, 751.4743);
        ((GeneralPath) shape).lineTo(301.4837, 753.28015);
        ((GeneralPath) shape).lineTo(301.4837, 769.46814);
        ((GeneralPath) shape).lineTo(299.55658, 771.274);
        ((GeneralPath) shape).lineTo(278.808, 771.274);
        ((GeneralPath) shape).lineTo(278.808, 765.534);
        ((GeneralPath) shape).lineTo(294.80304, 765.534);
        ((GeneralPath) shape).lineTo(294.80304, 757.7947);
        ((GeneralPath) shape).lineTo(281.15204, 757.7947);
        ((GeneralPath) shape).lineTo(278.85666, 756.24414);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0194951f, 0, 0, 4));
        g.draw(shape);

        g.setTransform(transformations.pollLast()); // _0_0_0_9_0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 4.6909547f, 49.79658f));

        // _0_0_0_9_1

        // _0_0_0_9_1_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(87.79573, 771.2311);
        ((GeneralPath) shape).lineTo(88.15287, 719.80347);
        ((GeneralPath) shape).lineTo(93.599304, 719.80347);
        ((GeneralPath) shape).lineTo(93.599304, 752.7499);
        ((GeneralPath) shape).lineTo(96.81359, 752.7499);
        ((GeneralPath) shape).lineTo(107.43859, 738.9106);
        ((GeneralPath) shape).lineTo(114.49216, 738.9106);
        ((GeneralPath) shape).lineTo(102.43858, 753.9106);
        ((GeneralPath) shape).lineTo(115.56358, 771.2735);
        ((GeneralPath) shape).lineTo(108.331436, 771.2419);
        ((GeneralPath) shape).lineTo(96.54573, 755.5177);
        ((GeneralPath) shape).lineTo(93.77787, 755.5177);
        ((GeneralPath) shape).lineTo(93.77787, 771.2572);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x1E221C));
        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_1_1
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(191.61577, 771.15564);
        ((GeneralPath) shape).lineTo(191.61577, 740.56177);
        ((GeneralPath) shape).lineTo(193.50964, 739.0989);
        ((GeneralPath) shape).lineTo(209.03928, 739.0989);
        ((GeneralPath) shape).lineTo(210.74376, 740.8798);
        ((GeneralPath) shape).lineTo(211.65881, 740.8848);
        ((GeneralPath) shape).lineTo(211.65881, 739.0984);
        ((GeneralPath) shape).lineTo(229.1773, 739.0984);
        ((GeneralPath) shape).lineTo(230.62927, 740.81573);
        ((GeneralPath) shape).lineTo(230.62927, 771.15234);
        ((GeneralPath) shape).lineTo(224.9477, 771.2786);
        ((GeneralPath) shape).lineTo(224.9477, 744.37756);
        ((GeneralPath) shape).lineTo(214.784, 744.37756);
        ((GeneralPath) shape).lineTo(213.52142, 745.90405);
        ((GeneralPath) shape).lineTo(213.52142, 771.27905);
        ((GeneralPath) shape).lineTo(207.83986, 771.34216);
        ((GeneralPath) shape).lineTo(207.83986, 744.1867);
        ((GeneralPath) shape).lineTo(197.9918, 744.1867);
        ((GeneralPath) shape).lineTo(196.72923, 745.5224);
        ((GeneralPath) shape).lineTo(196.72923, 771.2173);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0036696f, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_1_2
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(123.42805, 739.274);
        ((GeneralPath) shape).lineTo(121.156555, 741.106);
        ((GeneralPath) shape).lineTo(121.156555, 768.9478);
        ((GeneralPath) shape).lineTo(123.64874, 771.28375);
        ((GeneralPath) shape).lineTo(147.79913, 771.34625);
        ((GeneralPath) shape).lineTo(150.32452, 769.07477);
        ((GeneralPath) shape).lineTo(150.32452, 741.231);
        ((GeneralPath) shape).lineTo(148.05109, 739.274);
        ((GeneralPath) shape).lineTo(123.42805, 739.274);
        ((GeneralPath) shape).closePath();
        ((GeneralPath) shape).moveTo(127.469055, 745.0826);
        ((GeneralPath) shape).lineTo(143.31671, 745.0826);
        ((GeneralPath) shape).lineTo(144.19952, 745.9029);
        ((GeneralPath) shape).lineTo(144.19952, 764.59235);
        ((GeneralPath) shape).lineTo(143.0628, 765.6646);
        ((GeneralPath) shape).lineTo(128.16437, 765.6646);
        ((GeneralPath) shape).lineTo(126.58625, 764.33844);
        ((GeneralPath) shape).lineTo(126.64875, 746.02985);
        ((GeneralPath) shape).lineTo(127.46906, 745.0826);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_1_3
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(159.46915, 741.4095);
        ((GeneralPath) shape).lineTo(161.55867, 739.22046);
        ((GeneralPath) shape).lineTo(181.908, 739.22046);
        ((GeneralPath) shape).lineTo(181.9989, 744.63794);
        ((GeneralPath) shape).lineTo(165.16425, 744.63794);
        ((GeneralPath) shape).lineTo(165.16425, 751.4743);
        ((GeneralPath) shape).lineTo(180.64536, 751.4743);
        ((GeneralPath) shape).lineTo(182.05858, 753.28015);
        ((GeneralPath) shape).lineTo(182.05858, 769.46814);
        ((GeneralPath) shape).lineTo(180.13147, 771.274);
        ((GeneralPath) shape).lineTo(159.3829, 771.274);
        ((GeneralPath) shape).lineTo(159.3829, 765.534);
        ((GeneralPath) shape).lineTo(175.37793, 765.534);
        ((GeneralPath) shape).lineTo(175.37793, 757.7947);
        ((GeneralPath) shape).lineTo(161.72693, 757.7947);
        ((GeneralPath) shape).lineTo(159.43156, 756.24414);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0194951f, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_1_4
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(242.85384, 739.274);
        ((GeneralPath) shape).lineTo(240.5804, 741.106);
        ((GeneralPath) shape).lineTo(240.5804, 768.9478);
        ((GeneralPath) shape).lineTo(243.07454, 771.28375);
        ((GeneralPath) shape).lineTo(267.223, 771.34625);
        ((GeneralPath) shape).lineTo(269.74838, 769.07477);
        ((GeneralPath) shape).lineTo(269.74838, 741.231);
        ((GeneralPath) shape).lineTo(267.4769, 739.274);
        ((GeneralPath) shape).lineTo(242.85385, 739.274);
        ((GeneralPath) shape).closePath();
        ((GeneralPath) shape).moveTo(246.89485, 745.0826);
        ((GeneralPath) shape).lineTo(262.74057, 745.0826);
        ((GeneralPath) shape).lineTo(263.62534, 745.9029);
        ((GeneralPath) shape).lineTo(263.62534, 764.59235);
        ((GeneralPath) shape).lineTo(262.48862, 765.6646);
        ((GeneralPath) shape).lineTo(247.58823, 765.6646);
        ((GeneralPath) shape).lineTo(246.0101, 764.33844);
        ((GeneralPath) shape).lineTo(246.0746, 746.02985);
        ((GeneralPath) shape).lineTo(246.89491, 745.0826);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_1_5
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(278.89426, 741.4095);
        ((GeneralPath) shape).lineTo(280.9838, 739.22046);
        ((GeneralPath) shape).lineTo(301.33313, 739.22046);
        ((GeneralPath) shape).lineTo(301.42404, 744.63794);
        ((GeneralPath) shape).lineTo(284.5894, 744.63794);
        ((GeneralPath) shape).lineTo(284.5894, 751.4743);
        ((GeneralPath) shape).lineTo(300.0705, 751.4743);
        ((GeneralPath) shape).lineTo(301.4837, 753.28015);
        ((GeneralPath) shape).lineTo(301.4837, 769.46814);
        ((GeneralPath) shape).lineTo(299.55658, 771.274);
        ((GeneralPath) shape).lineTo(278.808, 771.274);
        ((GeneralPath) shape).lineTo(278.808, 765.534);
        ((GeneralPath) shape).lineTo(294.80304, 765.534);
        ((GeneralPath) shape).lineTo(294.80304, 757.7947);
        ((GeneralPath) shape).lineTo(281.15204, 757.7947);
        ((GeneralPath) shape).lineTo(278.85666, 756.24414);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0194951f, 0, 0, 4));
        g.draw(shape);

        g.setTransform(transformations.pollLast()); // _0_0_0_9_1
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 2.89016f, 48.273743f));

        // _0_0_0_9_2

        // _0_0_0_9_2_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(87.79573, 771.2311);
        ((GeneralPath) shape).lineTo(88.15287, 719.80347);
        ((GeneralPath) shape).lineTo(93.599304, 719.80347);
        ((GeneralPath) shape).lineTo(93.599304, 752.7499);
        ((GeneralPath) shape).lineTo(96.81359, 752.7499);
        ((GeneralPath) shape).lineTo(107.43859, 738.9106);
        ((GeneralPath) shape).lineTo(114.49216, 738.9106);
        ((GeneralPath) shape).lineTo(102.43858, 753.9106);
        ((GeneralPath) shape).lineTo(115.56358, 771.2735);
        ((GeneralPath) shape).lineTo(108.331436, 771.2419);
        ((GeneralPath) shape).lineTo(96.54573, 755.5177);
        ((GeneralPath) shape).lineTo(93.77787, 755.5177);
        ((GeneralPath) shape).lineTo(93.77787, 771.2572);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x3B4237));
        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_2_1
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(191.61577, 771.15564);
        ((GeneralPath) shape).lineTo(191.61577, 740.56177);
        ((GeneralPath) shape).lineTo(193.50964, 739.0989);
        ((GeneralPath) shape).lineTo(209.03928, 739.0989);
        ((GeneralPath) shape).lineTo(210.74376, 740.8798);
        ((GeneralPath) shape).lineTo(211.65881, 740.8848);
        ((GeneralPath) shape).lineTo(211.65881, 739.0984);
        ((GeneralPath) shape).lineTo(229.1773, 739.0984);
        ((GeneralPath) shape).lineTo(230.62927, 740.81573);
        ((GeneralPath) shape).lineTo(230.62927, 771.15234);
        ((GeneralPath) shape).lineTo(224.9477, 771.2786);
        ((GeneralPath) shape).lineTo(224.9477, 744.37756);
        ((GeneralPath) shape).lineTo(214.784, 744.37756);
        ((GeneralPath) shape).lineTo(213.52142, 745.90405);
        ((GeneralPath) shape).lineTo(213.52142, 771.27905);
        ((GeneralPath) shape).lineTo(207.83986, 771.34216);
        ((GeneralPath) shape).lineTo(207.83986, 744.1867);
        ((GeneralPath) shape).lineTo(197.9918, 744.1867);
        ((GeneralPath) shape).lineTo(196.72923, 745.5224);
        ((GeneralPath) shape).lineTo(196.72923, 771.2173);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0036696f, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_2_2
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(123.42805, 739.274);
        ((GeneralPath) shape).lineTo(121.156555, 741.106);
        ((GeneralPath) shape).lineTo(121.156555, 768.9478);
        ((GeneralPath) shape).lineTo(123.64874, 771.28375);
        ((GeneralPath) shape).lineTo(147.79913, 771.34625);
        ((GeneralPath) shape).lineTo(150.32452, 769.07477);
        ((GeneralPath) shape).lineTo(150.32452, 741.231);
        ((GeneralPath) shape).lineTo(148.05109, 739.274);
        ((GeneralPath) shape).lineTo(123.42805, 739.274);
        ((GeneralPath) shape).closePath();
        ((GeneralPath) shape).moveTo(127.469055, 745.0826);
        ((GeneralPath) shape).lineTo(143.31671, 745.0826);
        ((GeneralPath) shape).lineTo(144.19952, 745.9029);
        ((GeneralPath) shape).lineTo(144.19952, 764.59235);
        ((GeneralPath) shape).lineTo(143.0628, 765.6646);
        ((GeneralPath) shape).lineTo(128.16437, 765.6646);
        ((GeneralPath) shape).lineTo(126.58625, 764.33844);
        ((GeneralPath) shape).lineTo(126.64875, 746.02985);
        ((GeneralPath) shape).lineTo(127.46906, 745.0826);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_2_3
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(159.46915, 741.4095);
        ((GeneralPath) shape).lineTo(161.55867, 739.22046);
        ((GeneralPath) shape).lineTo(181.908, 739.22046);
        ((GeneralPath) shape).lineTo(181.9989, 744.63794);
        ((GeneralPath) shape).lineTo(165.16425, 744.63794);
        ((GeneralPath) shape).lineTo(165.16425, 751.4743);
        ((GeneralPath) shape).lineTo(180.64536, 751.4743);
        ((GeneralPath) shape).lineTo(182.05858, 753.28015);
        ((GeneralPath) shape).lineTo(182.05858, 769.46814);
        ((GeneralPath) shape).lineTo(180.13147, 771.274);
        ((GeneralPath) shape).lineTo(159.3829, 771.274);
        ((GeneralPath) shape).lineTo(159.3829, 765.534);
        ((GeneralPath) shape).lineTo(175.37793, 765.534);
        ((GeneralPath) shape).lineTo(175.37793, 757.7947);
        ((GeneralPath) shape).lineTo(161.72693, 757.7947);
        ((GeneralPath) shape).lineTo(159.43156, 756.24414);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0194951f, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_2_4
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(242.85384, 739.274);
        ((GeneralPath) shape).lineTo(240.5804, 741.106);
        ((GeneralPath) shape).lineTo(240.5804, 768.9478);
        ((GeneralPath) shape).lineTo(243.07454, 771.28375);
        ((GeneralPath) shape).lineTo(267.223, 771.34625);
        ((GeneralPath) shape).lineTo(269.74838, 769.07477);
        ((GeneralPath) shape).lineTo(269.74838, 741.231);
        ((GeneralPath) shape).lineTo(267.4769, 739.274);
        ((GeneralPath) shape).lineTo(242.85385, 739.274);
        ((GeneralPath) shape).closePath();
        ((GeneralPath) shape).moveTo(246.89485, 745.0826);
        ((GeneralPath) shape).lineTo(262.74057, 745.0826);
        ((GeneralPath) shape).lineTo(263.62534, 745.9029);
        ((GeneralPath) shape).lineTo(263.62534, 764.59235);
        ((GeneralPath) shape).lineTo(262.48862, 765.6646);
        ((GeneralPath) shape).lineTo(247.58823, 765.6646);
        ((GeneralPath) shape).lineTo(246.0101, 764.33844);
        ((GeneralPath) shape).lineTo(246.0746, 746.02985);
        ((GeneralPath) shape).lineTo(246.89491, 745.0826);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1, 0, 0, 4));
        g.draw(shape);

        // _0_0_0_9_2_5
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(278.89426, 741.4095);
        ((GeneralPath) shape).lineTo(280.9838, 739.22046);
        ((GeneralPath) shape).lineTo(301.33313, 739.22046);
        ((GeneralPath) shape).lineTo(301.42404, 744.63794);
        ((GeneralPath) shape).lineTo(284.5894, 744.63794);
        ((GeneralPath) shape).lineTo(284.5894, 751.4743);
        ((GeneralPath) shape).lineTo(300.0705, 751.4743);
        ((GeneralPath) shape).lineTo(301.4837, 753.28015);
        ((GeneralPath) shape).lineTo(301.4837, 769.46814);
        ((GeneralPath) shape).lineTo(299.55658, 771.274);
        ((GeneralPath) shape).lineTo(278.808, 771.274);
        ((GeneralPath) shape).lineTo(278.808, 765.534);
        ((GeneralPath) shape).lineTo(294.80304, 765.534);
        ((GeneralPath) shape).lineTo(294.80304, 757.7947);
        ((GeneralPath) shape).lineTo(281.15204, 757.7947);
        ((GeneralPath) shape).lineTo(278.85666, 756.24414);
        ((GeneralPath) shape).closePath();

        g.fill(shape);
        g.setStroke(new BasicStroke(1.0194951f, 0, 0, 4));
        g.draw(shape);

        g.setTransform(transformations.pollLast()); // _0_0_0_9_2

        g.setTransform(transformations.pollLast()); // _0_0_0_9

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
        return 489;
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    public static int getOrigHeight() {
        return 212;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

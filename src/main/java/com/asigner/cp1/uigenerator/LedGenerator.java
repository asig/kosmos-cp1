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

package com.asigner.cp1.uigenerator;

import com.asigner.cp1.ui.CP1Colors;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Color.WHITE;
import static java.awt.MultipleGradientPaint.ColorSpaceType.SRGB;
import static java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE;

public class LedGenerator {

    private int w = 35;
    private int h = 35;

    public void generate() throws IOException {
        String path = "src/main/resources/com/asigner/cp1/ui";
        new File(path).mkdirs();

        BufferedImage img = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        configureG2d(g2d);
        paint(g2d, true);
        ImageIO.write(img, "png", new File(path + "/led_on.png"));

        img = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        configureG2d(g2d);
        paint(g2d, false);
        ImageIO.write(img, "png", new File(path + "/led_off.png"));
    }

    private void configureG2d(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    public void paint(Graphics2D g, boolean on) {
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

        g.setPaint(CP1Colors.awt(CP1Colors.GREEN));
        g.fillRect(0, 0, w, h);

        double scale = (double)h/(double)getOrigHeight();
        g.scale(scale, scale);

        //
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 0, 0));

        // _0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -347.32144f, -504.68365f));

        // _0_0
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -118.87552f, 438.09518f));

        // _0_0_0

        // _0_0_0_0
        shape = new Ellipse2D.Double(466.19696044921875, 66.58847045898438, 55.357139587402344, 55.357139587402344);
        g.setPaint(new Color(0x0E100D));
        g.fill(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -1.7857056f, -1.7857132f));

        // _0_0_0_1

        // _0_0_0_1_0
        shape = new Ellipse2D.Double(475.3040771484375, 75.69560241699219, 40.71428680419922, 40.71428680419922);
        g.setPaint(on ? new Color(0xFF0101) : new Color(0x870002));
        g.fill(shape);
        g.setComposite(AlphaComposite.getInstance(3, 0.65f * origAlpha));

        // _0_0_0_1_1
        shape = new Ellipse2D.Double(482.0127258300781, 82.71663665771484, 14.831945419311523, 14.55772590637207);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(-0.25253820419311523, 547.285888671875), 20.357143f, new Point2D.Double(-0.25253820419311523, 547.285888671875), new float[]{0, 1}, new Color[]{WHITE, new Color(0xFFFFFF, true)}, NO_CYCLE, SRGB, new AffineTransform(0.36429337f, 0, 0, 0.3575582f, 489.52072f, -105.69106f)));
        g.fill(shape);

        g.setTransform(transformations.pollLast()); // _0_0_0_1

        g.setTransform(transformations.pollLast()); // _0_0_0

        g.setTransform(transformations.pollLast()); // _0_0

        g.setTransform(transformations.pollLast()); // _0

    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     *
     * @return The width of the bounding box of the original SVG image.
     */
    public static int getOrigWidth() {
        return 60;
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     *
     * @return The height of the bounding box of the original SVG image.
     */
    public static int getOrigHeight() {
        return 60;
    }

    public static void main(String ... args) throws IOException {
        LedGenerator gen = new LedGenerator();
        gen.generate();
    }
}

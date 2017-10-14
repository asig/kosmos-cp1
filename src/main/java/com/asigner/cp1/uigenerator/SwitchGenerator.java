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

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SwitchGenerator {

    private static final String PATH = "src/main/resources/com/asigner/cp1/ui/switch/";

    private static final int SWITCH_HEIGHT = 60;

    private int w, h;

    public SwitchGenerator() {
        double scale = (double) getOrigWidth() / (double) getOrigHeight();
        w = (int)(SWITCH_HEIGHT * scale);
        h = SWITCH_HEIGHT;
    }

    public void generate() throws IOException {
        generate(false, false);
        generate(true, false);
        generate(false, true);
        generate(true, true);
    }

    public void generate(boolean on, boolean pressed) throws IOException {
        String path = "src/main/resources/com/asigner/cp1/ui";
        new File(path).mkdirs();

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        configureG2d(g2d);
        paint(g2d, on, pressed);
        String name = String.format("%s/switch_%s%s.png", path, on ? "on" : "off", pressed ? "_pressed" : "");
        ImageIO.write(img, "png", new File(name));
    }

    private void configureG2d(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    /**
     * Paints the transcoded SVG image on the specified graphics context. You
     * can install a custom transformation on the graphics context to scale the
     * image.
     *
     * @param g Graphics context.
     */
    public void paint(Graphics2D g, boolean on, boolean pressed) {
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

        double scale = (double)SWITCH_HEIGHT/(double)getOrigHeight();
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
        g.setPaint(new java.awt.Color(0x0E100D));
        g.fill(shape);

        // _0_0_0_1
        shape = new Rectangle2D.Double(574.32861328125, 56.62732696533203, 47.90354919433594, 104.72515869140625);
        g.setPaint(new java.awt.Color(0x4E4634));
        g.fill(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, 305.06607f, -403.05087f));

        // _0_0_0_2

        // _0_0_0_2_0
        shape = new Rectangle2D.Double(277.1428527832031, 476.290771484375, 32.14285659790039, 68.92857360839844);
        g.setPaint(new java.awt.Color(0x302D1C));
        g.fill(shape);

        // _0_0_0_2_1

        // _0_0_0_2_1_0
        shape = new Rectangle2D.Double(276.4285888671875, 508.8622131347656, 33.57143020629883, 38.92856979370117);
        g.setPaint(new java.awt.Color(0x151615));
        g.fill(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(1, 0, 0, 1, -5.722046E-6f, -0.03760874f));

        // _0_0_0_2_1_1

        // _0_0_0_2_1_1_0
        shape = new Rectangle2D.Double(278.75390625, 514.7294921875, 28.920806884765625, 2.5575592517852783);
        g.setPaint(new java.awt.Color(0x303030));
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
            g.setPaint(new java.awt.Color(255, 255, 255, 100));
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

    public static void main(String ... args) throws IOException {
        SwitchGenerator generator = new SwitchGenerator();
        generator.generate();
    }
}

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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ContactGenerator {

    private final static int w = 960/35; // Control panel is 960 wide, needs room for ~35 slots
    private final static double scale = (double)w/(double)ContactTop.getOrigWidth();
    private final static int h = (int)(ContactTop.getOrigHeight() * scale);

    private static class ContactBottom {

        /**
         * Paints the transcoded SVG image on the specified graphics context. You
         * can install a custom transformation on the graphics context to scale the
         * image.
         *
         * @param g Graphics context.
         */
        public static void paint(Graphics2D g) {
            Shape shape = null;

            float origAlpha = 1.0f;
            Composite origComposite = g.getComposite();
            if (origComposite instanceof AlphaComposite) {
                AlphaComposite origAlphaComposite = (AlphaComposite)origComposite;
                if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                    origAlpha = origAlphaComposite.getAlpha();
                }
            }

            g.scale(scale, scale);

            java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();


            //
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 0, -4.0690106E-6f));

            // _0
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(1, 0, 0, 1, 882.1452f, -32.18148f));

            // _0_0
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(-1, 0, 0, -1, 0, 0));

            // _0_0_0
            shape = new Rectangle2D.Double(842.1452026367188, -112.54295349121094, 40, 71.6482925415039);
            g.setPaint(new Color(0x3F5D47));
            g.fill(shape);

            g.setTransform(transformations.pop()); // _0_0_0

            // _0_0_1
            shape = new GeneralPath();
            ((GeneralPath) shape).moveTo(-845.1809, 112.53037);
            ((GeneralPath) shape).lineTo(-879.1095, 112.53037);
            ((GeneralPath) shape).lineTo(-879.1095, 77.401505);
            ((GeneralPath) shape).lineTo(-862.1452, 66.33846);
            ((GeneralPath) shape).lineTo(-845.1809, 77.401505);
            ((GeneralPath) shape).closePath();

            g.setPaint(new Color(0xFDC8A8));
            g.fill(shape);
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(-1, 0, 0, -1, 0, 0));

            // _0_0_2
            shape = new Rectangle2D.Double(860.0023193359375, -67.72998809814453, 4.285714149475098, 26.83538818359375);
            g.setPaint(new Color(0x506D5D));
            g.fill(shape);

            g.setTransform(transformations.pop()); // _0_0_2
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(-1, 0, 0, -1, 0, 0));

            // _0_0_3
            shape = new Ellipse2D.Double(854.3165283203125, -92.11280822753906, 15.657364845275879, 15.657364845275879);
            g.setPaint(new Color(0x010001));
            g.fill(shape);

            g.setTransform(transformations.pop()); // _0_0_3
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(-1, 0, 0, -1, 0, 0));

            // _0_0_4
            shape = new Rectangle2D.Double(842.1452026367188, -40.89448165893555, 40, 8.713000297546387);
            g.setPaint(new Color(0x595D53));
            g.fill(shape);

            g.setTransform(transformations.pop()); // _0_0_4

            g.setTransform(transformations.pop()); // _0_0

            g.setTransform(transformations.pop()); // _0

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
            return 43;
        }

        /**
         * Returns the height of the bounding box of the original SVG image.
         *
         * @return The height of the bounding box of the original SVG image.
         */
        public static int getOrigHeight() {
            return 86;
        }
    }


    /**
     * This class has been automatically generated using
     * <a href="http://ebourg.github.io/flamingo-svg-transcoder/">Flamingo SVG transcoder</a>.
     */
    private static class ContactTop {

        /**
         * Paints the transcoded SVG image on the specified graphics context. You
         * can install a custom transformation on the graphics context to scale the
         * image.
         *
         * @param g Graphics context.
         */
        public static void paint(Graphics2D g) {
            Shape shape = null;

            float origAlpha = 1.0f;
            Composite origComposite = g.getComposite();
            if (origComposite instanceof AlphaComposite) {
                AlphaComposite origAlphaComposite = (AlphaComposite)origComposite;
                if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                    origAlpha = origAlphaComposite.getAlpha();
                }
            }


            g.scale(scale, scale);


            java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();



            //
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(1.0666667f, 0, 0, 1.0666667f, 0, -4.0690106E-6f));

            // _0
            transformations.push(g.getTransform());
            g.transform(new AffineTransform(1, 0, 0, 1, 882.1452f, -32.18148f));

            // _0_0

            // _0_0_0
            shape = new Rectangle2D.Double(-882.1452026367188, 32.181480407714844, 40, 71.6482925415039);
            g.setPaint(new Color(0x3F5D47));
            g.fill(shape);

            // _0_0_1
            shape = new GeneralPath();
            ((GeneralPath) shape).moveTo(-879.1095, 32.19406);
            ((GeneralPath) shape).lineTo(-845.1809, 32.19406);
            ((GeneralPath) shape).lineTo(-845.1809, 67.32293);
            ((GeneralPath) shape).lineTo(-862.1452, 78.38597);
            ((GeneralPath) shape).lineTo(-879.1095, 67.32293);
            ((GeneralPath) shape).closePath();

            g.setPaint(new Color(0xFDC8A8));
            g.fill(shape);

            // _0_0_2
            shape = new Rectangle2D.Double(-864.2880859375, 76.99443817138672, 4.285714149475098, 26.83538818359375);
            g.setPaint(new Color(0x506D5D));
            g.fill(shape);

            // _0_0_3
            shape = new Ellipse2D.Double(-869.973876953125, 52.61161804199219, 15.657364845275879, 15.657364845275879);
            g.setPaint(new Color(0x010001));
            g.fill(shape);

            // _0_0_4
            shape = new Rectangle2D.Double(-882.1452026367188, 103.82994842529297, 40, 8.713000297546387);
            g.setPaint(new Color(0x1E221C));
            g.fill(shape);

            g.setTransform(transformations.pop()); // _0_0

            g.setTransform(transformations.pop()); // _0

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
            return 43;
        }

        /**
         * Returns the height of the bounding box of the original SVG image.
         *
         * @return The height of the bounding box of the original SVG image.
         */
        public static int getOrigHeight() {
            return 86;
        }
    }



    public void generate() throws IOException {
        String path = "src/main/resources/com/asigner/cp1/ui";
        new File(path).mkdirs();

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        configureG2d(g2d);
        ContactTop.paint(g2d);
        ImageIO.write(img, "png", new File(path + "/contact_top.png"));

        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        configureG2d(g2d);
        ContactBottom.paint(g2d);
        ImageIO.write(img, "png", new File(path + "/contact_bottom.png"));
    }

    private void configureG2d(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    public static void main(String ... args) throws IOException {
        ContactGenerator gen = new ContactGenerator();
        gen.generate();
    }
}

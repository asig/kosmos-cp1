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

package com.asigner.cp1.ui;

import com.google.common.collect.Maps;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.widgets.Display;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SWTResources {

    private static final Map<RGBA, Color> colors = Maps.newHashMap();
    private static final Map<String, Image> images = Maps.newHashMap();

    public static final Color WHITE = getColor(new RGB(255,255,255));
    public static final Color BLACK = getColor(new RGB(0,0,0));
    public static final Color GRAY50 = getColor(new RGB(128,128,128));
    public static final Color YELLOW = getColor(new RGB(255,255,0));
    public static final Color RED = getColor(new RGB(255,0,0));

    public static Image getImage(String path) {
        Image img = images.get(path);
        if (img == null) {
            img = new Image(Display.getDefault(), SWTResources.class.getResourceAsStream(path));
            images.put(path, img);
        }
        return img;
    }

    public static Color getColor(RGB rgb) {
        if (rgb == null) {
            return null;
        }
        return getColor(new RGBA(rgb.red, rgb.green, rgb.blue, 255));
    }

    public static Color getColor(RGBA rgba) {
        if (rgba == null) {
            return null;
        }
        Color c = colors.get(rgba);
        if (c == null) {
            c = new Color(Display.getDefault(), rgba);
            colors.put(rgba, c);
        }
        return c;
    }

    static {
        loadFont("/com/asigner/cp1/ui/helvetica-light-586c33cdd0018.ttf");
        loadFont("/com/asigner/cp1/ui/helvetica-black-586c33de4b4d0.otf");
    }

    private static void loadFont(String path) {
        try {
            Path tmpFont = Files.createTempFile("font", ".ttf");
            Files.copy(SWTResources.class.getResourceAsStream(path), tmpFont, REPLACE_EXISTING);
            Display.getDefault().loadFont(tmpFont.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

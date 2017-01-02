// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation.ui;

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
        loadFont("/com/asigner/cp1/emulation/ui/Pragmatica_black_regular.ttf");
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

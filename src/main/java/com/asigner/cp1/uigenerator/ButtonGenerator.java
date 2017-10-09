package com.asigner.cp1.uigenerator;

import com.asigner.cp1.ui.CP1Colors;
import com.asigner.cp1.ui.OS;
import com.asigner.cp1.ui.SWTResources;
import com.google.common.base.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import java.io.File;

public class ButtonGenerator {

    private static final String PATH = "src/main/resources/com/asigner/cp1/ui/buttons/";

    private static final int BUTTON_HEIGHT = 50;

    private static final String LARGE_FONT_NAME = "Helvetica Black";
    private static final String SMALL_FONT_NAME = "Helvetica";
    private static final boolean isMac = OS.isMac();

    private boolean pressed = false;

    private Font largeFont;
    private Font smallFont;

    private String text = "";
    private String subText = "";
    private int w, h;

    public ButtonGenerator(double f, String text, String subText) {
        this.w = (int)(f * BUTTON_HEIGHT);
        this.h = BUTTON_HEIGHT;
        this.text = text;
        this.subText = subText;

        initializeFonts();
    }

    private void initializeFonts() {
        int height = h;
        if (largeFont != null) {
            largeFont.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }

        Display display = Display.getDefault();
        double largeTextSizeFactor = Strings.isNullOrEmpty(subText) ? 0.5 : 0.375;
        largeFont = new Font(display, LARGE_FONT_NAME, scaleFontSize(height * largeTextSizeFactor), SWT.NONE);
        smallFont = new Font(display, SMALL_FONT_NAME, scaleFontSize(height * 0.15), SWT.NONE);
    }

    private int scaleFontSize(double fontSize) {
        return (int)(isMac ? 1.3 * fontSize : fontSize);
    }

    public void generate(String name) {
        generate(name, false);
        generate(name, true);
    }

    public void generate(String name, boolean pressed) {
        this.pressed = pressed;

        new File(PATH).mkdirs();

        Image img = new Image(Display.getDefault(), w, h);
        GC gc = new GC(img);
        paint(gc);

        ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[] { img.getImageData() };
        imageLoader.save(PATH + name + (pressed ? "_pressed" : "") + ".png",SWT.IMAGE_PNG);
    }

    private void paint(GC gc) {
        gc.setAntialias(SWT.ON);
        Rectangle bounds = new Rectangle(0, 0, w, h);

        gc.setForeground(CP1Colors.PANEL_BACKGROUND);
        gc.setBackground(CP1Colors.PANEL_BACKGROUND);
        gc.fillRectangle(bounds);

        Color fg = (pressed) ? SWTResources.GRAY50 : SWTResources.BLACK;
        gc.setForeground(fg);
        gc.setBackground(SWTResources.WHITE);

        int r = bounds.height/2;

        gc.setLineWidth(1);
        gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width-1, bounds.height-1, r, r);
        gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width-1, bounds.height-1, r, r);

        gc.setLineWidth(1);
        gc.drawRoundRectangle(bounds.x+3, bounds.y+3, bounds.width-6-1, bounds.height-6-1, (int)(r*0.8), (int)(r*0.8));

        gc.setFont(largeFont);
        Point pt = gc.textExtent(text);
        int h = pt.y;
        int largeTextY;
        if (Strings.isNullOrEmpty(subText)) {
            // Only large text, center it.
            largeTextY = bounds.y + (bounds.height - h)/2;
            // Needs some manual adjustment...
            largeTextY -= (int)(bounds.height*0.05);
        } else {
            largeTextY = bounds.y + (int)( (bounds.height - h)/2 * 0.2);
        }
        gc.drawString(text, bounds.x+(bounds.width-pt.x)/2, largeTextY, true);

        if (!Strings.isNullOrEmpty(subText)) {
            gc.setFont(smallFont);
            pt = gc.textExtent(subText);
            int y = bounds.y + bounds.height - 6 - pt.y;
            gc.drawString(subText, bounds.x+(bounds.width-pt.x)/2, y, true);
        }
    }

    public static void main(String ... args) {
        new ButtonGenerator(1.1, "0", "").generate("0");
        new ButtonGenerator(1.1, "1", "").generate("1");
        new ButtonGenerator(1.1, "2", "").generate("2");
        new ButtonGenerator(1.1, "3", "").generate("3");
        new ButtonGenerator(1.1, "4", "").generate("4");
        new ButtonGenerator(1.1, "5", "").generate("5");
        new ButtonGenerator(1.1, "6", "").generate("6");
        new ButtonGenerator(1.1, "7", "").generate("7");
        new ButtonGenerator(1.1, "8", "").generate("8");
        new ButtonGenerator(1.1, "9", "").generate("9");

        new ButtonGenerator(3.0, "0", "").generate("0w");
        new ButtonGenerator(1.4, "1", "").generate("1w");
        new ButtonGenerator(1.4, "2", "").generate("2w");
        new ButtonGenerator(1.4, "3", "").generate("3w");
        new ButtonGenerator(1.4, "4", "").generate("4w");
        new ButtonGenerator(1.4, "5", "").generate("5w");
        new ButtonGenerator(1.4, "6", "").generate("6w");
        new ButtonGenerator(1.4, "7", "").generate("7w");
        new ButtonGenerator(1.4, "8", "").generate("8w");
        new ButtonGenerator(1.4, "9", "").generate("9w");

        new ButtonGenerator(2.6, "STEP", "Schritt").generate("step");
        new ButtonGenerator(2.6, "STP", "Stopp").generate("stp");
        new ButtonGenerator(2.6, "RUN", "Lauf").generate("run");
        new ButtonGenerator(2.6, "CAL", "Cass. lesen").generate("cal");
        new ButtonGenerator(3.2, "CLR", "Irrtum").generate("clr");
        new ButtonGenerator(3.2, "ACC", "Akku").generate("acc");
        new ButtonGenerator(3.2, "CAS", "Cass. speichern").generate("cas");
        new ButtonGenerator(3.2, "PC", "Programmzähler").generate("pc");
        new ButtonGenerator(3.2, "OUT", "auslesen").generate("out");
        new ButtonGenerator(3.2, "INP", "eingeben").generate("inp");
    }
}

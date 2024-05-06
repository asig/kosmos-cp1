#include "colors.h"
#include "save.h"

#include <QSvgRenderer>
#include <QPainter>
#include <QImage>
#include <QFontDatabase>
#include <QString>
#include <QPainterPath>
#include <QFile>

#include <iostream>

namespace {

constexpr const int BUTTON_HEIGHT = 50;

constexpr const char *LARGE_FONT_NAME = ":/fonts/helvetica-black-586c33de4b4d0.otf";
constexpr const char *SMALL_FONT_NAME = ":/fonts/helvetica-light-586c33cdd0018.ttf";

QFont makeFont(const char* path) {
    int id = QFontDatabase::addApplicationFont(path);
    QString family = QFontDatabase::applicationFontFamilies(id).at(0);
    return QFont(family);
}

void generate_button(double scale, const char *text, const char *subtext, bool isPressed, const std::string &basename) {
    int w = (int)(scale * BUTTON_HEIGHT);
    int h = BUTTON_HEIGHT;

    double largeTextSizeFactor = subtext == nullptr ? 0.7 : 0.5;
    QFont largeFont = makeFont(LARGE_FONT_NAME);
    largeFont.setPixelSize(h * largeTextSizeFactor);
    QFontMetrics largeFontM(largeFont);

    QFont smallFont = makeFont(SMALL_FONT_NAME);
    smallFont.setWeight(QFont::Light);
    smallFont.setPixelSize(h * 0.2);
    QFontMetrics smallFontM(smallFont);

    QImage image(w, h, QImage::Format_RGB32);
    QPainter painter(&image);


    painter.setRenderHint(QPainter::Antialiasing, true);
    painter.setRenderHint(QPainter::TextAntialiasing, true);
    painter.setBrush(QBrush(kosmos_cp1::generator::PANEL_BACKGROUND));
    painter.fillRect(0,0,w,h,kosmos_cp1::generator::PANEL_BACKGROUND);

    QColor fg = isPressed ?  QColor(128,128,128) : Qt::black;
    painter.setPen(fg);
    painter.setBackground(QBrush(QColor(255,255,255)));

    int r = h*0.25;
    painter.setPen(fg);

    painter.setBrush(QColor(255,255,255));
    // See https://stackoverflow.com/questions/29196610/qt-drawing-a-filled-rounded-rectangle-with-border
    // for why QRectF with .5 offets is used.
    painter.drawRoundedRect(QRectF(0.5,0.5,w-1,h-1), r,r,Qt::AbsoluteSize);

    painter.setBrush(QColor(255,255,255));
    painter.drawRoundedRect(QRectF(3.5,3.5,w-7,h-7),r,r,Qt::AbsoluteSize);

    painter.setFont(largeFont);
    auto br = largeFontM.boundingRect(text);
    int tH = br.height();
    int largeTextY;
    if (subtext == nullptr) {
        // Only large text, center it.
        largeTextY = (h - tH)/2 - br.y();
    } else {
        largeTextY = (int)(( (h - tH)/2  - br.y())) * 0.8;
    }
    painter.drawText( (w-br.width())/2, largeTextY, text);

    if (subtext) {
        painter.setFont(smallFont);
        br = smallFontM.boundingRect(subtext);
        int y = h - 6 - smallFontM.descent();
        painter.drawText( (w - br.width())/2, y, subtext);
    }

    ::kosmos_cp1::generator::save(image, basename + (isPressed ? "_pressed" : "") + ".png");
}

void generate_button(double scale, const char *text, const char *subtext, std::string basename) {
    generate_button(scale, text, subtext, false, basename);
    generate_button(scale, text, subtext, true, basename);
}

}

namespace kosmos_cp1::generator {

void generate_buttons(const std::string& root) {
    generate_button(1.1, "0", nullptr, root + "/0");
    generate_button(1.1, "1", nullptr, root + "/1");
    generate_button(1.1, "2", nullptr, root + "/2");
    generate_button(1.1, "3", nullptr, root + "/3");
    generate_button(1.1, "4", nullptr, root + "/4");
    generate_button(1.1, "5", nullptr, root + "/5");
    generate_button(1.1, "6", nullptr, root + "/6");
    generate_button(1.1, "7", nullptr, root + "/7");
    generate_button(1.1, "8", nullptr, root + "/8");
    generate_button(1.1, "9", nullptr, root + "/9");


    generate_button(3.0, "0", nullptr, root + "/0w");
    generate_button(1.4, "1", nullptr, root + "/1w");
    generate_button(1.4, "2", nullptr, root + "/2w");
    generate_button(1.4, "3", nullptr, root + "/3w");
    generate_button(1.4, "4", nullptr, root + "/4w");
    generate_button(1.4, "5", nullptr, root + "/5w");
    generate_button(1.4, "6", nullptr, root + "/6w");
    generate_button(1.4, "7", nullptr, root + "/7w");
    generate_button(1.4, "8", nullptr, root + "/8w");
    generate_button(1.4, "9", nullptr, root + "/9w");

    generate_button(2.6, "STEP","Schritt", root + "/step");
    generate_button(2.6, "STP", "Stopp", root + "/stp");
    generate_button(2.6, "RUN", "Lauf", root + "/run");
    generate_button(2.6, "CAL", "Cass. lesen", root + "/cal");
    generate_button(3.2, "CLR", "Irrtum", root + "/clr");
    generate_button(3.2, "ACC", "Akku", root + "/acc");
    generate_button(3.2, "CAS", "Cass. speichern", root + "/cas");
    generate_button(3.2, "PC",  "Programmzähler", root + "/pc");
    generate_button(3.2, "OUT", "auslesen", root + "/out");
    generate_button(3.2, "INP", "eingeben", root + "/inp");
}

}

//    int id = QFontDatabase::addApplicationFont(":/fonts/helvetica-light-586c33cdd0018.ttf")
//    QString family = QFontDatabase::applicationFontFamilies(id).at(0);
//    return QFont(family);


//    // Load your SVG
//    QSvgRenderer renderer(QString("./svg-logo-h.svg"));

//    // Prepare a QImage with desired characteritisc
//    QImage image(500, 200, QImage::Format_ARGB32);
//    image.fill(0xaaA08080);  // partly transparent red-ish background

//    // Get QPainter that paints to the image
//    QPainter painter(&image);
//    renderer.render(&painter);

//    // Save, image format based on file extension
//    image.save("./svg-logo-h.png");

//}



//public class ButtonGenerator {


//private static final String LARGE_FONT_NAME = "Helvetica Black";
//private static final String SMALL_FONT_NAME = "Helvetica";
//private static final boolean isMac = OS.isMac();

//private boolean pressed = false;

//private Font largeFont;
//private Font smallFont;

//private String text = "";
//private String subText = "";
//private int w, h;

//public ButtonGenerator(double f, String text, String subText) {
//        this.w = (int)(f * BUTTON_HEIGHT);
//        this.h = BUTTON_HEIGHT;
//        this.text = text;
//        this.subText = subText;

//        initializeFonts();
//    }


//private int scaleFontSize(double fontSize) {
//        return (int)(isMac ? 1.3 * fontSize : fontSize);
//    }

//public void generate(String name) {
//        generate(name, false);
//        generate(name, true);
//    }

//public void generate(String name, boolean pressed) {
//        this.pressed = pressed;

//        new File(PATH).mkdirs();

//        Image img = new Image(Display.getDefault(), w, h);
//        GC gc = new GC(img);
//        paint(gc);

//        ImageLoader imageLoader = new ImageLoader();
//        imageLoader.data = new ImageData[] { img.getImageData() };
//        imageLoader.save(PATH + name + (pressed ? "_pressed" : "") + ".png",SWT.IMAGE_PNG);
//    }

//private void paint(GC gc) {
//        gc.setAntialias(SWT.ON);
//        Rectangle bounds = new Rectangle(0, 0, w, h);

//        gc.setForeground(CP1Colors.PANEL_BACKGROUND);
//        gc.setBackground(CP1Colors.PANEL_BACKGROUND);
//        gc.fillRectangle(bounds);

//        Color fg = (pressed) ? SWTResources.GRAY50 : SWTResources.BLACK;
//        gc.setForeground(fg);
//        gc.setBackground(SWTResources.WHITE);

//        int r = bounds.height/2;

//        gc.setLineWidth(1);
//        gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width-1, bounds.height-1, r, r);
//        gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width-1, bounds.height-1, r, r);

//        gc.setLineWidth(1);
//        gc.drawRoundRectangle(bounds.x+3, bounds.y+3, bounds.width-6-1, bounds.height-6-1, (int)(r*0.8), (int)(r*0.8));

//        gc.setFont(largeFont);
//        Point pt = gc.textExtent(text);
//        int h = pt.y;
//        int largeTextY;
//        if (Strings.isNullOrEmpty(subText)) {
//            // Only large text, center it.
//            largeTextY = bounds.y + (bounds.height - h)/2;
//            // Needs some manual adjustment...
//            largeTextY -= (int)(bounds.height*0.05);
//        } else {
//            largeTextY = bounds.y + (int)( (bounds.height - h)/2 * 0.2);
//        }
//        gc.drawString(text, bounds.x+(bounds.width-pt.x)/2, largeTextY, true);

//        if (!Strings.isNullOrEmpty(subText)) {
//            gc.setFont(smallFont);
//            pt = gc.textExtent(subText);
//            int y = bounds.y + bounds.height - 6 - pt.y;
//            gc.drawString(subText, bounds.x+(bounds.width-pt.x)/2, y, true);
//        }
//    }

//public static void main(String ... args) {
//    }
//}

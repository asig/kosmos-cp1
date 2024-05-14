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

void generate_button(double scale, double largeFontHorAdjust, const char *text, const char *subtext, bool isPressed, const std::string &basename) {
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
    painter.drawText( (w-br.width()*largeFontHorAdjust)/2, largeTextY, text);

    if (subtext) {
        painter.setFont(smallFont);
        br = smallFontM.boundingRect(subtext);
        int y = h - 6 - smallFontM.descent();
        painter.drawText( (w - br.width())/2, y, subtext);
    }

    ::kosmos_cp1::generator::save(image, basename + (isPressed ? "_pressed" : "") + ".png");
}

void generate_button(double scale, double largeFontHorAdjust, const char *text, const char *subtext, std::string basename) {
    generate_button(scale, largeFontHorAdjust, text, subtext, false, basename);
    generate_button(scale, largeFontHorAdjust, text, subtext, true, basename);
}

}

namespace kosmos_cp1::generator {

void generate_buttons(const std::string& root) {
    generate_button(1.1, 1, "0", nullptr, root + "/0");
    generate_button(1.1, 1.5, "1", nullptr, root + "/1");
    generate_button(1.1, 1, "2", nullptr, root + "/2");
    generate_button(1.1, 1, "3", nullptr, root + "/3");
    generate_button(1.1, 1, "4", nullptr, root + "/4");
    generate_button(1.1, 1, "5", nullptr, root + "/5");
    generate_button(1.1, 1, "6", nullptr, root + "/6");
    generate_button(1.1, 1, "7", nullptr, root + "/7");
    generate_button(1.1, 1, "8", nullptr, root + "/8");
    generate_button(1.1, 1, "9", nullptr, root + "/9");


    generate_button(3.0, 1, "0", nullptr, root + "/0w");
    generate_button(1.4, 1.5, "1", nullptr, root + "/1w");
    generate_button(1.4, 1, "2", nullptr, root + "/2w");
    generate_button(1.4, 1, "3", nullptr, root + "/3w");
    generate_button(1.4, 1, "4", nullptr, root + "/4w");
    generate_button(1.4, 1, "5", nullptr, root + "/5w");
    generate_button(1.4, 1, "6", nullptr, root + "/6w");
    generate_button(1.4, 1, "7", nullptr, root + "/7w");
    generate_button(1.4, 1, "8", nullptr, root + "/8w");
    generate_button(1.4, 1, "9", nullptr, root + "/9w");

    generate_button(2.6, 1, "STEP","Schritt", root + "/step");
    generate_button(2.6, 1, "STP", "Stopp", root + "/stp");
    generate_button(2.6, 1, "RUN", "Lauf", root + "/run");
    generate_button(2.6, 1, "CAL", "Cass. lesen", root + "/cal");
    generate_button(3.2, 1, "CLR", "Irrtum", root + "/clr");
    generate_button(3.2, 1, "ACC", "Akku", root + "/acc");
    generate_button(3.2, 1, "CAS", "Cass. speichern", root + "/cas");
    generate_button(3.2, 1, "PC",  "Programmzähler", root + "/pc");
    generate_button(3.2, 1, "OUT", "auslesen", root + "/out");
    generate_button(3.2, 1, "INP", "eingeben", root + "/inp");
}

}

#include "seven_segment.h"

#include "save.h"
#include "colors.h"

#include <QFile>
#include <QTextStream>
#include <QString>
#include <QSvgRenderer>
#include <QPainter>
#include <QImage>

#include <iostream>

namespace {

constexpr const int w = 35;
constexpr const int h = 35;

void generate_led(const std::string& basename, const std::string& svgName, const std::string& pngName) {

    // Load SVG
    QFile f(QString(":/svgs/") + svgName.c_str());
    f.open(QFile::ReadOnly | QFile::Text);
    QTextStream in(&f);
    QString content = in.readAll();

    QSvgRenderer renderer(content.toUtf8());
    QImage image(w, h, QImage::Format_RGB32);
    QPainter painter(&image);
    painter.setRenderHint(QPainter::Antialiasing, true);
    painter.fillRect(image.rect(), kosmos_cp1::generator::GREEN);
    renderer.render(&painter);

    std::string filename = basename + "/" + pngName;
    kosmos_cp1::generator::save(image, filename);
}

}

namespace kosmos_cp1::generator {

void generate_leds(const std::string& basename) {

    ::generate_led(basename, "led-on.svg", "led_on.png");
    ::generate_led(basename, "led-off.svg", "led_off.png");
}

}

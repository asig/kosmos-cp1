#include "switches.h"

#include "save.h"

#include <QFile>
#include <QTextStream>
#include <QString>
#include <QSvgRenderer>
#include <QPainter>
#include <QImage>

#include <iostream>

namespace {

constexpr const int svgW = 57;
constexpr const int svgH = 116;

constexpr const int h = 60;

void generate_switch(const std::string& basename, const std::string& svgName, const std::string& pngName) {

    double scale = (double) svgW / (double) svgH;
    int w = (int)(h * scale);

    // Load SVG
    QFile f(QString(":/svgs/") + svgName.c_str());
    f.open(QFile::ReadOnly | QFile::Text);
    QTextStream in(&f);
    QString content = in.readAll();

    // Now, draw segments to image
    QSvgRenderer renderer(content.toUtf8());
    QImage image(w, h, QImage::Format_RGB32);
    QPainter painter(&image);
    painter.setRenderHint(QPainter::Antialiasing, true);
    renderer.render(&painter);

    std::string filename = basename + "/" + pngName;
    kosmos_cp1::generator::save(image, filename);
}

}

namespace kosmos_cp1::generator {

void generate_switches(const std::string& basename) {
    ::generate_switch(basename, "switch-on.svg", "switch_on.png");
    ::generate_switch(basename, "switch-on-pressed.svg", "switch_on_pressed.png");
    ::generate_switch(basename, "switch-off.svg", "switch_off.png");
    ::generate_switch(basename, "switch-off-pressed.svg", "switch_off_pressed.png");
}

}

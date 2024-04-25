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

//    --a--
//   |     |
//   f     b
//   |     |
//    --g--
//   |     |
//   e     c
//   |     |
//    --d--  .dp
//
// a == 0, b == 1, ... dp == 7


namespace {

std::map<int, std::string> segmentCols = {
    {0, "#ff1111"},
    {1, "#11ff11"},
    {2, "#1111ff"},
    {3, "#ff11ff"},
    {4, "#ffff11"},
    {5, "#ffffff"},
    {6, "#881111"},
    {7, "#118811"},
};

constexpr const int svgH = 186;
constexpr const int svgW = 138;

constexpr const int h = 80;

void generate_7segment(const std::string& basename, int mask, bool dot) {

    // Load SVG
    QFile f(":/svgs/7segment.svg");
    f.open(QFile::ReadOnly | QFile::Text);
    QTextStream in(&f);
    QString content = in.readAll();

    // replace all segment cols
    for (int i = 0; i < 8; i++) {
        QColor segCol = (mask && (1 << i)) ? kosmos_cp1::generator::SEGMENT_ON : kosmos_cp1::generator::SEGMENT_OFF;
        if (i == 7 && !dot) {
            segCol = kosmos_cp1::generator::SEGMENT_BG;
        }
        QString colStr = QString::asprintf("#%02x%02x%02x", segCol.red(), segCol.green(), segCol.blue());
        content.replace(segmentCols[i].c_str(), colStr);
    }

    // Now, draw segments to iomage
    int w = h*((double) svgW / (double) svgH);


    QSvgRenderer renderer(content.toUtf8());
    QImage image(w, h, QImage::Format_RGB32);
    QPainter painter(&image);
    painter.setRenderHint(QPainter::Antialiasing, true);
    renderer.render(&painter);

    char buf[100];
    sprintf(buf, "%02x", mask);
    std::string filename = basename + "/" + (dot ? "dot" : "nodot") + "/" + buf + ".png";
    kosmos_cp1::generator::save(image, filename);
}

}

namespace kosmos_cp1::generator {

void generate_7segment(const std::string& basename) {
    ::generate_7segment(basename, 1, false);
    for (int i =0 ; i < 128; i++) {
        ::generate_7segment(basename, i, false);
    }
    for (int i =0 ; i < 256; i++) {
        ::generate_7segment(basename, i, true);
    }
}

}

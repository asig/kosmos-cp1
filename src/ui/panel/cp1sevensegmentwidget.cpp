/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
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

#include "ui/panel/cp1sevensegmentwidget.h"

#include <QPainter>

namespace kosmos_cp1::ui::panel {

namespace {

QImage DIGITS_NODOT[128];
QImage DIGITS_DOT[256];
bool imagesInitialized = false;

void initImages() {
    for (int i = 0; i < 128; i++) {
        DIGITS_NODOT[i].load(QString::asprintf(":/ui/digits/nodot/%02x.png", i));
    }
    for (int i = 0; i < 256; i++) {
        DIGITS_DOT[i].load(QString::asprintf(":/ui/digits/dot/%02x.png", i));
    }
}

}

CP1SevenSegmentWidget::CP1SevenSegmentWidget(bool showDot, QWidget *parent)
    : showDot_{showDot}, QWidget{parent}
{
    // Totally not thread safe, but we don't care.
    if (!imagesInitialized) {
        initImages();
        imagesInitialized = true;
    }
    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFixedSize(DIGITS_DOT[0].size());
}

void CP1SevenSegmentWidget::paintEvent(QPaintEvent *) {
    QImage *imgs = showDot_ ? DIGITS_DOT : DIGITS_NODOT;

    QPainter painter(this);
    painter.drawImage(0,0, imgs[mask_ | (showDot_ ? 128 : 0)]);
}

} // namespace kosmos_cp1::ui::panel

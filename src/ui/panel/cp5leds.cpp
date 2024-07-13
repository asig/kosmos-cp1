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

#include "ui/panel/cp5leds.h"

#include <QPainter>

#include "ui/panel/cp1colors.h"

namespace kosmos_cp1::ui::panel {

CP5Leds::CP5Leds(QWidget *parent)
    : QWidget{parent}
{
    ledOn_.load(":/ui/led_on.png");
    ledOff_.load(":/ui/led_off.png");

    QSize imgSize = ledOn_.size();

    QSize sz(8*imgSize.width() + 7*spacing, imgSize.height());
    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFixedSize(sz);
}

void CP5Leds::paintEvent(QPaintEvent *event) {
    QPainter painter(this);

    QRect r = rect();
    painter.setBackground(CP1Color::GREEN);
    painter.fillRect(r, CP1Color::GREEN);

    for (int i = 0; i < 8; i++) {
        bool on = val_ & (1 << i);
        QImage& img = on ? ledOn_ : ledOff_;
        painter.drawImage( (7-i)*(img.width() + spacing), 0, img);
    }
}

} // namespace kosmos_cp1::ui::panel

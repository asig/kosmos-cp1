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

#include "ui/panel/cp1button.h"

#include <QImage>
#include <QPainter>
#include <QMouseEvent>

namespace kosmos_cp1::ui::panel {

CP1Button::CP1Button(const QString& str, int row, int col, QWidget *parent)
    : QWidget{parent}
{
    row_ = row;
    col_ = col;

    pressed_ = false;

    img_.load(":/ui/buttons/"+ str + ".png");
    imgPressed_.load(":/ui/buttons/"+ str + "_pressed.png");

    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFixedSize(img_.size());
}

void CP1Button::mousePressEvent(QMouseEvent *event) {
    if (!(event->buttons() & Qt::MouseButton::LeftButton)) return;

    pressed_ = true;
    update();
    emit keyPressed(this);
}

void CP1Button::mouseReleaseEvent(QMouseEvent *event) {
    if (pressed_) {
        emit keyReleased(this);
    }
    pressed_ = false;
    update();
}

void CP1Button::mouseMoveEvent(QMouseEvent *event) {
    QPoint pt = event->pos();
    bool newPressed = rect().contains(pt);
    if (newPressed != pressed_) {
        pressed_ = newPressed;
        update();
    }
}

void CP1Button::paintEvent(QPaintEvent *event) {
    QPainter painter(this);
    painter.drawImage(0,0, pressed_ ? imgPressed_ : img_);
}

} // namespace kosmos_cp1::ui::panel

#include "ui/panel/cp5switch.h"

#include <QMouseEvent>
#include <QPainter>

namespace kosmos_cp1::ui::panel {

CP5Switch::CP5Switch(QWidget *parent)
    : QWidget{parent}
{
    switchOn_.load(":/ui/switch_on.png");
    switchOff_.load(":/ui/switch_off.png");
    switchOnPressed_.load(":/ui/switch_on_pressed.png");
    switchOffPressed_.load(":/ui/switch_off_pressed.png");

    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFixedSize(switchOn_.size());
}

void CP5Switch::mousePressEvent(QMouseEvent *event) {
    if (!(event->buttons() & Qt::MouseButton::LeftButton)) return;
    pressed_ = true;
    update();
}

void CP5Switch::mouseReleaseEvent(QMouseEvent *event) {
    if (pressed_) {
        setValue(1-val_);
    }
    pressed_ = false;
    update();
}

void CP5Switch::mouseMoveEvent(QMouseEvent *event) {
    QPoint pt = event->pos();
    bool newPressed = rect().contains(pt);
    if (newPressed != pressed_) {
        pressed_ = newPressed;
        update();
    }
}

void CP5Switch::paintEvent(QPaintEvent *event) {
    QImage *img;
    if (pressed_) {
        img = val_ ? &switchOnPressed_ : &switchOffPressed_;
    } else {
        img = val_ ? &switchOn_ : &switchOff_;
    }
    QPainter painter(this);
    painter.drawImage(0,0, *img);
}


} // namespace kosmos_cp1::ui::panel


///*
// * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
// *
// * This file is part of kosmos-cp1.
// *
// * kosmos-cp1 is free software: you can redistribute it and/or
// * modify it under the terms of the GNU General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * kosmos-cp1 is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
// */

//package com.asigner.cp1.ui.widgets;


//    private static Image ON;
//private static Image OFF;
//private static Image ON_PRESSED;
//private static Image OFF_PRESSED;
//private static int w, h;

//    static {
//        Rectangle r = ON.getBounds();
//        w = r.width;
//        h = r.height;
//    }

//private List<Listener> listeners = Lists.newLinkedList();

//private boolean pressed = false;
//private boolean on = false;

//public CP5Switch(Composite parent, int style) {
//        super(parent, style);

//        this.addPaintListener(this::paint);
//        this.setBackground(CP1Colors.GREEN);

//        setSize(w, h);

//        this.addMouseListener(new MouseListener() {
//            @Override
//                public void mouseDoubleClick(MouseEvent mouseEvent) {
//            }

//            @Override
//                public void mouseDown(MouseEvent mouseEvent) {
//                pressed = true;
//                redraw();
//            }

//            @Override
//                public void mouseUp(MouseEvent mouseEvent) {
//                pressed = false;
//                on = !on;
//                listeners.forEach(Listener::switchFlipped);
//                redraw();
//            }
//        });
//    }

//public void addListener(Listener listener) {
//        this.listeners.add(listener);
//    }

//public boolean isOn() {
//        return on;
//    }

//    @Override
//        public Point computeSize(final int wHint, final int hHint, final boolean changed) { return getSize(); }

//    @Override
//        public void setSize(int width, int height) {
//        super.setSize(width, height);
//    }

//    @Override
//        public void setSize(Point pt) {
//        this.setSize(pt.x, pt.y);
//    }

//private void paint(PaintEvent paintEvent) {
//        Image img;
//        if (on) {
//            img = pressed ? ON_PRESSED : ON;
//        } else {
//            img = pressed ? OFF_PRESSED : OFF;
//        }
//        paintEvent.gc.drawImage(img,  0, 0);
//    }

//    @Override
//        protected void checkSubclass() {
//    }
//}

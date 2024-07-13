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

#include "ui/panel/cp1keyboard.h"

#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QPainter>
#include <QPainterPath>
#include <QPalette>
#include <QKeyEvent>

#include "ui/panel/cp1colors.h"

namespace kosmos_cp1::ui::panel {

namespace {

constexpr const int BUTTON_HEIGHT = 50;
constexpr const int MARGIN_WIDTH = 10;
constexpr const int ARC_WIDTH = 30;

}

CP1Keyboard::CP1Keyboard(QWidget *parent)
    : QWidget{parent}
{
    QHBoxLayout *topLayout = new QHBoxLayout();

    QWidget *left = new QWidget();
    QVBoxLayout *leftLayout = new QVBoxLayout();
    leftLayout->setSpacing(10);

    QHBoxLayout *leftRow0 = new QHBoxLayout();
    leftRow0->addWidget(makeBtn("0", 4, 0));
    leftRow0->addWidget(makeBtn("1", 4, 1));
    leftRow0->addWidget(makeBtn("2", 4, 2));
    leftRow0->addWidget(makeBtn("3", 4, 3));
    leftRow0->addWidget(makeBtn("4", 3, 0));
    leftRow0->addWidget(makeBtn("5", 3, 1));
    leftRow0->addWidget(makeBtn("6", 3, 2));
    leftRow0->addWidget(makeBtn("7", 3, 3));
    leftRow0->addWidget(makeBtn("8", 2, 0));
    leftRow0->addWidget(makeBtn("9", 2, 1));
    leftLayout->addLayout(leftRow0);

    QHBoxLayout *leftRow1 = new QHBoxLayout();
    leftRow1->addStretch();
    leftRow1->addWidget(makeBtn("step", 1, 1, BTN_STEP));
    leftRow1->addWidget(makeBtn("stp", 1, 2, BTN_STP));
    leftRow1->addWidget(makeBtn("run", 1, 3, BTN_RUN));
    leftRow1->addWidget(makeBtn("cal", 1, 0, BTN_CAL));
    leftRow1->addStretch();
    leftLayout->addLayout(leftRow1);

    QHBoxLayout *leftRow2 = new QHBoxLayout();
    leftRow2->addStretch();
    leftRow2->addWidget(makeBtn("clr", 0, 1, BTN_CLR));
    leftRow2->addWidget(makeBtn("acc", 0, 3, BTN_ACC));
    leftRow2->addWidget(makeBtn("cas", 0, 0, BTN_CAS));
    leftRow2->addStretch();
    leftLayout->addLayout(leftRow2);

    QHBoxLayout *leftRow3 = new QHBoxLayout();
    leftRow3->addStretch();
    leftRow3->addWidget(makeBtn("pc", 0, 2, BTN_PC));
    leftRow3->addWidget(makeBtn("out", 2, 2, BTN_OUT));
    leftRow3->addWidget(makeBtn("inp", 2, 3, BTN_INP));
    leftRow3->addStretch();
    leftLayout->addLayout(leftRow3);

    left->setLayout(leftLayout);

    QWidget *right = new QWidget();
    QVBoxLayout *rightLayout = new QVBoxLayout();
    rightLayout->setSpacing(10);

    QHBoxLayout *rightRow0 = new QHBoxLayout();
    rightRow0->addWidget(makeBtn("7w", 3, 3, BTN_7));
    rightRow0->addWidget(makeBtn("8w", 2, 0, BTN_8));
    rightRow0->addWidget(makeBtn("9w", 2, 1, BTN_9));
    rightLayout->addLayout(rightRow0);

    QHBoxLayout *rightRow1 = new QHBoxLayout();
    rightRow1->addWidget(makeBtn("4w", 3, 0, BTN_4));
    rightRow1->addWidget(makeBtn("5w", 3, 1, BTN_5));
    rightRow1->addWidget(makeBtn("6w", 3, 2, BTN_6));
    rightLayout->addLayout(rightRow1);

    QHBoxLayout *rightRow2 = new QHBoxLayout();
    rightRow2->addWidget(makeBtn("1w", 4, 1, BTN_1));
    rightRow2->addWidget(makeBtn("2w", 4, 2, BTN_2));
    rightRow2->addWidget(makeBtn("3w", 4, 3, BTN_3));
    rightLayout->addLayout(rightRow2);

    QHBoxLayout *rightRow3 = new QHBoxLayout();
    rightRow3->addWidget(makeBtn("0w", 4, 0, BTN_0), Qt::AlignLeft);
    rightRow3->addStretch();
    rightLayout->addLayout(rightRow3);
    right->setLayout(rightLayout);

    topLayout->addWidget(left);
    topLayout->addWidget(right);
    topLayout->setContentsMargins(MARGIN_WIDTH,MARGIN_WIDTH,MARGIN_WIDTH,MARGIN_WIDTH);
    topLayout->setSpacing(40);

    setLayout(topLayout);
    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFocusPolicy(Qt::FocusPolicy::StrongFocus);

    for (int i = 0; i < kRows; i++) {
        keyMask_[i] = 0;
    }
}

CP1Button* CP1Keyboard::makeBtn(const QString& str, int row, int col, int btnCode) {
    CP1Button *btn = new CP1Button(str, row, col);
    connect(btn, &CP1Button::keyPressed, this, &CP1Keyboard::onKeyPressed);
    connect(btn, &CP1Button::keyReleased, this, &CP1Keyboard::onKeyReleased);
    if (btnCode != -1) {
        buttons_[btnCode] = btn;
    }
    return btn;
}

void CP1Keyboard::onKeyPressed(CP1Button *btn) {
    keyMask_[btn->row()] |= (1 << btn->col());
}

void CP1Keyboard::onKeyReleased(CP1Button *btn) {
    keyMask_[btn->row()] &= ~(1 << btn->col());
}

void CP1Keyboard::paintEvent(QPaintEvent *) {
    QPainter painter(this);
    QRect r = rect();

    int x = r.x();
    int y = r.y();
    int w = r.width();
    int h = r.height();

    painter.setBrush(CP1Color::PANEL_BACKGROUND);

    // Draw green corners
    painter.fillRect(x, y, w, MARGIN_WIDTH, CP1Color::GREEN);
    painter.fillRect(x, y + w - MARGIN_WIDTH, w, MARGIN_WIDTH, CP1Color::GREEN);
    painter.fillRect(x, y, MARGIN_WIDTH, h, CP1Color::GREEN);
    painter.fillRect(x + w - MARGIN_WIDTH, y , MARGIN_WIDTH, h, CP1Color::GREEN);

    // Draw full border in light color
    painter.setPen(QPen(CP1Color::GREEN_LIGHT, MARGIN_WIDTH));
    painter.drawRoundedRect(x+MARGIN_WIDTH/2, y+MARGIN_WIDTH/2, w-MARGIN_WIDTH, h-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);

    // redraw top left in dark color
    QPainterPath path;
    path.moveTo(x + 2*MARGIN_WIDTH, y + h - 2*MARGIN_WIDTH);
    path.lineTo(x , y + h);
    path.lineTo(x , y);
    path.lineTo(x + w, y);
    path.lineTo(x + w - 2*MARGIN_WIDTH, y + 2*MARGIN_WIDTH);
    path.closeSubpath();

    painter.setClipPath(path);
    painter.setPen(QPen(CP1Color::GREEN_DARK, MARGIN_WIDTH));
    painter.drawRoundedRect(x+MARGIN_WIDTH/2, y+MARGIN_WIDTH/2, w-MARGIN_WIDTH, h-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);
}

void CP1Keyboard::keyPressEvent(QKeyEvent *event) {
    if (handleKey(event, true)) return;
    QWidget::keyPressEvent(event);
}

void CP1Keyboard::keyReleaseEvent(QKeyEvent *event) {
    if (handleKey(event, false)) return;
    QWidget::keyPressEvent(event);
}

bool CP1Keyboard::handleKey(QKeyEvent *e, bool pressed) {
    CP1Button *btn = nullptr;
    int key = e->key();
    switch(key) {
    case Qt::Key_0:
    case Qt::Key_1:
    case Qt::Key_2:
    case Qt::Key_3:
    case Qt::Key_4:
    case Qt::Key_5:
    case Qt::Key_6:
    case Qt::Key_7:
    case Qt::Key_8:
    case Qt::Key_9:
        btn = buttons_[key - Qt::Key_0];
        break;
    case Qt::Key_O:
        btn = buttons_[BTN_OUT];
        break;
    case Qt::Key_Return:
    case Qt::Key_Enter:
        btn = buttons_[BTN_INP];
        break;
    case Qt::Key_L:
        btn = buttons_[BTN_CAL];
        break;
    case Qt::Key_T:
        btn = buttons_[BTN_STEP];
        break;
    case Qt::Key_R:
        btn = buttons_[BTN_RUN];
        break;
    case Qt::Key_S:
        btn = buttons_[BTN_CAS];
        break;
    case Qt::Key_Delete:
    case Qt::Key_Clear:
        btn = buttons_[BTN_CLR];
        break;
    case Qt::Key_P:
        btn = buttons_[BTN_PC];
        break;
    case Qt::Key_A:
        btn = buttons_[BTN_ACC];
        break;
    case Qt::Key_Period:
        btn = buttons_[BTN_STP];
        break;
    }
    if (btn) {
        btn ->setPressed(pressed);
    }
    return btn != nullptr;
}

} // namespace kosmos_cp1::ui::panel

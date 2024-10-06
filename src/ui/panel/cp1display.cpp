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

#include "ui/panel/cp1display.h"

#include <unordered_map>

#include <QPainter>
#include <QHBoxLayout>

#include "ui/panel/cp1colors.h"

namespace kosmos_cp1::ui::panel {

namespace {

std::unordered_map<char, uint8_t> charMap = {
{'0', 1 << 5 | 1 << 4 | 1 << 3 | 1 << 2 | 1 << 1 | 1 << 0},
{'1', 1 << 2 | 1 << 1},
{'2', 1 << 6 | 1 << 4 | 1 << 3 | 1 << 1 | 1 << 0},
{'3', 1 << 6 | 1 << 3 | 1 << 2 | 1 << 1 | 1 << 0},
{'4', 1 << 6 | 1 << 5 | 1 << 2 | 1 << 1},
{'5', 1 << 6 | 1 << 5 | 1 << 3 | 1 << 2 | 1 << 0},
{'6', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 3 | 1 << 2 | 1 << 0},
{'7', 1 << 5 | 1 << 2 | 1 << 1 | 1 << 0},
{'8', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 3 | 1 << 2 | 1 << 1 | 1 << 0},
{'9', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 2 | 1 << 1 | 1 << 0},
{'A', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 2 | 1 << 1 | 1 << 0},
{'E', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 3 | 1 << 0},
{'P', 1 << 6 | 1 << 5 | 1 << 4 | 1 << 1 | 1 << 0},
{'C', 1 << 5 | 1 << 4 | 1 << 3 | 1 << 0},
{'u', 1 << 4 | 1 << 3 | 1 << 2 },
{'n', 1 << 5 | 1 << 1 | 1 << 0},
{' ', 0},
};

constexpr const int MARGIN_WIDTH = 10;
constexpr const double SPACER_WIDTH = 8; // Spacer width, roughly 15% of the digit's width.

}

CP1Display::CP1Display(Intel8155 *pid, QWidget *parent)
    : pid_{pid}, QWidget{parent}
{

    digits_[0] = new CP1SevenSegmentWidget(false, this);

    digits_[1] = new CP1SevenSegmentWidget(false, this);
    digits_[2] = new CP1SevenSegmentWidget(true, this);

    digits_[3] = new CP1SevenSegmentWidget(false, this);
    digits_[4] = new CP1SevenSegmentWidget(false, this);
    digits_[5] = new CP1SevenSegmentWidget(false, this);

    QHBoxLayout *layout = new QHBoxLayout();
    layout->setContentsMargins(MARGIN_WIDTH,MARGIN_WIDTH,MARGIN_WIDTH,MARGIN_WIDTH);
    layout->setSpacing(0);

    layout->addWidget(digits_[0]);
    layout->addSpacing(SPACER_WIDTH);
    layout->addWidget(digits_[1]);
    layout->addWidget(digits_[2]);
    layout->addSpacing(SPACER_WIDTH);
    layout->addWidget(digits_[3]);
    layout->addWidget(digits_[4]);
    layout->addWidget(digits_[5]);
    setLayout(layout);

    QSize sz = digits_[0]->size();
    sz = QSize( 6 * sz.width() + 2 * SPACER_WIDTH + 2 * MARGIN_WIDTH, sz.height() + 2 * MARGIN_WIDTH);

    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFixedSize(sz);

    // port writes need to be handled immediately, as they choose which display digit to update
    connect(pid_, &Intel8155::portWritten, this, &CP1Display::onPidPortWritten, Qt::DirectConnection);

    connect(this, &CP1Display::segmentsChanged, this, &CP1Display::setSegments);
}

void CP1Display::onPidPortWritten(Port port, uint8_t value) {
    if (port == Port::C) {
        for (uint8_t i = 0; i < 8; i++) {
            if ((value & (1 << i)) == 0) {
                activeDigit_ = i;
                break;
            }
        }
    } else if (port == Port::A && lastPortWritten_ == Port::C) {
        // Ignore writes to A unless they happen directly after a write to C.
        // For some reason that I don't fully understand yet, starting at 0x026f
        // in the ROM port A is cleared, then the line is selected by writing to
        // Port C, and only then the new value is written, so a digit is empty at
        // 5/6th of the time...
        if (activeDigit_ > 5) {
            qDebug() << "activeDigit_ == " << activeDigit_ << " is out of range!";
            std::abort();
        }
        emit segmentsChanged(5 - activeDigit_, value);
    }
    lastPortWritten_ = port;
}

void CP1Display::setSegments(int digit, uint8_t value) {
    digits_[digit]->setSegments(value);
    update();
}

void CP1Display::display(const std::string& str) {
    std::string s = "      " + str;
    int start = s.length() - 6;
    for (int i = 0; i < 6; i++) {
        digits_[i]->setSegments(charMap[s[start+i]]);
    }
}

void CP1Display::paintEvent(QPaintEvent *) {
    QPainter painter(this);
    QRect r = rect();

    // Draw green corners
    painter.setBackground(CP1Color::GREEN);
    painter.fillRect(r.x(), r.y(), r.width(), MARGIN_WIDTH, CP1Color::GREEN);
    painter.fillRect(r.x(), r.y() + r.height() - MARGIN_WIDTH, r.width(), MARGIN_WIDTH, CP1Color::GREEN);
    painter.fillRect(r.x(), r.y(), MARGIN_WIDTH, r.height(), CP1Color::GREEN);
    painter.fillRect(r.x() + r.width() - MARGIN_WIDTH, r.y() , MARGIN_WIDTH, r.height(), CP1Color::GREEN);

    painter.setBrush(CP1Color::BLACK);
    painter.drawRoundedRect(r, 10,10,Qt::AbsoluteSize);
}

} // namespace kosmos_cp1::ui::panel


#include "cp1display.h"

#include <unordered_map>

#include <QPainter>
#include <QHBoxLayout>

#include "ui/cp1colors.h"

namespace kosmos_cp1 {
namespace ui {

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

    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    setFixedSize(sizeHint());

    connect(pid_, &Intel8155::portWritten, this, &CP1Display::onPidPortWritten);
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
        CP1SevenSegmentWidget *digit = digits_[5 - activeDigit_];
        digit->setSegments(value);
        update();
    }
    lastPortWritten_ = port;
}

QSize CP1Display::sizeHint() const {
    QSize sz = digits_[0]->sizeHint();
    sz = QSize( 6 * sz.width() + 2 * SPACER_WIDTH + 2 * MARGIN_WIDTH, sz.height() + 2 * MARGIN_WIDTH);
    return sz;
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


} // namespace ui
} // namespace kosmos_cp1


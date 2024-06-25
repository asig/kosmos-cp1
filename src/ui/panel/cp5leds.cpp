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

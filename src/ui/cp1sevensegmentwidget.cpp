#include "cp1sevensegmentwidget.h"

#include <QPainter>

namespace kosmos_cp1 {
namespace ui {

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
    setFixedSize(sizeHint());
}

QSize CP1SevenSegmentWidget::sizeHint() const {
    return DIGITS_DOT[0].size();
}

void CP1SevenSegmentWidget::paintEvent(QPaintEvent *) {
    QImage *imgs = showDot_ ? DIGITS_DOT : DIGITS_NODOT;

    QPainter painter(this);
    painter.drawImage(0,0, imgs[mask_ | (showDot_ ? 128 : 0)]);
}

} // namespace ui
} // namespace kosmos_cp1

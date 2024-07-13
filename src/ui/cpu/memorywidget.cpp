#include "ui/cpu/memorywidget.h"

#include <QPainter>
#include <QPaintEvent>

#include "ui/resources.h"

namespace kosmos_cp1::ui::cpu {

namespace {

constexpr const int kBytesPerLine = 16;

QColor kBg = QColor(Qt::white);
QColor kBgSelected = QColor(Qt::red);
QColor kBgDisabled = QColor(Qt::lightGray).lighter(120);


QColor kFg = QColor(Qt::black);
QColor kFgSelected = QColor(Qt::yellow);
QColor kFgDisabled = QColor(Qt::lightGray).lighter(80);

}

MemoryWidget::MemoryWidget(const Ram* ram, QWidget *parent)
    : QWidget{parent}
{  
    ram_ = ram;
    lastWritten_ = -1;

    setFont(Resources::dejaVuSansFont());

    // Compute the size of the widget:

    QFontMetrics fm{Resources::dejaVuSansFont()};
    lineH_ = fm.height();
    ascent_ = fm.ascent();
    charW_ = fm.averageCharWidth();

    int w = charW_ * (6 + kBytesPerLine * 3 - 1); // 4 bytes address, colon, space, bytes
    int sz = ram->size();
    lines_ = (sz + kBytesPerLine - 1)/kBytesPerLine;

    setFixedSize(QSize(w, lines_*lineH_));

    connect(ram, &Ram::memoryWritten, [this](std::uint16_t addr, std::uint8_t val) {
        lastWritten_ = addr;
    });
}

void MemoryWidget::paintEvent(QPaintEvent* event) {
    QPainter painter(this);    
    painter.setFont(Resources::dejaVuSansFont());
    painter.setBackgroundMode(Qt::OpaqueMode);

    painter.setPen(isEnabled() ? kFg : kFgDisabled);
    painter.setBackground(isEnabled() ? kBg : kBgDisabled);

    painter.fillRect(event->rect(), painter.background());
    for (int i = 0; i < lines_; i++) {
        paintLine(painter, i);
    }
}

void MemoryWidget::paintLine(QPainter& painter, int lineIdx) {
    bool enabled = isEnabled();
    int y =  lineIdx * lineH_;

    // address
    QString str = QString::asprintf("%04X:", lineIdx * kBytesPerLine);
    painter.drawText(0, y + ascent_, str);

    for (int i = 0; i < kBytesPerLine; i++) {
        int pos = lineIdx * kBytesPerLine + i;
        bool resetPen = false;
        if (enabled && pos == lastWritten_) {
            resetPen = true;
            painter.setPen(kFgSelected);
            painter.setBackground(kBgSelected);
        }
        QString str = pos < ram_->size() ? QString::asprintf("%02X", ram_->read(pos)) : "  ";
        painter.drawText((6+i*3) * charW_, y + ascent_, str);
        if (resetPen) {
            painter.setPen(kFg);
            painter.setBackground(kBg);
        }
    }
}

} // namespace kosmos_cp1::ui::cpu

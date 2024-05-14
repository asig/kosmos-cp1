#include "cp1button.h"

#include <QImage>
#include <QPainter>
#include <QMouseEvent>

namespace kosmos_cp1 {
namespace ui {

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

} // namespace ui
} // namespace kosmos_cp1

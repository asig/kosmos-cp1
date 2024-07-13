#include "ui/panel/cp5switch.h"

#include <QMouseEvent>
#include <QPainter>

namespace kosmos_cp1::ui::panel {

CP5Switch::CP5Switch(QWidget *parent)
    : val_(0), pressed_(false), QWidget{parent}
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

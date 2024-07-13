#pragma once

#include <cstdint>

#include <QImage>
#include <QWidget>

namespace {

//constexpr const int spacing = 5;

}

namespace kosmos_cp1::ui::panel {

using std::uint8_t;

class CP5Switch : public QWidget
{
    Q_OBJECT
public:
    explicit CP5Switch(QWidget *parent = nullptr);

    void setValue(uint8_t value) {
        if (value != val_) {
            val_ = value;
            update();
            emit valueChanged(val_);
        }
    }

    std::uint8_t value() const {
        return val_;
    }

signals:
    void valueChanged(uint8_t newValue);

protected:
    void mousePressEvent(QMouseEvent *event) override;
    void mouseMoveEvent(QMouseEvent *event) override;
    void mouseReleaseEvent(QMouseEvent *event) override;
    void paintEvent(QPaintEvent *event) override;

private:
    QImage switchOn_;
    QImage switchOff_;
    QImage switchOnPressed_;
    QImage switchOffPressed_;

    bool pressed_;

    uint8_t val_;


};

} // namespace kosmos_cp1::ui::panel

#pragma once

#include <cstdint>

#include <QImage>
#include <QWidget>

namespace {

constexpr const int spacing = 5;

}

namespace kosmos_cp1::ui::panel {

using std::uint8_t;

class CP5Leds : public QWidget
{
    Q_OBJECT
public:
    explicit CP5Leds(QWidget *parent = nullptr);

    void setValue(uint8_t value) {
        if (value != val_) {
            val_ = value;
            update();
        }
    }
signals:

protected:
    void paintEvent(QPaintEvent *event) override;

private:
    QImage ledOn_;
    QImage ledOff_;

    uint8_t val_;


};

} // namespace kosmos_cp1::ui::panel

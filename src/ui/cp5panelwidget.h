#pragma once

#include <cstdint>

#include <QWidget>

#include "ui/cp5leds.h"
#include "ui/cp5switches.h"


namespace kosmos_cp1 {
namespace ui {

using std::uint8_t;

class CP5PanelWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP5PanelWidget(QWidget *parent = nullptr);

    void writeLeds(uint8_t leds);
    uint8_t readSwitches();

signals:
    void switchesChanged(uint8_t val);

private:
    CP5Leds *leds_;
    CP5Switches *switches_;

};

} // namespace ui
} // namespace kosmos_cp1

#pragma once

#include <cstdint>

#include <QWidget>

#include "ui/panel/cp5leds.h"
#include "ui/panel/cp5switches.h"


namespace kosmos_cp1::ui::panel {

using std::uint8_t;

class CP5PanelWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP5PanelWidget(QWidget *parent = nullptr);

    void writeLeds(uint8_t leds);
    uint8_t readSwitches() const;

signals:
    void switchesChanged(uint8_t val);

private:
    CP5Leds *leds_;
    CP5Switches *switches_;

};

} // namespace kosmos_cp1::ui::panel

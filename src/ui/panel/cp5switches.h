#pragma once

#include <QWidget>

#include "ui/panel/cp5switch.h"

namespace kosmos_cp1::ui::panel {

class CP5Switches : public QWidget
{
    Q_OBJECT
public:
    explicit CP5Switches(QWidget *parent = nullptr);

    std::uint8_t value();

signals:

private:
    CP5Switch *switches_[8];

};

} // namespace kosmos_cp1::ui::panel


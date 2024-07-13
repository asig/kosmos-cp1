#include "ui/panel/cp5switches.h"

#include <QHBoxLayout>

#include "ui/panel/cp1colors.h"

namespace kosmos_cp1::ui::panel {

CP5Switches::CP5Switches(QWidget *parent)
    : QWidget{parent}
{
    QHBoxLayout *layout = new QHBoxLayout();
    layout->setContentsMargins(8,5,8,5);
    layout->setSpacing(0);
    for (int i = 0; i < 8; i++) {
        switches_[i] = new CP5Switch();
        layout->addWidget(switches_[i]);
    }
    setLayout(layout);

    QPalette pal = QPalette();
    pal.setColor(QPalette::Window, CP1Color::SWITCH_BG);
    setAutoFillBackground(true);
    setPalette(pal);
}

std::uint8_t CP5Switches::value() {
    std::uint8_t value = 0;
    for (int i = 0; i < 8; i++) {
        if (switches_[7-i]->value()) {
            value |= 1 << i;
        }
    }
    return value;
}

} // namespace kosmos_cp1::ui::panel

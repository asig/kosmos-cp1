#include "ui/panel/cp5panelwidget.h"

#include <QHBoxLayout>

namespace kosmos_cp1::ui::panel {

CP5PanelWidget::CP5PanelWidget(QWidget *parent)
    : QWidget{parent}
{
    leds_ = new CP5Leds();
    switches_ = new CP5Switches();

    QHBoxLayout *layout = new QHBoxLayout();
    layout->addStretch();
    layout->addWidget(leds_);
    layout->addSpacing(50);
    layout->addWidget(switches_, 0, Qt::AlignCenter);
    setLayout(layout);
}

void CP5PanelWidget::writeLeds(uint8_t leds) {
    leds_->setValue(leds);
}

uint8_t CP5PanelWidget::readSwitches() const {
    return switches_->value();
}

} // namespace kosmos_cp1::ui::panel


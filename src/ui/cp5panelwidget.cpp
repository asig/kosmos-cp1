#include "cp5panelwidget.h"

namespace kosmos_cp1 {
namespace ui {

CP5PanelWidget::CP5PanelWidget(QWidget *parent)
    : QWidget{parent}
{

}

void CP5PanelWidget::writeLeds(uint8_t leds) {
}

uint8_t CP5PanelWidget::readSwitches() {
    return 0;
}

} // namespace ui
} // namespace kosmos_cp1

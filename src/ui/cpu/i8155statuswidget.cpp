#include "ui/cpu/i8155statuswidget.h"

namespace kosmos_cp1::ui::cpu {

I8155StatusWidget::I8155StatusWidget(const QString& title, const Ram* ram,  QWidget *parent)
    : QGroupBox(title, parent)
{

}

void I8155StatusWidget::updateState() {

}

} // namespace kosmos_cp1::ui::cpu

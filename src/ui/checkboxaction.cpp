#include "ui/checkboxaction.h"

#include <QCheckBox>

namespace kosmos_cp1::ui {

CheckboxAction::CheckboxAction(const QString& text, bool checked, QWidget *parent)
    : text_(text), QWidgetAction(parent)
{
    setCheckable(true);
    setChecked(checked);
}

QWidget *CheckboxAction::createWidget(QWidget *parent) {
    QCheckBox *cb = new QCheckBox(text_, parent);
    cb->setCheckState(isChecked() ? Qt::CheckState::Checked : Qt::CheckState::Unchecked);
    connect(cb, &QCheckBox::stateChanged, this, &CheckboxAction::onStateChanged);
    return cb;
}

void CheckboxAction::deleteWidget(QWidget *widget) {
    QCheckBox *cb = static_cast<QCheckBox*>(widget);
    disconnect(cb, &QCheckBox::stateChanged, this, &CheckboxAction::onStateChanged);
    QWidgetAction::deleteWidget(widget);
}

void CheckboxAction::onStateChanged(int state) {
    setChecked(state == Qt::CheckState::Checked);
}

} // namespace kosmos_cp1::ui

/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "ui/cpu/checkboxaction.h"

#include <QCheckBox>

namespace kosmos_cp1::ui::cpu {

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

} // namespace kosmos_cp1::ui::cpu

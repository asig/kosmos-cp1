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


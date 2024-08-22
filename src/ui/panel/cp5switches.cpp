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

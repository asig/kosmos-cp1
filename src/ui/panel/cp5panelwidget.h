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

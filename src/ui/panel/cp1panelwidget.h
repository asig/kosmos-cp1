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

#include <QWidget>

#include "ui/panel/cp1display.h"
#include "ui/panel/cp1keyboard.h"

namespace kosmos_cp1::ui::panel {

class CP1PanelWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP1PanelWidget(Intel8155 *pid, QWidget *parent = nullptr);

    CP1Display *cp1Display() {
        return display_;
    }

    CP1Keyboard *cp1Keyboard() {
        return keyboard_;
    }

private:
    CP1Display *display_;
    CP1Keyboard *keyboard_;
};

} // namespace kosmos_cp1::ui::panel

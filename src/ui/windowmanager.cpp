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

#include "ui/windowmanager.h"

#include "ui/basewindow.h"

namespace kosmos_cp1 {
namespace ui {

WindowManager::WindowManager(QObject *parent) : QObject{parent}
{
}

WindowManager::~WindowManager() {
}

void WindowManager::addWindow(BaseWindow* w) {
    windows_.push_back(w);
}

void WindowManager::closeAllWindows() {
    for (auto *w : windows_) {
        w->close();
    }
}

const std::vector<BaseWindow*>& WindowManager::windows() const {
    return windows_;
}


} // namespace ui
} // namespace kosmos_cp1

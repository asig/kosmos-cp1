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

#include "emulation/ram.h"

namespace kosmos_cp1::ui::cpu {

using kosmos_cp1::emulation::Ram;

class MemoryWidget : public QWidget
{
    Q_OBJECT
public:
    explicit MemoryWidget(const Ram *ram, QWidget *parent = nullptr);

signals:

protected:
    void paintEvent(QPaintEvent* event) override;

private:
    const Ram *ram_;

    int lineH_;
    int ascent_;
    int charW_;
    int lines_;

    int lastWritten_;

    void paintLine(QPainter& painter, int lineIdx);
};

} // namespace kosmos_cp1::ui::cpu

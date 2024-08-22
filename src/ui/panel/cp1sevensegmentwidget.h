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

namespace kosmos_cp1::ui::panel {

using std::uint8_t;

//    --a--
//   |     |
//   f     b
//   |     |
//    --g--
//   |     |
//   e     c
//   |     |
//    --d--  .dp
//
// a == 0, b == 1, ... dp == 7

class CP1SevenSegmentWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP1SevenSegmentWidget(bool showDot = false, QWidget *parent = nullptr);

    void setShowDot(bool showDot) {
        if (showDot == showDot_) return;
        showDot_ = showDot;
        update();
    }

    void setSegments(uint8_t mask) {
        if (mask == mask_) return;
        mask_ = mask;
        update();
    }

signals:

protected:
    void paintEvent(QPaintEvent *event) override;

private:
    bool showDot_;
    uint8_t mask_;

    int w_;
    int h_;
};

} // namespace kosmos_cp1::ui::panel

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

#include <QImage>
#include <QWidget>

namespace {

//constexpr const int spacing = 5;

}

namespace kosmos_cp1::ui::panel {

using std::uint8_t;

class CP5Switch : public QWidget
{
    Q_OBJECT
public:
    explicit CP5Switch(QWidget *parent = nullptr);

    void setValue(uint8_t value) {
        if (value != val_) {
            val_ = value;
            update();
            emit valueChanged(val_);
        }
    }

    std::uint8_t value() const {
        return val_;
    }

signals:
    void valueChanged(uint8_t newValue);

protected:
    void mousePressEvent(QMouseEvent *event) override;
    void mouseMoveEvent(QMouseEvent *event) override;
    void mouseReleaseEvent(QMouseEvent *event) override;
    void paintEvent(QPaintEvent *event) override;

private:
    QImage switchOn_;
    QImage switchOff_;
    QImage switchOnPressed_;
    QImage switchOffPressed_;

    bool pressed_;

    uint8_t val_;


};

} // namespace kosmos_cp1::ui::panel

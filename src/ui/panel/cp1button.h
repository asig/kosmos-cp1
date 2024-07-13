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
#include <QString>

namespace kosmos_cp1::ui::panel {

class CP1Button : public QWidget
{
    Q_OBJECT
public:
    explicit CP1Button(const QString& str, int row, int col, QWidget *parent = nullptr);

    bool pressed() const {
        return pressed_;
    }

    int row() const {
        return row_;
    }

    int col() const {
        return col_;
    }

    void setPressed(bool pressed) {
        if (pressed == pressed_) return;
        pressed_ = pressed;
        update();
        if (pressed_) {
            emit keyPressed(this);
        } else {
            emit keyReleased(this);
        }
    }

signals:
    void keyPressed(CP1Button *btn);
    void keyReleased(CP1Button *btn);

protected:
    void mousePressEvent(QMouseEvent *event) override;
    void mouseMoveEvent(QMouseEvent *event) override;
    void mouseReleaseEvent(QMouseEvent *event) override;
    void paintEvent(QPaintEvent *event) override;

private:
    int row_;
    int col_;

    QImage img_;
    QImage imgPressed_;

    bool mouseOverControl_;
    bool pressed_;
};

} // namespace kosmos_cp1::ui::panel

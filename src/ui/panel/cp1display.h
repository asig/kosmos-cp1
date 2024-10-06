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
#include <string>

#include <QWidget>

#include "ui/panel/cp1sevensegmentwidget.h"
#include "emulation/intel8155.h"

namespace kosmos_cp1::ui::panel {

using std::uint8_t;

using ::kosmos_cp1::emulation::Intel8155;
using ::kosmos_cp1::emulation::Port;

class CP1Display : public QWidget
{
    Q_OBJECT
public:
    explicit CP1Display(Intel8155 *pid, QWidget *parent = nullptr);

    void display(const std::string& str);

public slots:
    void onPidPortWritten(Port port, uint8_t val);

    // Slot so that we can call this from a different thread safely.
    void setSegments(int digit, uint8_t value);

signals:
    // Used internally so that we can get a direct callback in onPidPortWritten
    // but then can cross thread boundaries to actually update the UI.
    void segmentsChanged(int digit, uint8_t value);

protected:
    void paintEvent(QPaintEvent *event) override;

private:
    Intel8155 *pid_;
    CP1SevenSegmentWidget *digits_[6];

    uint8_t activeDigit_; // set by writes to Intel 8155's port C
    Port lastPortWritten_;


};

} // namespace kosmos_cp1::ui::panel

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

#include <QGroupBox>
#include <QLabel>
#include <QCheckBox>

#include "emulation/intel8155.h"
#include "ui/cpu/memorywidget.h"


namespace kosmos_cp1::ui::cpu {

using ::kosmos_cp1::emulation::Intel8155;


class I8155StatusWidget : public QGroupBox
{
    Q_OBJECT
public:
    explicit I8155StatusWidget(const QString& title, Intel8155 *pid, QWidget *parent = nullptr);

    void updateState();

signals:

private:
    Intel8155 *pid_;

    QLabel *portAMode_;
    QCheckBox *portAInterruptEnabled_;
    QLabel *portAVal_;

    QLabel *portBMode_;
    QCheckBox *portBInterruptEnabled_;
    QLabel *portBVal_;

    QLabel *portCMode_;
    QLabel *portCVal_;

    QCheckBox *ceLA_;
    QCheckBox *io_;
    QCheckBox *ale_;
    QCheckBox *rdLA_;
    QCheckBox *wrLA_;

    MemoryWidget *memory_;
};

} // namespace kosmos_cp1::ui::cpu


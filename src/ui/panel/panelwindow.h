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

#include <QMainWindow>
#include <QWindow>

#include "executorthread.h"
#include "emulation/intel8049.h"
#include "emulation/intel8155.h"
#include "emulation/dataport.h"
#include "ui/basewindow.h"
#include "ui/windowmanager.h"
#include "ui/panel/cp1panelwidget.h"
#include "ui/panel/cp5panelwidget.h"

namespace kosmos_cp1::ui::panel {

using kosmos_cp1::ui::BaseWindow;
using kosmos_cp1::ui::WindowManager;

using ::kosmos_cp1::ExecutorThread;

using ::kosmos_cp1::emulation::Intel8155;
using ::kosmos_cp1::emulation::Intel8049;
using ::kosmos_cp1::emulation::DataPort;

class PanelWindow : public BaseWindow
{
    Q_OBJECT
public:
    explicit PanelWindow(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent = nullptr);

    void createWindow() override;
    QString windowName() override;

signals:
    void requestTitleChange(const QString& newState);

public slots:
    void onPinProgWritten(uint8_t val);
    void onPort1ValueWritten(uint8_t newVal);
    void on8155PortWritten(Port port, uint8_t val);
    void onSwitchesChanged(uint8_t val);

private:
    Intel8049 *cpu_;
    Intel8155 *pid_;
    Intel8155 *pidExtension_;

    uint8_t pinProgValue_;
    bool inPort1ValueWritten_;

    ExecutorThread *executorThread_;

    CP1PanelWidget *cp1Panel_;
    CP5PanelWidget *cp5Panel_;

    QAction* loadStateAction_;
    QAction* saveStateAction_;

    void createActions();
    void createMainUI();

    void updateWindowTitle(const QString& state);
};

}

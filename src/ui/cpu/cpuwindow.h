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

#include "executorthread.h"
#include "emulation/intel8049.h"
#include "emulation/intel8155.h"
#include "emulation/dataport.h"
#include "ui/basewindow.h"
#include "ui/windowmanager.h"
#include "ui/cpu/i8049disassemblywidget.h"
#include "ui/cpu/i8049statuswidget.h"
#include "ui/cpu/i8155statuswidget.h"

namespace kosmos_cp1::ui::cpu {

using kosmos_cp1::ui::BaseWindow;
using kosmos_cp1::ui::WindowManager;

using ::kosmos_cp1::ExecutorThread;

using ::kosmos_cp1::emulation::Intel8155;
using ::kosmos_cp1::emulation::Intel8049;
using ::kosmos_cp1::emulation::DataPort;

class CpuWindow : public BaseWindow
{
    Q_OBJECT
public:
    explicit CpuWindow(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent = nullptr);

    void createWindow() override;
    QString windowName() override;

public slots:
    void onCpuStateChanged();
    void onInstructionExecuted();
    void onResetExecuted();
    void onExecutionStarted();
    void onExecutionStopped();
    void onBreakpointHit();

protected:
     void closeEvent(QCloseEvent *event) override;
     void showEvent(QShowEvent *event) override;

private:
    Intel8049 *cpu_;
    Intel8155 *pid_;
    Intel8155 *pidExtension_;

    ExecutorThread *executorThread_;

    QAction *runAction_;
    QAction *stopAction_;
    QAction *singleStepAction_;
    QAction *resetAction_;
    QAction *breakOnMovXAction_;
    QAction *throttleExecutionAction_;

    I8049DisassemblyWidget *disassembly_;
    I8155StatusWidget *status8155_;
    I8155StatusWidget *status8155Extension_;
    I8049StatusWidget *status8049_;

    void createActions();
    void createToolBar();
    void createMainUI();

    void update8155();
    void updateView();
    void update8155States();

    void enableChildren(bool enabled);
};

} // namespace kosmos_cp1::ui::cpu


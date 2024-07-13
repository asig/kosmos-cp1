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
#include <QComboBox>
#include <QPushButton>
#include <QTextEdit>

#include "executorthread.h"
#include "emulation/intel8155.h"
#include "ui/basewindow.h"
#include "ui/windowmanager.h"

namespace kosmos_cp1::ui::assembler {

using kosmos_cp1::ui::BaseWindow;
using kosmos_cp1::ui::WindowManager;

using ::kosmos_cp1::ExecutorThread;

using ::kosmos_cp1::emulation::Intel8155;

class AssemblerWindow : public BaseWindow
{
    Q_OBJECT
public:
    explicit AssemblerWindow(Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent = nullptr);

    void createWindow() override;
    QString windowName() override;

signals:

public slots:    

private slots:
    void onAssembleClicked();

private:

    struct SampleCode {
        QString name;
        QString code;
    };

    Intel8155 *pid_;
    Intel8155 *pidExtension_;
    ExecutorThread *executorThread_;

    QComboBox *sampleCombo_;
    QTextEdit *sourceCode_;
    QTextEdit *results_;
    QPushButton *assembleBtn_;

    std::vector<SampleCode> samples_;

    void createMainUI();

    static SampleCode loadSample(const QString& name);

};

} // namespace kosmos_cp1::ui::assembler


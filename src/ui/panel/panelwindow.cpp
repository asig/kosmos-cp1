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

#include "ui/panel/panelwindow.h"

#include "fmt/format.h"

#include <QVBoxLayout>
#include <QPushButton>

#include "ui/panel/cp1colors.h"

namespace kosmos_cp1::ui::panel {

namespace {
constexpr const char *WINDOW_TITLE = "Kosmos CP1";
}

using ::kosmos_cp1::emulation::Port;

PanelWindow::PanelWindow(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent)
    : cpu_(cpu), pid_(pid), pidExtension_(pidExtension), executorThread_(executorThread), BaseWindow{windowManager, parent}
{
}

void PanelWindow::createWindow() {
    createActions();
    createMenuBar();
    createMainUI();

    pinProgValue_ = 0;
    inPort1ValueWritten_ = false;

    // Hook up the 8049's PROG to the Panel. CP1's ROM uses MOVD A, P4 to
    // read the keyboard state from the lower nibble of P2. MOVD will
    // pull PROG to 0 when the address is valid on the lower 4 pins of port 2,
    // so we connect that pin to the panel to allow it to pick this up and then
    // send its data to the port.
    connect(cpu_, &Intel8049::pinPROGWritten, this, &PanelWindow::onPinProgWritten, Qt::DirectConnection);

    // Allow window title changes in the window's UI thread
    connect(this, &PanelWindow::requestTitleChange, this, &PanelWindow::updateWindowTitle);

    // Looks like Lambdas are always executed in the emitter's thread, therefore we just emit another signal
    // to will be handled in the window's UI thread.
    connect(executorThread_, &ExecutorThread::executionStarted, [this] { emit requestTitleChange("running"); } );
    connect(executorThread_, &ExecutorThread::executionStopped, [this] { emit requestTitleChange("stopped"); } );
    connect(executorThread_, &ExecutorThread::breakpointHit, [this] { emit requestTitleChange("stopped"); } );
    connect(executorThread_, &ExecutorThread::performanceUpdate, [this](double performance) {
        emit requestTitleChange(QString::fromStdString(fmt::format("{:d}%", (int)(performance*100+.5))));
    });

    connect(cpu_->port(1).get(), &DataPort::valueWritten, this, &PanelWindow::onPort1ValueWritten, Qt::DirectConnection);

    connect(pid_, &Intel8155::portWritten, [this](Port port, uint8_t val) {
        if (port != Port::B) return;
        cp5Panel_->writeLeds(val);
    });
    connect(cp5Panel_, &CP5PanelWidget::switchesChanged, [this](uint8_t val) {
        cpu_->port(1)->write(val);
    });

    updateWindowTitle("stopped");
}

QString PanelWindow::windowName() {
    return "Panel";
}

void PanelWindow::createMainUI() {

    QWidget *mainWidget = new QWidget();
    QPalette pal = QPalette();
    pal.setColor(QPalette::Window, CP1Color::GREEN);

    mainWidget->setAutoFillBackground(true);
    mainWidget->setPalette(pal);

    cp1Panel_ = new CP1PanelWidget(pid_);

    cp5Panel_ = new CP5PanelWidget();
    cp5Panel_->writeLeds(pid_->pbValue());
    cpu_->port(1)->write(cp5Panel_->readSwitches());

    QVBoxLayout *layout = new QVBoxLayout();
    layout->addWidget(cp5Panel_);
    layout->addSpacing(50);
    layout->addWidget(cp1Panel_);
    mainWidget->setLayout(layout);

    setCentralWidget(mainWidget);

}

void PanelWindow::updateWindowTitle(const QString& state) {
    QString title = QString(WINDOW_TITLE) + " (" + state + ")";
    setWindowTitle(title);
}

void PanelWindow::onPinProgWritten(uint8_t val) {
    if (pinProgValue_ == val) {
        return;
    }
    uint8_t oldValue = pinProgValue_;
    pinProgValue_ = val;
    if (!(oldValue == 1 && val == 0)) {
        return;
    }

    // Falling flank indicates that the address is valid on P2. Next, the CPU will read or write data.
    // We know that the writes to the port don't happen in the CPU, so we just write the keyboard state
    // to the port.

    uint8_t row = 0;
    uint8_t mask = pid_->pcValue();
    for (uint8_t i = 0; i < 8; i++) {
        if ((mask & (1 << i)) == 0) {
            row = i;
            break;
        }
    }
    uint8_t keyMask = cp1Panel_->cp1Keyboard()->keyMask(row);
    cpu_->port(2)->write(keyMask, 0x0f); // only the lower 4 bits of the port are connected to the key matrix. DO NOT TOUCH the upper nibble, as this is connected to the 8155s.
}

void PanelWindow::onPort1ValueWritten(uint8_t newVal) {
    if (inPort1ValueWritten_) {
        // Called by our own write. Bail out.
        return;
    }

    // CPU wrote to the port to prepare the pins for input. Write switch settings
    inPort1ValueWritten_ = true;
    cpu_->port(1)->write(cp5Panel_->readSwitches());
    inPort1ValueWritten_ = false;
}

void PanelWindow::createActions() {
//    QAction *a = new QAction(tr("Load state"));
//    a->setShortcut(QKeyCombination(Qt::CTRL | Qt::Key_L));
//    a->setToolTip(a->text() + " (" + tr("Ctrl+L") + ")");
//    loadStateAction_ = a;
//    connect(loadStateAction_, &QAction::triggered, this, &PanelWindow::onLoadStateClicked);

//    a = new QAction(tr("Save state"));
//    a->setShortcut(QKeyCombination(Qt::CTRL | Qt::Key_S));
//    a->setToolTip(a->text() + " (" + tr("Ctrl+S") + ")");
//    saveStateAction_ = a;
//    connect(saveStateAction_, &QAction::triggered, this, &PanelWindow::onSaveStateClicked);
}

}


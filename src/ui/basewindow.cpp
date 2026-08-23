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

#include "ui/basewindow.h"

#include <QMenuBar>
#include <QFileDialog>
#include <QMessageBox>
#include <cstdint>
#include <fstream>

#include "ui/aboutdialog.h"

namespace kosmos_cp1::ui {

namespace {
constexpr uint32_t STATE_MAGIC = 0x4b435031; // "KCP1"
constexpr uint16_t STATE_VERSION = 1;

void writeStateHeader(std::ostream& os) {
    os.put(static_cast<char>((STATE_MAGIC >> 24) & 0xff));
    os.put(static_cast<char>((STATE_MAGIC >> 16) & 0xff));
    os.put(static_cast<char>((STATE_MAGIC >> 8) & 0xff));
    os.put(static_cast<char>(STATE_MAGIC & 0xff));
    os.put(static_cast<char>(STATE_VERSION & 0xff));
    os.put(static_cast<char>((STATE_VERSION >> 8) & 0xff));
}

bool readStateHeader(std::istream& is) {
    uint8_t bytes[6];
    is.read(reinterpret_cast<char*>(bytes), sizeof(bytes));
    if (!is) {
        return false;
    }

    const uint32_t magic = static_cast<uint32_t>(bytes[3])
        | (static_cast<uint32_t>(bytes[2]) << 8)
        | (static_cast<uint32_t>(bytes[1]) << 16)
        | (static_cast<uint32_t>(bytes[0]) << 24);
    const uint16_t version = static_cast<uint16_t>(bytes[4])
        | (static_cast<uint16_t>(bytes[5]) << 8);
    return magic == STATE_MAGIC && version == STATE_VERSION;
}
}

BaseWindow::BaseWindow(ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent)
    : executorThread_(executorThread), windowManager_(windowManager), QMainWindow{parent}
{
    windowManager->addWindow(this);
}

BaseWindow::~BaseWindow() {
    for (auto *action : actions_) {
        delete action;
    }
}


void BaseWindow::createMenuBar() {
    QMenu* fileMenu = menuBar()->addMenu(tr("&File"));
    QAction *quitAction = fileMenu->addAction("Quit");
    connect(quitAction, &QAction::triggered, [this] { windowManager_->closeAllWindows();});
    actions_.push_back(quitAction);

    QMenu *windowsMenu = menuBar()->addMenu(tr("&Windows"));
    for (auto *w : windowManager_->windows()) {
        QAction *winAction = windowsMenu->addAction(w->windowName());
        connect(winAction, &QAction::triggered, [this, w] { w->show(); });
        actions_.push_back(winAction);
    }

    QMenu* stateMenu = menuBar()->addMenu(tr("&State"));
    QAction *a = stateMenu->addAction("Load State");
    a->setShortcut(QKeyCombination(Qt::CTRL | Qt::Key_L));
    a->setToolTip(a->text() + " (" + tr("Ctrl+L") + ")");
    connect(a, &QAction::triggered, this, &BaseWindow::onLoadStateClicked);
    actions_.push_back(a);

    a = stateMenu->addAction("Save State");
    a->setShortcut(QKeyCombination(Qt::CTRL | Qt::Key_S));
    a->setToolTip(a->text() + " (" + tr("Ctrl+S") + ")");
    connect(a, &QAction::triggered, this, &BaseWindow::onSaveStateClicked);
    actions_.push_back(a);

    QMenu* helpMenu = menuBar()->addMenu(tr("&Help"));
    a = helpMenu->addAction("About...");
    connect(a, &QAction::triggered, [this] {
        AboutDialog dlg(this);
        dlg.exec();
    });
    actions_.push_back(a);
}

void BaseWindow::onLoadStateClicked() {
    const QString fileName = QFileDialog::getOpenFileName(
        this, tr("Load state"), QString(), tr("CP1 State files (*.cp1);;All files (*)"));
    if (fileName.isEmpty()) {
        return;
    }

    std::ifstream is(fileName.toStdString(), std::ios::binary);
    if (!is) {
        QMessageBox::warning(this, tr("Load state"), tr("Could not open the state file."));
        return;
    }

    EmulatorState state;
    if (!readStateHeader(is)) {
        QMessageBox::warning(this, tr("Load state"), tr("The state file is invalid or unsupported."));
        return;
    }
    is >> state;
    if (!is) {
        QMessageBox::warning(this, tr("Load state"), tr("The state file is invalid or incomplete."));
        return;
    }
    executorThread_->loadState(state);
    is.close();
}

void BaseWindow::onSaveStateClicked() {
    QString fileName = QFileDialog::getSaveFileName(
        this, tr("Save state"), QString(), tr("CP1 State files (*.cp1);;All files (*)"));
    if (fileName.isEmpty()) {
        return;
    }
    if (!fileName.endsWith(".cp1", Qt::CaseInsensitive)) {
        fileName += ".cp1";
    }

    std::ofstream os(fileName.toStdString(), std::ios::binary | std::ios::trunc);
    if (!os) {
        QMessageBox::warning(this, tr("Save state"), tr("Can't save the state!"));
        return;
    }
    writeStateHeader(os);
    os << (executorThread_->saveState());
    if (!os) {
        QMessageBox::warning(this, tr("Save state"), tr("Could not write the state file."));
    }
    os.close();
}


} //namespace kosmos_cp1::ui

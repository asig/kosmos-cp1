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

#include "ui/aboutdialog.h"

namespace kosmos_cp1::ui {

BaseWindow::BaseWindow(WindowManager *windowManager, QWidget *parent)
    : windowManager_(windowManager), QMainWindow{parent}
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

    QMenu* helpMenu = menuBar()->addMenu(tr("&Help"));
    QAction *aboutAction = helpMenu->addAction("About...");
    connect(aboutAction, &QAction::triggered, [this] {
        AboutDialog dlg(this);
        dlg.exec();
    });
    actions_.push_back(aboutAction);
}


} //namespace kosmos_cp1::ui

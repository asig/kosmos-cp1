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

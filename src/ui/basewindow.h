#pragma once

#include <QMainWindow>
#include <QWindow>

#include "ui/windowmanager.h"

namespace kosmos_cp1::ui {

class BaseWindow : public QMainWindow
{
    Q_OBJECT
public:
    explicit BaseWindow(WindowManager *windowManager, QWidget *parent = nullptr);
    virtual ~BaseWindow();

    virtual void createWindow() = 0;
    virtual QString windowName() = 0;

protected:
    void createMenuBar();

private:
    WindowManager *windowManager_;

    std::vector<QAction*> actions_;

};

}

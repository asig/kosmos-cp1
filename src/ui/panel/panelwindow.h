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

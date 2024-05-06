#pragma once

#include <QMainWindow>
#include <QWindow>

#include "executorthread.h"
#include "emulation/intel8049.h"
#include "emulation/intel8155.h"
#include "emulation/dataport.h"
#include "ui/cp1panelwidget.h"
#include "ui/cp5panelwidget.h"

namespace kosmos_cp1::ui {

using ::kosmos_cp1::ExecutorThread;

using ::kosmos_cp1::emulation::Intel8155;
using ::kosmos_cp1::emulation::Intel8049;
using ::kosmos_cp1::emulation::DataPort;

class PanelWindow : public QMainWindow
{
    Q_OBJECT
public:
    explicit PanelWindow(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, QWidget *parent = nullptr);

signals:

public slots:
    void onPinProgWritten(uint8_t val);
    void onPort1ValueChanged(uint8_t oldVal, uint8_t newVal);

    void onLoadStateClicked();
    void onSaveStateClicked();

private:
    Intel8049 *cpu_;
    Intel8155 *pid_;
    Intel8155 *pidExtension_;

    uint8_t pinProgValue_;
    bool inPort1ValueChange_;

    ExecutorThread *executorThread_;

    CP1PanelWidget *cp1Panel_;
    CP5PanelWidget *cp5Panel_;

    QAction* loadStateAction_;
    QAction* saveStateAction_;

    void createActions();
    void createToolBar();
    void createMenuBar();
    void createMainUI();

    void updateWindowTitle(const std::string& state);
};

}

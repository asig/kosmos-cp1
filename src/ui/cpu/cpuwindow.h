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

signals:

public slots:
//    void onPinProgWritten(uint8_t val);
//    void onPort1ValueChanged(uint8_t oldVal, uint8_t newVal);

//    void onLoadStateClicked();
//    void onSaveStateClicked();

    void onCpuStateChanged();
    void onInstructionExecuted();
    void onResetExecuted();
    void onExecutionStarted();
    void onExecutionStopped();
    void onBreakpointHit();

private:
    Intel8049 *cpu_;
    Intel8155 *pid_;
    Intel8155 *pidExtension_;

    ExecutorThread *executorThread_;

    QAction* runAction_;
    QAction* stopAction_;
    QAction* singleStepAction_;
    QAction* resetAction_;
    QAction* breakOnMovXAction_;
    QAction* throttleExecutionAction_;

    I8049DisassemblyWidget *disassembly_;
    I8155StatusWidget *status8155_;
    I8155StatusWidget *status8155Extension_;
    I8049StatusWidget *status8049_;

//    private AboutAction aboutAction;
//    private CoolBarManager coolBarManager;

    void createActions();
    void createToolBar();
    void createMainUI();

    void update8155();
    void updateView();
    void update8155States();

    void enableChildren(bool enabled);
};

} // namespace kosmos_cp1::ui::cpu


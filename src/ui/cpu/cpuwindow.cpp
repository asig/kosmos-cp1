#include "ui/cpu/cpuwindow.h"

#include <QToolBar>
#include <QSplitter>
#include <QVBoxLayout>

#include "ui/cpu/checkboxaction.h"

namespace kosmos_cp1::ui::cpu {


CpuWindow::CpuWindow(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent)
    : cpu_(cpu), pid_(pid), pidExtension_(pidExtension), executorThread_(executorThread), BaseWindow{windowManager, parent}
{
}

void CpuWindow::createWindow() {
    createActions();
    createToolBar();
    createMenuBar();
    createMainUI();

    connect(cpu_->port(0).get(), &DataPort::valueWritten, this, &CpuWindow::onCpuStateChanged);
    connect(cpu_->port(1).get(), &DataPort::valueWritten, this, &CpuWindow::onCpuStateChanged);
    connect(cpu_->port(2).get(), &DataPort::valueWritten, this, &CpuWindow::onCpuStateChanged);

    connect(cpu_, &Intel8049::stateChanged, this, &CpuWindow::onCpuStateChanged);
    connect(cpu_, &Intel8049::instructionExecuted, this, &CpuWindow::onInstructionExecuted);
    connect(cpu_, &Intel8049::resetExecuted, this, &CpuWindow::onResetExecuted);

    connect(pid_, &Intel8155::resetExecuted, this, &CpuWindow::onResetExecuted);
    connect(pid_, &Intel8155::commandRegisterWritten, this, &CpuWindow::update8155);
    connect(pid_, &Intel8155::portWritten, this, &CpuWindow::update8155);
    connect(pid_->ram(), &Ram::memoryWritten, this, &CpuWindow::update8155);
    connect(pid_, &Intel8155::pinsChanged, this, &CpuWindow::update8155);

    connect(pidExtension_, &Intel8155::resetExecuted, this, &CpuWindow::onResetExecuted);
    connect(pidExtension_, &Intel8155::commandRegisterWritten, this, &CpuWindow::update8155);
    connect(pidExtension_, &Intel8155::portWritten, this, &CpuWindow::update8155);
    connect(pidExtension_->ram(), &Ram::memoryWritten, this, &CpuWindow::update8155);
    connect(pidExtension_, &Intel8155::pinsChanged, this, &CpuWindow::update8155);

    connect(executorThread_, &ExecutorThread::executionStarted, this, &CpuWindow::onExecutionStarted);
    connect(executorThread_, &ExecutorThread::executionStopped, this, &CpuWindow::onExecutionStopped);
    connect(executorThread_, &ExecutorThread::breakpointHit, this, &CpuWindow::onBreakpointHit);

    setWindowTitle("Intel MCS-48 Emulator");
}

QString CpuWindow::windowName() {
    return "CPU";
}

void CpuWindow::update8155() {
    if (executorThread_->isRunning()) { // Only update in single-step mode
        return;
    }
    status8155_->updateState();
    status8155Extension_->updateState();
}

void CpuWindow::onCpuStateChanged() {
    if (executorThread_->isRunning()) { // Only update in single-step mode
        return;
    }
    updateView();
    status8049_->updateState();
    update8155States();
}

void CpuWindow::onInstructionExecuted() {
    if (executorThread_->isRunning()) { // Only update in single-step mode
        return;
    }
    updateView();
    status8049_->updateState();
}

void CpuWindow::onExecutionStarted() {
    singleStepAction_->setEnabled(false);
    stopAction_->setEnabled(true);
    runAction_->setEnabled(false);

    enableChildren(false);
}

void CpuWindow::onExecutionStopped() {
    updateView();
    singleStepAction_->setEnabled(true);
    stopAction_->setEnabled(false);
    runAction_->setEnabled(true);
    status8049_->updateState();
    update8155States();

    enableChildren(true);
}

void CpuWindow::onResetExecuted() {
    updateView();
    bool isRunning = executorThread_->isRunning();
    singleStepAction_->setEnabled(!isRunning);
    stopAction_->setEnabled(isRunning);
    runAction_->setEnabled(!isRunning);
    status8049_->updateState();
    update8155States();

    enableChildren(!isRunning);
}

void CpuWindow::onBreakpointHit() {
    status8049_->updateState();
    update8155States();
    updateView();

    enableChildren(true);
}

void CpuWindow::updateView() {
    disassembly_->setAddress(cpu_->state().pc);
}

void CpuWindow::update8155States() {
    status8155_->updateState();
    status8155Extension_->updateState();
}

void CpuWindow::createMainUI() {
    disassembly_ = new I8049DisassemblyWidget(cpu_->rom(), executorThread_);
    status8049_ = new I8049StatusWidget("8049 (Main unit)", cpu_);
    status8155_ = new I8155StatusWidget("8155 (Main unit)", pid_);
    status8155Extension_ = new I8155StatusWidget("8155 (CP3 memory extensions)", pidExtension_);

    QWidget *leftSide = new QWidget();
    QVBoxLayout *leftSideLayout = new QVBoxLayout();
    leftSideLayout->addWidget(disassembly_);
    leftSide->setLayout(leftSideLayout);

    QWidget *rightSide = new QWidget();
    QVBoxLayout *rightSideLayout = new QVBoxLayout();
    rightSideLayout->addWidget(status8049_);
    rightSideLayout->addWidget(status8155_);
    rightSideLayout->addWidget(status8155Extension_);
    rightSide->setLayout(rightSideLayout);


    QSplitter* splitter = new QSplitter(Qt::Horizontal);
    splitter->addWidget(leftSide);
    splitter->addWidget(rightSide);

    setCentralWidget(splitter);

    enableChildren(false);
}

void CpuWindow::createActions() {
    runAction_ = new QAction(QIcon(":/ui/actions/control.png"), tr("Start"));
    stopAction_ = new QAction(QIcon(":/ui/actions/control-stop-square.png"), tr("Stop"));
    singleStepAction_ = new QAction(QIcon(":/ui/actions/arrow-step-over.png"), tr("Step"));
    resetAction_ = new QAction(QIcon(":/ui/actions/arrow-circle-135-left.png"), tr("Reset"));
    breakOnMovXAction_ = new CheckboxAction("Break on MOVX", false);

    connect(runAction_, &QAction::triggered, [this] {
        executorThread_->startExecution();        
    } );

    connect(stopAction_, &QAction::triggered, [this] {
        executorThread_->stopExectuion();
    } );

    connect(singleStepAction_, &QAction::triggered, [this] {
        executorThread_->singleStep();
    } );

    connect(resetAction_, &QAction::triggered, [this] {
        executorThread_->reset();
    });

    connect(breakOnMovXAction_, &QAction::toggled, [this] {
        executorThread_->setBreakOnMovX(breakOnMovXAction_->isChecked());
    });

}

void CpuWindow::createToolBar() {
#ifdef Q_OS_MACOS
    setUnifiedTitleAndToolBarOnMac(true);
#endif

    QToolBar* tb = new QToolBar("Toolbar", this);
    tb->addAction(singleStepAction_);
    tb->addAction(runAction_);
    tb->addAction(stopAction_);
    tb->addAction(resetAction_);
    tb->addSeparator();
    tb->addAction(breakOnMovXAction_);

    addToolBar(tb);
}

void CpuWindow::enableChildren(bool enabled) {
    disassembly_->setEnabled(enabled);
    status8049_->setEnabled(enabled);
    status8155_->setEnabled(enabled);
    status8155Extension_->setEnabled(enabled);
}

void CpuWindow::closeEvent(QCloseEvent *event) {
    executorThread_->startExecution();
}

void CpuWindow::showEvent(QShowEvent *event) {
    executorThread_->stopExectuion();
}

} //namespace kosmos_cp1::ui::cpu

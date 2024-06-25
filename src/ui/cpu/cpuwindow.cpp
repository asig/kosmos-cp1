#include "ui/cpu/cpuwindow.h"

#include <QToolBar>
#include <QSplitter>
#include <QVBoxLayout>

#include "ui/cpu/checkboxaction.h"

namespace kosmos_cp1::ui::cpu {


CpuWindow::CpuWindow(Intel8049 *cpu, Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, QWidget *parent)
    : cpu_(cpu), pid_(pid), pidExtension_(pidExtension), executorThread_(executorThread), QMainWindow{parent}
{
    traceExecution_ = false;

    createActions();
    createToolBar();
    createMenuBar();
    createMainUI();

    connect(cpu_->port(0).get(), &DataPort::valueChange, this, [this]{ if(traceExecution_) status8049_->updateState();} );
    connect(cpu_->port(1).get(), &DataPort::valueChange, this, [this]{if(traceExecution_) status8049_->updateState();});
    connect(cpu_->port(2).get(), &DataPort::valueChange, this, [this]{if(traceExecution_) status8049_->updateState();});

    connect(cpu_, &Intel8049::stateChanged, this, &CpuWindow::onCpuStateChanged);
    connect(cpu_, &Intel8049::instructionExecuted, this, &CpuWindow::onInstructionExecuted);
    connect(cpu_, &Intel8049::resetExecuted, this, &CpuWindow::onResetExecuted);

    connect(pid_, &Intel8155::resetExecuted, this, &CpuWindow::onResetExecuted);
    connect(pid_, &Intel8155::commandRegisterWritten, this, [this] { update8155(); } );
    connect(pid_, &Intel8155::portWritten, this, [this] { update8155(); } );
    connect(pid_, &Intel8155::memoryWritten, this, [this] { update8155(); } );
    connect(pid_, &Intel8155::pinsChanged, this, [this] { update8155(); } );

    connect(pidExtension_, &Intel8155::resetExecuted, this, &CpuWindow::onResetExecuted);
    connect(pidExtension_, &Intel8155::commandRegisterWritten, [this] { update8155(); } );
    connect(pidExtension_, &Intel8155::portWritten, [this] { update8155(); } );
    connect(pidExtension_, &Intel8155::memoryWritten, [this] { update8155(); } );
    connect(pidExtension_, &Intel8155::pinsChanged, [this] { update8155(); } );

    connect(executorThread_, &ExecutorThread::executionStarted, this, &CpuWindow::onExecutionStarted);
    connect(executorThread_, &ExecutorThread::executionStopped, this, &CpuWindow::onExecutionStopped);
    connect(executorThread_, &ExecutorThread::breakpointHit, this, &CpuWindow::onBreakpointHit);

    setWindowTitle("Intel MCS-48 Emulator");
}

void CpuWindow::update8155() {
    if (!traceExecution_) {
        return;
    }
    status8155_->updateState();
    status8155Extension_->updateState();
}

void CpuWindow::onCpuStateChanged() {
    if (!traceExecution_) {
        return;
    }
    updateView();
    status8049_->updateState();
    update8155States();
}

void CpuWindow::onInstructionExecuted() {
    if (!traceExecution_) {
        return;
    }
    updateView();
    status8049_->updateState();
}

void CpuWindow::onExecutionStarted() {
    singleStepAction_->setEnabled(false);
    stopAction_->setEnabled(true);
    runAction_->setEnabled(false);
}

void CpuWindow::onExecutionStopped() {
    updateView();
    singleStepAction_->setEnabled(true);
    stopAction_->setEnabled(false);
    runAction_->setEnabled(true);
    status8049_->updateState();
    update8155States();
}

void CpuWindow::onResetExecuted() {
    updateView();
    singleStepAction_->setEnabled(true);
    stopAction_->setEnabled(false);
    runAction_->setEnabled(true);
    status8049_->updateState();
    update8155States();
}

void CpuWindow::onBreakpointHit() {
    status8049_->updateState();
    update8155States();
    updateView();
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
    status8155_ = new I8155StatusWidget("8155 (Main unit)", pid_->ram());
    status8155Extension_ = new I8155StatusWidget("8155 (CP3 memory extensions)", pidExtension_->ram());

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
}

void CpuWindow::createActions() {
    runAction_ = new QAction(QIcon(":/ui/actions/control.png"), tr("Start"));
    stopAction_ = new QAction(QIcon(":/ui/actions/control-stop-square.png"), tr("Stop"));
    singleStepAction_ = new QAction(QIcon(":/ui/actions/arrow-step-over.png"), tr("Step"));
    resetAction_ = new QAction(QIcon(":/ui/actions/arrow-circle-135-left.png"), tr("Reset"));
    traceExecutionAction_ = new CheckboxAction("Trace execution", true);

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

    traceExecution_ = traceExecutionAction_->isChecked();
    connect(traceExecutionAction_, &QAction::toggled, [this] {
        traceExecution_ = traceExecutionAction_->isChecked();
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
    tb->addAction(traceExecutionAction_);

    addToolBar(tb);
}

void CpuWindow::createMenuBar() {
}

//    private void createActions() {
//        resetAction = new ResetAction(executorThread);
//        breakOnMovxAction = new BreakOnMovxAction(executorThread);
//        traceExecutionAction = new TraceExecutionAction(executorThread, this);
//        throttleExecutionAction = new ThrottleExecutionAction(executorThread);
//        runAction = new RunAction(executorThread);
//        stopAction = new StopAction(executorThread, this);
//        singleStepAction = new SingleStepAction(this, executorThread);
//        save8049DisassemblyAction = new Save8049DisassemblyAction(cpu);
//        loadAction = new LoadAction(shell, pid, pidExtension, executorThread);
//        saveAction = new SaveAction(shell, pid, pidExtension, executorThread);
//        aboutAction = new AboutAction();
//        quitAction = new QuitAction();

//        resetAction.setDependentActions(singleStepAction, runAction, stopAction);
//        runAction.setDependentActions(singleStepAction, stopAction);
//        stopAction.setDependentActions(singleStepAction, runAction);
//    }

//    /**
//     * Open the window.
//     */
//    public void open() {
//        createShell();
//        createActions();
//        createContents();

//        addListeners();
//        shell.addDisposeListener(disposeEvent -> removeListeners());

//        shell.setMenuBar(createMenuBar());
//        shell.open();
//        shell.layout();
//        fireWindowOpened();
//    }

//    @Override
//    protected Shell getShell() {
//        return shell;
//    }

//    private void addListeners() {
//        cpu.addListener(cpuStateListener);
//        pid.addListener(pidStateListener);
//        pidExtension.addListener(pidStateListener);
//        executorThread.addListener(executionListener);
//        cpu.getPort(0).addListener(portListener);
//        cpu.getPort(1).addListener(portListener);
//        cpu.getPort(2).addListener(portListener);
//    }

//    private void removeListeners() {
//        cpu.removeListener(cpuStateListener);
//        pid.removeListener(pidStateListener);
//        pidExtension.removeListener(pidStateListener);
//        executorThread.removeListener(executionListener);
//        cpu.getPort(0).removeListener(portListener);
//        cpu.getPort(1).removeListener(portListener);
//        cpu.getPort(2).removeListener(portListener);
//    }

//    public boolean isDisposed() {
//        return shell.isDisposed();
//    }

//    @SuppressWarnings("unchecked")
//    private Menu createMenuBar() {
//        Menu menu = new Menu(shell, SWT.BAR);

//        createFileMenu(
//                menu,
//                (m) -> new MenuItem(m, SWT.SEPARATOR),
//                (m) -> new ActionMenuItem(m, SWT.NONE, save8049DisassemblyAction));

//        createWindowMenu(menu);
//        createHelpMenu(menu);

//        return menu;
//    }

//    private void createShell() {
//        Display display = Display.getDefault();
//        shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER);
//        shell.setText("Intel MCS-48 Emulator");
//        shell.setLayout(new GridLayout(1, false));
//        Image icon = SWTResources.getImage("/com/asigner/cp1/ui/icon-128x128.png");
//        shell.setImage(icon);
//        shell.addDisposeListener(disposeEvent -> {
//            dispose();
//            fireWindowClosed();
//        });
//    }

//    public void dispose() {
//        if (coolBarManager != null) {
//            coolBarManager.dispose();
//        }
//    }

//    private IContributionItem makeForcedTextContributionItem(Action action) {
//        ActionContributionItem item = new ActionContributionItem(action);
//        item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
//        return item;
//    }

//    private CoolBarManager createToolBarManager() {
//        CoolBarManager coolBarManager = new CoolBarManager(SWT.FLAT);
//        {
//            ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.NO_FOCUS);
//            coolBarManager.add(toolBarManager);
//            toolBarManager.add(makeForcedTextContributionItem(singleStepAction));
//            toolBarManager.add(makeForcedTextContributionItem(runAction));
//            toolBarManager.add(makeForcedTextContributionItem(stopAction));
//            toolBarManager.add(makeForcedTextContributionItem(resetAction));
//        }
//        {
//            ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.NO_FOCUS);
//            coolBarManager.add(toolBarManager);
//            toolBarManager.add(new CheckboxContributionItem(breakOnMovxAction));
//            toolBarManager.add(new CheckboxContributionItem(traceExecutionAction));
//            toolBarManager.add(new CheckboxContributionItem(throttleExecutionAction));
//        }

//        return coolBarManager;
//    }

//    /**
//     * Create contents of the window.
//     */
//    private void createContents() {
//        coolBarManager = createToolBarManager();
//        coolBarManager.createControl(shell);

//        Composite composite_1 = new Composite(shell, SWT.NONE);
//        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//        composite_1.setLayout(new GridLayout(2, false));

//        Group group = new Group(composite_1, SWT.NONE);
//        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//        group.setLayout(new FillLayout(SWT.HORIZONTAL));
//        group.setText("Disassembly");

//        disassembly = new DisassemblyComposite(group, SWT.NONE);
//        disassembly.setRom(cpu.getRom());
//        disassembly.addListener((addr, enabled) -> executorThread.enableBreakpoint(addr, enabled));

//        Composite composite = new Composite(composite_1, SWT.NONE);
//        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
//        GridLayout layout = new GridLayout(1, false);
//        layout.marginWidth = 0;
//        layout.marginHeight = 0;
//        composite.setLayout(layout);

//        status8049 = new Status8049Composite(composite, SWT.NONE);
//        status8049.setTraceExecution(isTraceExecution());
//        status8049.setText("8049 (Main unit)");
//        status8049.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4));
//        status8049.setCpu(cpu);

//        status8155 = new Status8155Composite(composite, SWT.NONE);
//        status8155.setText("8155 (Main unit)");
//        status8155.setTraceExecution(isTraceExecution());
//        status8155.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//        status8155.setPID(pid);

//        status8155Extension = new Status8155Composite(composite, SWT.NONE);
//        status8155Extension.setText("8155 (CP3 memory extension)");
//        status8155Extension.setTraceExecution(isTraceExecution());
//        status8155Extension.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//        status8155Extension.setPID(pidExtension);

//        stopAction.setEnabled(false);

//        shell.pack();
//    }

//    public boolean isTraceExecution() {
//        return traceExecution;
//    }

//    public void setTraceExecution(boolean traceExecution) {
//        status8049.setTraceExecution(traceExecution);
//        status8155.setTraceExecution(traceExecution);
//        status8155Extension.setTraceExecution(traceExecution);
//        this.traceExecution = traceExecution;
//    }

//    private void resetExecuted() {
//        if (isDisposed()) {
//            return;
//        }
//        singleStepAction.setEnabled(true);
//        stopAction.setEnabled(false);
//        runAction.setEnabled(true);
//        updateView();
//        status8049.updateState();
//        update8155States();
//    }

//    private void updateView() {
//        disassembly.selectAddress(cpu.getPC());
//    }

//    /**
//     * @wbp.parser.entryPoint
//     */
//    private static void wbpEntryPoint() {
//        try {
//            Rom rom = new Rom(new FileInputStream("CP1.bin"));
//            DataPort bus = new DataPort("BUS");
//            DataPort p1 = new DataPort("P1");
//            DataPort p2 = new DataPort("P2");
//            Intel8049 cpu = new Intel8049(rom, bus, p1, p2);
//            Intel8155 pid = new Intel8155("internal", bus);
//            Intel8155 pidExtension = new Intel8155("extension", bus);

//            ExecutorThread executorThread = new ExecutorThread(cpu, pid, pidExtension);
//            CpuWindow cpuWindow = new CpuWindow(new WindowManager(), cpu, pid, pidExtension, executorThread);
//            executorThread.start();
//            cpuWindow.open();
//            Display display = Display.getDefault();
//            while (!cpuWindow.isDisposed()) {
//                if (!display.readAndDispatch()) {
//                    display.sleep();
//                }
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//}

} //namespace kosmos_cp1::ui::cpu

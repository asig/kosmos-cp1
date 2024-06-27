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
    createToolBar();
    createMenuBar();
    createMainUI();

    pinProgValue_ = 0;
    inPort1ValueChange_ = false;

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

    connect(cpu_->port(1).get(), &DataPort::valueChange, this, &PanelWindow::onPort1ValueChanged, Qt::DirectConnection);

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

void PanelWindow::onPort1ValueChanged(uint8_t oldVal, uint8_t newVal) {
    if (inPort1ValueChange_) {
        // Called by our own write. Bail out.
        return;
    }

    // CPU wrote to the port to prepare the pins for input. Write switch settings
    inPort1ValueChange_ = true;
    cpu_->port(1)->write(cp5Panel_->readSwitches());
    inPort1ValueChange_ = false;
}

void PanelWindow::createActions() {
    QAction *a = new QAction(tr("Load state"));
    a->setShortcut(QKeyCombination(Qt::CTRL | Qt::Key_L));
    a->setToolTip(a->text() + " (" + tr("Ctrl+L") + ")");
    loadStateAction_ = a;
    connect(loadStateAction_, &QAction::triggered, this, &PanelWindow::onLoadStateClicked);

    a = new QAction(tr("Save state"));
    a->setShortcut(QKeyCombination(Qt::CTRL | Qt::Key_S));
    a->setToolTip(a->text() + " (" + tr("Ctrl+S") + ")");
    saveStateAction_ = a;
    connect(saveStateAction_, &QAction::triggered, this, &PanelWindow::onSaveStateClicked);
}

void PanelWindow::createToolBar() {
}

void PanelWindow::onLoadStateClicked() {
}

void PanelWindow::onSaveStateClicked() {
}

///**
//     * Open the window.
//     */
//public void open() {
//    createShell();
//    createActions();
//    createContent();

//    executorThread.addListener(executionListener);
//    cpu.getPort(1).addListener(port1Listener);
//    pid.addListener(pidListener);
//    shell.addDisposeListener(ev -> {
//        executorThread.removeListener(executionListener);
//        cpu.getPort(1).removeListener(port1Listener);
//        pid.removeListener(pidListener);
//    });

//    shell.setMenuBar(createMenuBar());
//    shell.open();
//    shell.layout();
//    shell.pack();
//    fireWindowOpened();
//}

//@Override
//    protected Shell getShell() {
//    return shell;
//}

//public boolean isDisposed() {
//    return shell.isDisposed();
//}

//private void createShell() {
//    shell = new Shell((Display)null, SWT.SHELL_TRIM | SWT.CENTER);
//    updateWindowTitle("stopped");
//    shell.setLayout(new FillLayout(SWT.HORIZONTAL));
//    Image icon = SWTResources.getImage("/com/asigner/cp1/ui/icon-128x128.png");
//    shell.setImage(icon);
//    shell.addDisposeListener(disposeEvent -> fireWindowClosed());
//}

//private void createActions() {
//    loadAction = new LoadAction(shell, pid, pidExtension, executorThread);
//    saveAction = new SaveAction(shell, pid, pidExtension, executorThread);
//}

///**
//     * Create contents of the window.
//     * @wbp.parser.entryPoint
//     */
//private void createContent() {
//    Composite composite = new Composite(shell, SWT.NONE);
//    GridLayout gl_composite = new GridLayout(1, false);
//    gl_composite.marginTop = 0;
//    gl_composite.marginBottom = 0;
//    gl_composite.marginRight = 0;
//    gl_composite.marginLeft = 0;
//    composite.setLayout(gl_composite);
//    composite.setBackground(CP1Colors.GREEN);

//    cp5 = new CP5Panel(composite, SWT.NONE);
//    cp5.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
//    cp5.writeLeds(pid.getPaValue());
//    cpu.getPort(1).write(cp5.readSwitches());
//    cp5.addSwitchesListener(() -> {
//        cpu.getPort(1).write(cp5.readSwitches());
//    });

    //        Label spacer1 = new Label(composite, SWT.NONE);
    //        spacer1.setLayoutData(GridDataFactory.fillDefaults().hint(-1, 50).create());
    //        spacer1.setBackground(CP1Colors.GREEN);

//    cp1 = new CP1Panel(composite, SWT.NONE);
//    cp1.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
//    cp1.getCP1Display().setPid(pid);
//}

//private Menu createMenuBar() {
//    Menu menu = new Menu(shell, SWT.BAR);

//    createFileMenu(menu);

//    Menu stateMenu = new Menu(menu);
//    new ActionMenuItem(stateMenu, SWT.NONE, loadAction);
//    new ActionMenuItem(stateMenu, SWT.NONE, saveAction);

//    MenuItem stateItem = new MenuItem(menu, SWT.CASCADE);
//    stateItem.setText("&State");
//    stateItem.setMenu(stateMenu);

//    createWindowMenu(menu);
//    createHelpMenu(menu);

//    return menu;
//}

}


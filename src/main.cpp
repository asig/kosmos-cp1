#include <QApplication>
#include <QFile>

#include "emulation/dataport.h"
#include "emulation/intel8049.h"
#include "emulation/intel8155.h"
#include "executorthread.h"
#include "ui/panelwindow.h"

using kosmos_cp1::emulation::DataPort;
using kosmos_cp1::emulation::Intel8049;
using kosmos_cp1::emulation::Intel8155;
using kosmos_cp1::ExecutorThread;
using kosmos_cp1::ui::PanelWindow;

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    a.setWindowIcon(QIcon(":/ui/icon-128x128.png"));

    // Load ROM
    QFile romFile(":/CP1.bin");
    romFile.open(QIODevice::ReadOnly);
    QByteArray romContent = romFile.readAll();
    romFile.close();
    std::vector<uint8_t> rom(romContent.begin(), romContent.end());

    std::shared_ptr<DataPort> bus = std::make_shared<DataPort>("BUS");
    std::shared_ptr<DataPort> p1 = std::make_shared<DataPort>("P1");
    std::shared_ptr<DataPort> p2 = std::make_shared<DataPort>("P2");

    Intel8049 cpu(rom, bus, p1, p2);
    Intel8155 pid("internal", bus);
    Intel8155 pidExtension("extension", bus);

    // Connect the relevant pins to the main unit's 8155
    QObject::connect(&cpu, &Intel8049::pinALEWritten, &pid, &Intel8155::onPinALEWritten);
    QObject::connect(&cpu, &Intel8049::pinRDLowActiveWritten, &pid, &Intel8155::onPinRDLowActiveWritten);
    QObject::connect(&cpu, &Intel8049::pinWRLowActiveWritten, &pid, &Intel8155::onPinWRLowActiveWritten);
    QObject::connect(p2.get(), &DataPort::bit4Written, &pid, &Intel8155::onPinCELowActiveWritten);
    QObject::connect(p2.get(), &DataPort::bit6Written, &pid, &Intel8155::onPinResetWritten);
    QObject::connect(p2.get(), &DataPort::bit7Written, &pid, &Intel8155::onPinIOWritten);

    // Connect the relevant pins to the CP5 Memory Extension's 8155
    QObject::connect(&cpu, &Intel8049::pinALEWritten, &pidExtension, &Intel8155::onPinALEWritten);
    QObject::connect(&cpu, &Intel8049::pinRDLowActiveWritten, &pidExtension, &Intel8155::onPinRDLowActiveWritten);
    QObject::connect(&cpu, &Intel8049::pinWRLowActiveWritten, &pidExtension, &Intel8155::onPinWRLowActiveWritten);

    QObject::connect(p2.get(), &DataPort::bit5Written, &pidExtension, &Intel8155::onPinCELowActiveWritten);
    QObject::connect(p2.get(), &DataPort::bit6Written, &pidExtension, &Intel8155::onPinResetWritten);
    QObject::connect(p2.get(), &DataPort::bit7Written, &pidExtension, &Intel8155::onPinIOWritten);

    // Now, reset the CPU
    cpu.reset();

//    Display.setAppName(Window.APP_NAME);

//    WindowManager windowManager = new WindowManager();
//    CpuWindow cpuWindow = new CpuWindow(windowManager, cpu, pid, pidExtension, executorThread);
//    KosmosPanelWindow panelWindow = new KosmosPanelWindow(windowManager, cpu, pid, pidExtension, executorThread);
//    AssemblerWindow assemblerWindow = new AssemblerWindow(windowManager, pid, pidExtension, executorThread);
//    windowManager.addWindow(cpuWindow);
//    windowManager.addWindow(panelWindow);
//    windowManager.addWindow(assemblerWindow);

//    cpuWindow.open();
//    panelWindow.open();
    ExecutorThread executorThread(&cpu, &pid, &pidExtension);
    executorThread.start();
    executorThread.postCommand(ExecutorThread::Command::START);

    PanelWindow w(&cpu, &pid, &pidExtension, &executorThread);
    w.show();
    int res = a.exec();
    executorThread.postCommand(ExecutorThread::Command::QUIT);
    executorThread.join();
    return res;
}

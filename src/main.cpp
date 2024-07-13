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

#include <QApplication>
#include <QFile>

#include "emulation/dataport.h"
#include "emulation/intel8049.h"
#include "emulation/intel8155.h"
#include "executorthread.h"
#include "ui/windowmanager.h"
#include "ui/panel/panelwindow.h"
#include "ui/cpu/cpuwindow.h"
#include "ui/assembler/assemblerwindow.h"
#include "ui/resources.h"

using kosmos_cp1::emulation::DataPort;
using kosmos_cp1::emulation::Intel8049;
using kosmos_cp1::emulation::Intel8155;
using kosmos_cp1::ExecutorThread;
using kosmos_cp1::ui::panel::PanelWindow;
using kosmos_cp1::ui::assembler::AssemblerWindow;
using kosmos_cp1::ui::cpu::CpuWindow;
using kosmos_cp1::ui::WindowManager;

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    kosmos_cp1::ui::Resources::init();
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
    QObject::connect(&cpu, &Intel8049::pinALEWritten, &pid, &Intel8155::onPinALEWritten, Qt::DirectConnection);
    QObject::connect(&cpu, &Intel8049::pinRDLowActiveWritten, &pid, &Intel8155::onPinRDLowActiveWritten, Qt::DirectConnection);
    QObject::connect(&cpu, &Intel8049::pinWRLowActiveWritten, &pid, &Intel8155::onPinWRLowActiveWritten, Qt::DirectConnection);
    QObject::connect(p2.get(), &DataPort::bit4Written, &pid, &Intel8155::onPinCELowActiveWritten, Qt::DirectConnection);
    QObject::connect(p2.get(), &DataPort::bit6Written, &pid, &Intel8155::onPinResetWritten, Qt::DirectConnection);
    QObject::connect(p2.get(), &DataPort::bit7Written, &pid, &Intel8155::onPinIOWritten, Qt::DirectConnection);

    // Connect the relevant pins to the CP5 Memory Extension's 8155
    QObject::connect(&cpu, &Intel8049::pinALEWritten, &pidExtension, &Intel8155::onPinALEWritten, Qt::DirectConnection);
    QObject::connect(&cpu, &Intel8049::pinRDLowActiveWritten, &pidExtension, &Intel8155::onPinRDLowActiveWritten, Qt::DirectConnection);
    QObject::connect(&cpu, &Intel8049::pinWRLowActiveWritten, &pidExtension, &Intel8155::onPinWRLowActiveWritten, Qt::DirectConnection);

    QObject::connect(p2.get(), &DataPort::bit5Written, &pidExtension, &Intel8155::onPinCELowActiveWritten, Qt::DirectConnection);
    QObject::connect(p2.get(), &DataPort::bit6Written, &pidExtension, &Intel8155::onPinResetWritten, Qt::DirectConnection);
    QObject::connect(p2.get(), &DataPort::bit7Written, &pidExtension, &Intel8155::onPinIOWritten, Qt::DirectConnection);

    // Now, reset the CPU
    cpu.reset();

    ExecutorThread executorThread(&cpu, &pid, &pidExtension);
    executorThread.start();

    WindowManager windowManager;
    PanelWindow panelWindow(&cpu, &pid, &pidExtension, &executorThread, &windowManager);
    CpuWindow cpuWindow(&cpu, &pid, &pidExtension, &executorThread, &windowManager);
    AssemblerWindow assemblerWindow(&pid, &pidExtension, &executorThread, &windowManager);

    for(auto *w : windowManager.windows()) {
        w->createWindow();
    }

    panelWindow.show();

    executorThread.startExecution();
    int res = a.exec();
    executorThread.quit();
    executorThread.wait();
    return res;
}

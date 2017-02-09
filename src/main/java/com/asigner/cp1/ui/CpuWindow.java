/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
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

package com.asigner.cp1.ui;

import com.asigner.cp1.emulation.DataPort;
import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;
import com.asigner.cp1.ui.actions.AboutAction;
import com.asigner.cp1.ui.actions.AssemblerAction;
import com.asigner.cp1.ui.actions.BreakOnMovxAction;
import com.asigner.cp1.ui.actions.LoadAction;
import com.asigner.cp1.ui.actions.ResetAction;
import com.asigner.cp1.ui.actions.RunAction;
import com.asigner.cp1.ui.actions.SaveAction;
import com.asigner.cp1.ui.actions.Save8049DisassemblyAction;
import com.asigner.cp1.ui.actions.SingleStepAction;
import com.asigner.cp1.ui.actions.StopAction;
import com.asigner.cp1.ui.actions.TraceExecutionAction;
import com.asigner.cp1.ui.widgets.ActionMenuItem;
import com.asigner.cp1.ui.widgets.ActionToolItem;
import com.asigner.cp1.ui.widgets.CheckboxToolItem;
import com.asigner.cp1.ui.widgets.DisassemblyComposite;
import com.asigner.cp1.ui.widgets.Status8049Composite;
import com.asigner.cp1.ui.widgets.Status8155Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.FileInputStream;
import java.io.IOException;

public class CpuWindow implements ExecutorThread.ExecutionListener, Intel8049.StateListener, Intel8155.StateListener {

    private RunAction runAction;
    private StopAction stopAction;
    private SingleStepAction singleStepAction;
    private ResetAction resetAction;
    private BreakOnMovxAction breakOnMovxAction;
    private TraceExecutionAction traceExecutionAction;
    private Save8049DisassemblyAction save8049DisassemblyAction;
    private LoadAction loadAction;
    private SaveAction saveAction;
    private AssemblerAction assemblerAction;
    private AboutAction aboutAction;

    private Shell shell;
    private Intel8049 cpu;
    private Intel8155 pid;
    private Intel8155 pidExtension;
    private ExecutorThread executorThread;
    private boolean traceExecution = true;

    private Status8049Composite status8049;
    private Status8155Composite status8155;
    private Status8155Composite status8155Extension;
    private DisassemblyComposite disassembly;

    public CpuWindow(Intel8049 cpu, Intel8155 pid, Intel8155 pidExtension, ExecutorThread executorThread) throws IOException {
        this.cpu = cpu;
        this.pid = pid;
        this.pidExtension = pidExtension;
        this.executorThread = executorThread;

        cpu.addListener(this);

        executorThread.addListener(this);
    }


    private void createActions() {
        resetAction = new ResetAction(executorThread);
        breakOnMovxAction = new BreakOnMovxAction(executorThread);
        traceExecutionAction = new TraceExecutionAction(executorThread, this);
        runAction = new RunAction(executorThread);
        stopAction = new StopAction(executorThread, this);
        singleStepAction = new SingleStepAction(this, executorThread);
        save8049DisassemblyAction = new Save8049DisassemblyAction(cpu);
        loadAction = new LoadAction(shell, pid, pidExtension, executorThread);
        saveAction = new SaveAction(shell, pid, pidExtension, executorThread);
        assemblerAction = new AssemblerAction(shell, pid, pidExtension, executorThread);
        aboutAction = new AboutAction();

        resetAction.setDependentActions(singleStepAction, runAction, stopAction);
        runAction.setDependentActions(singleStepAction, stopAction);
        stopAction.setDependentActions(singleStepAction, runAction);
    }

    /**
     * Open the window.
     */
    public void open() {
        createShell();
        createActions();
        createContents();

        shell.setMenuBar(createMenuBar());
        shell.open();
        shell.layout();
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    protected Menu createMenuBar() {
        Menu menu = new Menu(shell, SWT.BAR);

        Menu fileMenu = new Menu(menu);
        new ActionMenuItem(fileMenu, SWT.NONE, save8049DisassemblyAction);

        Menu stateMenu = new Menu(menu);
        new ActionMenuItem(stateMenu, SWT.NONE, loadAction);
        new ActionMenuItem(stateMenu, SWT.NONE, saveAction);
        new MenuItem(fileMenu, SWT.SEPARATOR);
        new ActionMenuItem(stateMenu, SWT.NONE, assemblerAction);

        Menu helpMenu = new Menu(menu);
        new ActionMenuItem(helpMenu, SWT.NONE, aboutAction);

        MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
        fileItem.setText("&File");
        fileItem.setMenu(fileMenu);
        MenuItem stateItem = new MenuItem(menu, SWT.CASCADE);
        stateItem.setText("&State");
        stateItem.setMenu(stateMenu);
        MenuItem helpItem = new MenuItem(menu, SWT.CASCADE);
        helpItem.setText("Help");
        helpItem.setMenu(helpMenu);

        return menu;
    }

    private void createShell() {
        Display display = Display.getDefault();
        shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER);
        shell.setText("Intel MCS-48 Emulator");
        shell.setLayout(new GridLayout(1, false));
        Image icon = SWTResources.getImage("/com/asigner/cp1/ui/icon-128x128.png");
        shell.setImage(icon);
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        ToolBar toolbar = new ToolBar(shell, SWT.FLAT);
        ToolItem toolItem1 = new ActionToolItem(toolbar, SWT.PUSH, singleStepAction);
        ToolItem toolItem2 = new ActionToolItem(toolbar, SWT.PUSH, runAction);
        ToolItem toolItem3 = new ActionToolItem(toolbar, SWT.PUSH, stopAction);
        ToolItem toolItem4 = new ActionToolItem(toolbar, SWT.PUSH, resetAction);
        ToolItem toolItem5 = new CheckboxToolItem(toolbar, breakOnMovxAction);
        ToolItem toolItem6 = new CheckboxToolItem(toolbar, traceExecutionAction);

        Composite composite_1 = new Composite(shell, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite_1.setLayout(new GridLayout(2, false));

        Group group = new Group(composite_1, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        group.setLayout(new FillLayout(SWT.HORIZONTAL));
        group.setText("Disassembly");

        disassembly = new DisassemblyComposite(group, SWT.NONE);
        disassembly.setRom(cpu.getRom());
        disassembly.addListener((addr, enabled) -> executorThread.enableBreakpoint(addr, enabled));

        Composite composite = new Composite(composite_1, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        status8049 = new Status8049Composite(composite, SWT.NONE);
        status8049.setTraceExecution(isTraceExecution());
        status8049.setText("8049 (Main unit)");
        status8049.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4));
        status8049.setCpu(cpu);
        DataPort.Listener portListener = (oldValue, newValue) -> {
            if (isTraceExecution()) {
                shell.getDisplay().syncExec(() -> status8049.updateState());
            }
        };
        cpu.getPort(0).addListener(portListener);
        cpu.getPort(1).addListener(portListener);
        cpu.getPort(2).addListener(portListener);

        status8155 = new Status8155Composite(composite, SWT.NONE);
        status8155.setText("8155 (Main unit)");
        status8155.setTraceExecution(isTraceExecution());
        status8155.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        status8155.setPID(pid);

        status8155Extension = new Status8155Composite(composite, SWT.NONE);
        status8155Extension.setText("8155 (CP3 memory extension)");
        status8155Extension.setTraceExecution(isTraceExecution());
        status8155Extension.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        status8155Extension.setPID(pidExtension);

        stopAction.setEnabled(false);

        shell.pack();
    }

    public boolean isTraceExecution() {
        return traceExecution;
    }

    public void setTraceExecution(boolean traceExecution) {
        status8049.setTraceExecution(traceExecution);
        status8155.setTraceExecution(traceExecution);
        status8155Extension.setTraceExecution(traceExecution);
        this.traceExecution = traceExecution;
    }

    @Override
    public void executionStarted() {
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                singleStepAction.setEnabled(false);
                stopAction.setEnabled(true);
                runAction.setEnabled(false);
            }});
    }

    @Override
    public void executionStopped() {
        updateView();
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                singleStepAction.setEnabled(true);
                stopAction.setEnabled(false);
                runAction.setEnabled(true);
                status8049.updateState();
                status8049.redraw();
                status8155.updateState();
                status8155.redraw();
                status8155Extension.updateState();
                status8155Extension.redraw();
            }});
    }

    @Override
    public void performanceUpdate(double performance) {
    }

    @Override
    public void instructionExecuted() {
        if (!isTraceExecution()) {
            return;
        }
        updateView();
        shell.getDisplay().asyncExec(() -> {
            status8049.updateState();
        });
        update8155States();
    }

    @Override
    public void commandRegisterWritten() {
        update8155States();
    }

    @Override
    public void portWritten(Port port, int value) {
        update8155States();
    }

    @Override
    public void memoryWritten() {
        update8155States();
    }

    @Override
    public void pinsChanged() {
        update8155States();
    }

    private void update8155States() {
        if (!isTraceExecution()) {
            return;
        }
        shell.getDisplay().asyncExec(() -> {
            status8155.updateState();
            status8155Extension.updateState();
        });
    }

    @Override
    public void resetExecuted() {
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                singleStepAction.setEnabled(true);
                stopAction.setEnabled(false);
                runAction.setEnabled(true);
            }});
        updateView();
        shell.getDisplay().asyncExec(() -> {
            status8049.updateState();
            status8049.redraw();
        });
        update8155States();
    }

    @Override
    public void stateChanged(Intel8049.State newState) {
        if (isTraceExecution()) {
            if (disassembly.getSelectedAddress() != cpu.getPC()) {
                updateView();
            }
            shell.getDisplay().asyncExec(status8049::updateState);
        }
    }

    @Override
    public void breakpointHit(int addr) {
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                status8049.updateState();
                status8049.redraw();
            }});
        update8155States();
        updateView();
    }

    public void updateView() {
        shell.getDisplay().syncExec(() -> disassembly.selectAddress(cpu.getPC()));
    }

    /**
     * @wbp.parser.entryPoint
     */
    private static void wbpEntryPoint() {
        try {
            Ram ram = new Ram(256);
            Rom rom = new Rom(new FileInputStream("CP1.bin"));
            DataPort bus = new DataPort("BUS");
            DataPort p1 = new DataPort("P1");
            DataPort p2 = new DataPort("P2");
            Intel8049 cpu = new Intel8049(rom, bus, p1, p2);
            Intel8155 pid = new Intel8155("internal", bus);
            Intel8155 pidExtension = new Intel8155("extension", bus);

            ExecutorThread executorThread = new ExecutorThread(cpu, pid, pidExtension);
            CpuWindow cpuWindow = new CpuWindow(cpu, pid, pidExtension, executorThread);
            executorThread.start();
            cpuWindow.open();
            Display display = Display.getDefault();
            while (!cpuWindow.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

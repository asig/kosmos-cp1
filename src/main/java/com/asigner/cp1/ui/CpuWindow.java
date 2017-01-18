package com.asigner.cp1.ui;

import com.asigner.cp1.emulation.DataPort;
import com.asigner.cp1.emulation.DataPortListener;
import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;
import com.asigner.cp1.ui.actions.AboutAction;
import com.asigner.cp1.ui.actions.BreakOnMovxAction;
import com.asigner.cp1.ui.actions.LoadStateAction;
import com.asigner.cp1.ui.actions.ResetAction;
import com.asigner.cp1.ui.actions.RunAction;
import com.asigner.cp1.ui.actions.SaveDisassemblyAction;
import com.asigner.cp1.ui.actions.SaveStateAction;
import com.asigner.cp1.ui.actions.SingleStepAction;
import com.asigner.cp1.ui.actions.StopAction;
import com.asigner.cp1.ui.actions.TraceExecutionAction;
import com.asigner.cp1.ui.widgets.ActionMenuItem;
import com.asigner.cp1.ui.widgets.ActionToolItem;
import com.asigner.cp1.ui.widgets.BitsetWidget;
import com.asigner.cp1.ui.widgets.CheckboxToolItem;
import com.asigner.cp1.ui.widgets.DisassemblyComposite;
import com.asigner.cp1.ui.widgets.MemoryComposite;
import com.asigner.cp1.ui.widgets.Status8049Composite;
import com.asigner.cp1.ui.widgets.Status8155Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.FileInputStream;
import java.io.IOException;

public class CpuWindow implements ExecutionListener, Intel8049.StateListener {

    private RunAction runAction;
    private StopAction stopAction;
    private SingleStepAction singleStepAction;
    private ResetAction resetAction;
    private BreakOnMovxAction breakOnMovxAction;
    private TraceExecutionAction traceExecutionAction;
    private SaveDisassemblyAction saveDisassemblyAction;
    private LoadStateAction loadStateAction;
    private SaveStateAction saveStateAction;
    private AboutAction aboutAction;

    private Shell shell;
    private Intel8049 cpu;
    private Intel8155 pid;
    private ExecutorThread executorThread;
    private boolean traceExecution = true;

    private Status8049Composite status8049Composite;
    private Status8155Composite status8155Composite;
    private DisassemblyComposite disassemblyComposite;
    private MemoryComposite memory8049Composite;
    private MemoryComposite memory8155Composite;

    private BitsetWidget busWidget;
    private BitsetWidget p1Widget;
    private BitsetWidget p2Widget;

    public CpuWindow(Intel8049 cpu, Intel8155 pid, ExecutorThread executorThread) throws IOException {
        this.cpu = cpu;
        this.pid = pid;
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
        singleStepAction = new SingleStepAction(executorThread);
        saveDisassemblyAction = new SaveDisassemblyAction(cpu);
        loadStateAction = new LoadStateAction(cpu);
        saveStateAction = new SaveStateAction(cpu);
        aboutAction = new AboutAction();

        resetAction.setDependentActions(singleStepAction, runAction, stopAction);
        runAction.setDependentActions(singleStepAction, stopAction);
        stopAction.setDependentActions(singleStepAction, runAction);
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
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
        new ActionMenuItem(fileMenu, SWT.NONE, saveDisassemblyAction);
        new MenuItem(fileMenu, SWT.SEPARATOR);
        new ActionMenuItem(fileMenu, SWT.NONE, loadStateAction);
        new ActionMenuItem(fileMenu, SWT.NONE, saveStateAction);

        Menu helpMenu = new Menu(menu);
        new ActionMenuItem(helpMenu, SWT.NONE, aboutAction);

        MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
        fileItem.setText("&File");
        fileItem.setMenu(fileMenu);
        MenuItem helpItem = new MenuItem(menu, SWT.CASCADE);
        helpItem.setText("Help");
        helpItem.setMenu(helpMenu);

        return menu;
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setText("Intel MCS-48 Emulator");
        shell.setLayout(new GridLayout(1, false));

        ToolBar toolbar = new ToolBar(shell, SWT.FLAT);
        ToolItem toolItem1 = new ActionToolItem(toolbar, SWT.PUSH, singleStepAction);
        ToolItem toolItem2 = new ActionToolItem(toolbar, SWT.PUSH, runAction);
        ToolItem toolItem3 = new ActionToolItem(toolbar, SWT.PUSH, stopAction);
        ToolItem toolItem4 = new ActionToolItem(toolbar, SWT.PUSH, resetAction);

        ToolItem toolItem5 = new CheckboxToolItem(toolbar, breakOnMovxAction);
        ToolItem toolItem6 = new CheckboxToolItem(toolbar, traceExecutionAction);
//        Button ctrl = new Button(toolbar, SWT.CHECK);
//        ctrl.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
//        ctrl.setText("Break on MOVX");
//        ctrl.pack();
//        toolItem5.setControl(ctrl);
//        toolItem5.setWidth(ctrl.computeSize(-1, -1).x);

        Composite composite_1 = new Composite(shell, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite_1.setLayout(new GridLayout(2, false));

        Group group = new Group(composite_1, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        group.setLayout(new FillLayout(SWT.HORIZONTAL));
        group.setText("Disassembly");

        disassemblyComposite = new DisassemblyComposite(group, SWT.NONE);
        disassemblyComposite.setRom(cpu.getRom());
        disassemblyComposite.addListener(new BreakpointChangedListener() {
            @Override
            public void breakpointChanged(int addr, boolean enabled) {
                executorThread.enableBreakpoint(addr, enabled);
            }});

        Composite composite = new Composite(composite_1, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        Group group_1 = new Group(composite, SWT.NONE);
        group_1.setText("Status (8049)");
        group_1.setLayout(new GridLayout(3, false));
        group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        status8049Composite = new Status8049Composite(group_1, SWT.NONE);
        status8049Composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4));
        status8049Composite.setCpu(cpu);

        DataPort bus = cpu.getPort(0);
        new Label(group_1, SWT.NONE).setText("BUS");
        busWidget = new BitsetWidget(group_1, 8, SWT.NONE);
        bus.addListener(new DataPortListener() {
            @Override
            public void valueChanged(int oldValue, final int newValue) {
                shell.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() { busWidget.setValue(newValue); }
                });
            }});

        DataPort p1 = cpu.getPort(1);
        new Label(group_1, SWT.NONE).setText("P1");
        p1Widget = new BitsetWidget(group_1, 8, SWT.NONE);
        p1.addListener(new DataPortListener() {
            @Override
            public void valueChanged(int oldValue, final int newValue) {
                shell.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() { p1Widget.setValue(newValue); }
                });
            }});

        DataPort p2 = cpu.getPort(2);
        new Label(group_1, SWT.NONE).setText("P2");
        p2Widget = new BitsetWidget(group_1, 8, SWT.NONE);
        new Label(group_1, SWT.NONE);
        new Label(group_1, SWT.NONE);
        p2.addListener(new DataPortListener() {
            @Override
            public void valueChanged(int oldValue, final int newValue) {
                shell.getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() { p2Widget.setValue(newValue); }
                });
            }});


        Group grpMemory = new Group(composite, SWT.NONE);
        grpMemory.setLayout(new FillLayout(SWT.HORIZONTAL));
        grpMemory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        grpMemory.setText("Memory (8049)");

        memory8049Composite = new MemoryComposite(grpMemory, SWT.NONE);
        memory8049Composite.setRam(cpu.getRam());

        Group group_2 = new Group(composite, SWT.NONE);
        group_2.setText("Status (8155)");
        group_2.setLayout(new GridLayout(1, false));
        group_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        status8155Composite = new Status8155Composite(group_2, SWT.NONE);
        status8155Composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        status8155Composite.setPID(pid);

        Group grpMemory8155 = new Group(composite, SWT.NONE);
        grpMemory8155.setLayout(new FillLayout(SWT.HORIZONTAL));
        grpMemory8155.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        grpMemory8155.setText("Memory (8155)");

        memory8155Composite = new MemoryComposite(grpMemory8155, SWT.NONE);
        memory8155Composite.setRam(pid.getRam());

        stopAction.setEnabled(false);

        shell.pack();
    }

    public boolean isTraceExecution() {
        return traceExecution;
    }

    public void setTraceExecution(boolean traceExecution) {
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
                memory8049Composite.redraw();
                memory8155Composite.redraw();
                status8155Composite.redraw();
                busWidget.redraw();
                p1Widget.redraw();
                p2Widget.redraw();
            }});
    }


    @Override
    public void instructionExecuted() {
        if (isTraceExecution()) {
            updateView();
        }
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
    }

    @Override
    public void stateChanged(Intel8049.State newState) {
        if (disassemblyComposite.getSelectedAddress() != cpu.getPC()) {
            updateView();
        }
    }

    @Override
    public void breakpointHit(int addr) {
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                memory8049Composite.redraw();
                memory8155Composite.redraw();
            }});
        updateView();
    }

    public void updateView() {
        shell.getDisplay().syncExec(() -> disassemblyComposite.selectAddress(cpu.getPC()));
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
            Intel8049 cpu = new Intel8049(ram, rom, bus, p1, p2);
            Intel8155 pid = new Intel8155(bus, new Ram(256));

            ExecutorThread executorThread = new ExecutorThread(cpu, pid);
            CpuWindow cpuWindow = new CpuWindow(
                    new Intel8049(ram, rom, bus, p1, p2),
                    new Intel8155(bus, new Ram(256)),
                    executorThread
            );
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

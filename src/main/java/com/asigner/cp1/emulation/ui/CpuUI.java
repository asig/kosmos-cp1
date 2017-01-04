package com.asigner.cp1.emulation.ui;

import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.asigner.cp1.emulation.Cpu;
import com.asigner.cp1.emulation.DataPort;
import com.asigner.cp1.emulation.DataPortListener;
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;
import com.asigner.cp1.emulation.ui.actions.AboutAction;
import com.asigner.cp1.emulation.ui.actions.LoadStateAction;
import com.asigner.cp1.emulation.ui.actions.ResetAction;
import com.asigner.cp1.emulation.ui.actions.RunAction;
import com.asigner.cp1.emulation.ui.actions.SaveDisassemblyAction;
import com.asigner.cp1.emulation.ui.actions.SaveStateAction;
import com.asigner.cp1.emulation.ui.actions.SingleStepAction;
import com.asigner.cp1.emulation.ui.actions.StopAction;
import com.asigner.cp1.emulation.ui.widgets.ActionButton;
import com.asigner.cp1.emulation.ui.widgets.ActionMenuItem;
import com.asigner.cp1.emulation.ui.widgets.ActionToolItem;
import com.asigner.cp1.emulation.ui.widgets.BitsetWidget;
import com.asigner.cp1.emulation.ui.widgets.DisassemblyComposite;
import com.asigner.cp1.emulation.ui.widgets.MemoryComposite;
import com.asigner.cp1.emulation.ui.widgets.StatusComposite;

public class CpuUI implements ExecutionListener {

    private RunAction runAction;
    private StopAction stopAction;
    private SingleStepAction singleStepAction;
    private ResetAction resetAction;
    private SaveDisassemblyAction saveDisassemblyAction;
    private LoadStateAction loadStateAction;
    private SaveStateAction saveStateAction;
    private AboutAction aboutAction;

    protected Shell shell;
    private Cpu cpu;
    private ExecutorThread executorThread;

    private StatusComposite statusComposite;
    private DisassemblyComposite disassemblyComposite;
    private MemoryComposite memoryComposite;

    private BitsetWidget busWidget;
    private BitsetWidget p1Widget;
    private BitsetWidget p2Widget;

    public CpuUI(Cpu cpu) throws IOException {
        this.cpu = cpu;

        executorThread = new ExecutorThread(cpu);
        executorThread.addListener(this);
        executorThread.start();       
    }


    private void createActions() {
        resetAction = new ResetAction(executorThread);
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
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
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
        shell.setSize(620, 477);
        shell.setText("Intel MCS-48 Emulator");
        shell.setLayout(new GridLayout(1, false));

        ToolBar toolbar = new ToolBar(shell, SWT.FLAT);
        ToolItem toolItem1 = new ActionToolItem(toolbar, SWT.PUSH, singleStepAction);
        ToolItem toolItem2 = new ActionToolItem(toolbar, SWT.PUSH, runAction);
        ToolItem toolItem3 = new ActionToolItem(toolbar, SWT.PUSH, stopAction);
        ToolItem toolItem4 = new ActionToolItem(toolbar, SWT.PUSH, resetAction);

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
                                GridLayout layout = new GridLayout(1, false);
                                layout.marginWidth = 0;
                                layout.marginHeight = 0;
                                composite.setLayout(layout);

                                        Group group_1 = new Group(composite, SWT.NONE);
                                        group_1.setText("Status");
                                        group_1.setLayout(new GridLayout(3, false));
                                        group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

                                                statusComposite = new StatusComposite(group_1, SWT.NONE);
                                                statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4));

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
                                        grpMemory.setText("Memory");

                                                memoryComposite = new MemoryComposite(grpMemory, SWT.NONE);
                                                memoryComposite.setRam(cpu.getRam());

                                        Group grpCommands = new Group(composite, SWT.NONE);
                                        grpCommands.setLayout(new RowLayout(SWT.HORIZONTAL));
                                        grpCommands.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
                                        grpCommands.setText("Commands");

                                                Button btnStep = new ActionButton(grpCommands, SWT.NONE, singleStepAction);
                                                Button btnRun = new ActionButton(grpCommands, SWT.NONE, runAction);
                                                Button btnStop = new ActionButton(grpCommands, SWT.NONE, stopAction);
                                                Button btnReset = new ActionButton(grpCommands, SWT.NONE, resetAction);
                                                stopAction.setEnabled(false);
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
                memoryComposite.redraw();
                busWidget.redraw();
                p1Widget.redraw();
                p2Widget.redraw();
            }});
    }

    @Override
    public void singleStepped() {
        updateView();
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
    public void breakpointHit(int addr) {
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                memoryComposite.redraw();
            }});
        updateView();
    }

    private final Runnable updateViewRunnable = new Runnable() {
        @Override
        public void run() {
            statusComposite.setA(cpu.getA());
            statusComposite.setT(cpu.getT());
            statusComposite.setF1(cpu.getF1());
            statusComposite.setPsw(cpu.getPSW());
            statusComposite.setPc(cpu.getPC());

            disassemblyComposite.selectAddress(cpu.getPC());
        }
    };

    public void updateView() {
        shell.getDisplay().syncExec(updateViewRunnable);
    }

    /**
     * @wbp.parser.entryPoint
     */
    private static void wbpEntryPoint() {
        try {
            Ram ram = new Ram(256);
            Rom rom = new Rom(new FileInputStream("CP1.bin"));
            CpuUI cpuUI = new CpuUI(new Cpu(ram, rom, new DataPort("BUS"), new DataPort("P1"), new DataPort("P2")));
            cpuUI.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

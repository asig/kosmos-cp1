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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.asigner.cp1.emulation.Cpu;
import com.asigner.cp1.emulation.CpuListener;
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;
import com.asigner.cp1.emulation.ui.actions.ResetAction;
import com.asigner.cp1.emulation.ui.actions.RunAction;
import com.asigner.cp1.emulation.ui.actions.SingleStepAction;
import com.asigner.cp1.emulation.ui.actions.StopAction;

public class CpuUI implements CpuListener, BreakpointHitListener {

    private RunAction runAction;
    private StopAction stopAction;
    private SingleStepAction singleStepAction;
    private ResetAction resetAction;

    protected Shell shell;
    private Cpu cpu;
    private ExecutorThread executorThread;

    private StatusComposite statusComposite;
    private DisassemblyComposite disassemblyComposite;
    private MemoryComposite memoryComposite;
    private Button btnRun;
    private Button btnStop;
    private Button btnStep;

    public CpuUI(Cpu cpu) throws IOException {
        this.cpu = cpu;
        this.cpu.addListener(this);

        executorThread = new ExecutorThread(cpu);
        executorThread.addListener(this);
        executorThread.start();

        resetAction = new ResetAction(executorThread);
        runAction = new RunAction(executorThread);
        stopAction = new StopAction(executorThread, this);
        singleStepAction = new SingleStepAction(executorThread);

        resetAction.setDependentActions(singleStepAction, runAction, stopAction);
        runAction.setDependentActions(singleStepAction, stopAction);
        stopAction.setDependentActions(singleStepAction, runAction);
    }


    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(620, 400);
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
                group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
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

                                        statusComposite = new StatusComposite(composite, SWT.NONE);
                                        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

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

                                                btnStep = new ActionButton(grpCommands, SWT.NONE, singleStepAction);
                                                btnRun = new ActionButton(grpCommands, SWT.NONE, runAction);
                                                btnStop = new ActionButton(grpCommands, SWT.NONE, stopAction);
                                                Button btnReset = new ActionButton(grpCommands, SWT.NONE, resetAction);
                                                stopAction.setEnabled(false);
    }

    @Override
    public void instructionExecuted() {
        updateView();
    }

    @Override
    public void cpuReset() {
        updateView();
    }

    @Override
    public void breakpointHit(int addr) {
        shell.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                btnRun.setEnabled(true);
                btnStep.setEnabled(true);
                btnStop.setEnabled(false);
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
            memoryComposite.redraw();
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
            CpuUI cpuUI = new CpuUI(new Cpu(ram, rom));
            cpuUI.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

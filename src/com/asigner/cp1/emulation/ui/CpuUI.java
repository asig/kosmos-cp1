package com.asigner.cp1.emulation.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.asigner.cp1.emulation.Cpu;
import com.asigner.cp1.emulation.CpuListener;
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;

public class CpuUI implements CpuListener {

    private final Set<Integer> breakpoints = new HashSet<Integer>();

    protected Shell shell;
    private Cpu cpu;

    private StatusComposite statusComposite;
    private DisassemblyComposite disassemblyComposite;
    private Button btnRun;
    private Button btnStop;
    private Button btnStep;

    public CpuUI(Cpu cpu) throws IOException {
        this.cpu = cpu;
        cpu.addListener(this);
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
     * @wbp.parser.entryPoint
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(620, 400);
        shell.setText("Intel MCS-48 Emulator");
        shell.setLayout(new GridLayout(2, false));

        Group group = new Group(shell, SWT.NONE);
        group.setLayout(new FillLayout(SWT.HORIZONTAL));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        group.setText("Disassembly");

                disassemblyComposite = new DisassemblyComposite(group, SWT.NONE);
                disassemblyComposite.setRom(cpu.getRom());
                disassemblyComposite.addListener(new BreakpointChangedListener() {
                    @Override
                    public void breakpointChanged(int addr, boolean enabled) {
                        if (enabled) {
                            breakpoints.add(addr);
                        } else {
                            breakpoints.remove(addr);
                        }
                    }});

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

                statusComposite = new StatusComposite(composite, SWT.NONE);
                statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

                Group grpMemory = new Group(composite, SWT.NONE);
                grpMemory.setLayout(new FillLayout(SWT.HORIZONTAL));
                grpMemory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
                grpMemory.setText("Memory");

                        MemoryComposite memoryComposite = new MemoryComposite(grpMemory, SWT.NONE);
                        memoryComposite.setRam(cpu.getRam());

                Group grpCommands = new Group(composite, SWT.NONE);
                grpCommands.setLayout(new RowLayout(SWT.HORIZONTAL));
                grpCommands.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
                grpCommands.setText("Commands");

                btnStep = new Button(grpCommands, SWT.NONE);
                btnStep.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        stepClicked();
                    }
                });
                btnStep.setText("Step");

                btnRun = new Button(grpCommands, SWT.NONE);
                btnRun.setText("Run");
                btnRun.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        runClicked();
                    }
                });
                btnRun.setEnabled(true);

                btnStop = new Button(grpCommands, SWT.NONE);
                btnStop.setText("Stop");
                btnStop.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        stopClicked();
                    }
                });
                btnStop.setEnabled(false);

                Button btnReset = new Button(grpCommands, SWT.NONE);
                btnReset.setText("Reset");
                btnReset.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        resetClicked();
                    }
                });
    }

    private void stepClicked() {
        cpu.executeSingleInstr();
    }

    private void runClicked() {
    	btnRun.setEnabled(false);
    	btnStep.setEnabled(false);
    	btnStop.setEnabled(true);
    }

    private void stopClicked() {
    	btnRun.setEnabled(true);
    	btnStep.setEnabled(true);
    	btnStop.setEnabled(false);
    }

    private void resetClicked() {
        cpu.reset();
    }

    public static void main(String ... args) {
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

    @Override
    public void instructionExecuted() {
        updateView();
    }

    @Override
    public void cpuReset() {
        updateView();
    }

    private void updateView() {
        statusComposite.setA(cpu.getA());
        statusComposite.setT(cpu.getT());
        statusComposite.setF1(cpu.getF1());
        statusComposite.setPsw(cpu.getPSW());
        statusComposite.setPc(cpu.getPC());
        disassemblyComposite.selectAddress(cpu.getPC());
    }
}

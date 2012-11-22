package com.asigner.cp1.emulation.ui;

import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.emulation.Rom;

public class CpuUI {

    protected Shell shell;
    private Rom rom;
    private Ram ram;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            CpuUI window = new CpuUI();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CpuUI() throws IOException {
        rom = new Rom(new FileInputStream("CP1.bin"));
        ram = new Ram(128);
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
        shell.setLayout(new GridLayout(2, false));

        Group group = new Group(shell, SWT.NONE);
        group.setLayout(new FillLayout(SWT.HORIZONTAL));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        group.setText("Disassembly");

                DisassemblyComposite disassemblyComposite = new DisassemblyComposite(group, SWT.NONE);
                disassemblyComposite.setRom(rom);

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        composite.setLayout(new GridLayout(1, false));

                StatusComposite statusComposite = new StatusComposite(composite, SWT.NONE);
                statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

                Group grpMemory = new Group(composite, SWT.NONE);
                grpMemory.setLayout(new FillLayout(SWT.HORIZONTAL));
                grpMemory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
                grpMemory.setText("Memory");

                        MemoryComposite memoryComposite = new MemoryComposite(grpMemory, SWT.NONE);
                        memoryComposite.setRam(ram);

    }
}

package com.asigner.cp1.ui;

import com.asigner.cp1.assembler.Assembler;
import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.emulation.Ram;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class AssemblerWindow extends Window {

    public static final String NAME = "Assembler";

    public static class SampleCode {
        private final String name;
        private final List<String> code;

        public SampleCode(String name, List<String> code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code.stream().collect(joining("\n"));
        }
    }

    private final Intel8155 pid;
    private final Intel8155 pidExtension;
    private final ExecutorThread executor;
    private final List<SampleCode> sampleListings;

    private Shell shell;

    private StyledText src;
    private Text results;


    public AssemblerWindow(WindowManager windowManager, Intel8155 pid, Intel8155 pidExtension, ExecutorThread executor) {
        super(windowManager, NAME);
        this.pid = pid;
        this.pidExtension = pidExtension;
        this.executor = executor;

        this.sampleListings = Lists.newArrayListWithCapacity(100);
        for (int i = 0; i < 100; i++) {
            InputStream is = this.getClass().getResourceAsStream(String.format("/com/asigner/cp1/listings/listing%d.asm", i));
            if (is != null) {
                try {
                    List<String> text = IOUtils.readLines(is, "UTF-8");
                    String name = text.get(0).substring(1).trim();
                    sampleListings.add(new AssemblerWindow.SampleCode(name, text));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Open the dialog.
     * @return the result
     */
    public void open() {
        createContents();

        shell.setMenuBar(createMenuBar());
        shell.open();
        shell.layout();
        fireWindowOpened();
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        Display display = Display.getDefault();
        shell = new Shell(display, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.SHELL_TRIM);
        shell.setText("Assembler");
        shell.setSize(552, 592);
        shell.setLayout(new GridLayout(2, false));
        shell.addDisposeListener(disposeEvent -> fireWindowClosed());

        Label lblNewLabel_2 = new Label(shell, SWT.NONE);
        lblNewLabel_2.setText("Sample Listings:");

        Combo sampleListingsCombo = new Combo(shell, SWT.READ_ONLY);
        sampleListingsCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        for (SampleCode code : sampleListings) {
            sampleListingsCombo.add(code.getName());
        }
        sampleListingsCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selected = sampleListingsCombo.getSelectionIndex();
                SampleCode sampleCode = sampleListings.get(selected);
                src.setText(sampleCode.getCode());
            }
        });

        Label lblNewLabel_1 = new Label(shell, SWT.NONE);
        lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        lblNewLabel_1.setText("Source");

        src = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        src.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        src.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        lblNewLabel.setText("Results");

        results = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
        GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        gd_text.heightHint = 150;
        results.setLayoutData(gd_text);

        Button btnAssemble = new Button(shell, SWT.NONE);
        btnAssemble.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
        btnAssemble.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnAssemble.setText("Assemble");
        btnAssemble.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                assemble();
            }
        });
    }

    private void assemble() {
        Assembler assembler = new Assembler(src.getText());
        assembler.assemble();
        List<String> errors = assembler.getErrors();
        if (errors.size() == 0) {
            results.setText("Assembly succeeded.");
            byte[] code = assembler.getCode();
            boolean running = executor.isRunning();
            if (running) {
                executor.postCommand(ExecutorThread.Command.STOP);
            }
            Ram ram = pid.getRam();
            for (int i = 0; i < 256; i++) {
                ram.write(i, code[i]);
            }
            if (code.length > 256) {
                ram = pidExtension.getRam();
                for (int i = 0; i < 256; i++) {
                    ram.write(i, code[256 + i]);
                }
            }
            if (running) {
                executor.postCommand(ExecutorThread.Command.START);
            }
        } else {
            results.setText(errors.stream().collect(joining("\n")));
        }
    }

    private Menu createMenuBar() {
        Menu menu = new Menu(shell, SWT.BAR);
        createFileMenu(menu);
        createWindowMenu(menu);
        createHelpMenu(menu);
        return menu;
    }
}

package com.asigner.cp1.ui;

import com.asigner.cp1.assembler.Assembler;
import com.google.common.collect.Lists;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class AssemblerDialog extends Dialog {

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

    public interface ResultListener {
        void codeAssembled(byte[] code);
    }

    private Shell shell;

    private StyledText src;
    private Text results;

    private ResultListener resultListener;
    private List<SampleCode> sampleListings = Lists.newArrayList();

    /**
     * Create the dialog.
     * @param parent
     */
    public AssemblerDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.SHELL_TRIM);
        setText("Assembler");
    }

    public void setSampleListings(List<SampleCode> sampleListings) {
        this.sampleListings = sampleListings;
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    /**
     * Open the dialog.
     * @return the result
     */
    public void open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), getStyle());
        shell.setSize(552, 592);
        shell.setText(getText());
        shell.setLayout(new GridLayout(2, false));

        Label lblNewLabel_2 = new Label(shell, SWT.NONE);
        lblNewLabel_2.setText("Sample Listings:");

        Combo sampleListingsCombo = new Combo(shell, SWT.NONE);
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
            if (resultListener != null) {
                resultListener.codeAssembled(assembler.getCode());
            }
        } else {
            results.setText(errors.stream().collect(joining("\n")));
        }
    }

}

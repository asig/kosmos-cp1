package com.asigner.cp1.ui;

import com.asigner.cp1.assembler.Assembler;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class AssemblerDialog extends Dialog {

    public interface ResultListener {
        void codeAssembled(byte[] code);
    }

    private Shell shell;

    private StyledText src;
    private Text results;

    private ResultListener resultListener;

    /**
     * Create the dialog.
     * @param parent
     */
    public AssemblerDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.SHELL_TRIM);
        setText("Assembler");
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
        shell.setLayout(new GridLayout(1, false));

        Label lblNewLabel_1 = new Label(shell, SWT.NONE);
        lblNewLabel_1.setText("Source");

        src = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        src.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        src.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));


        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setText("Results");

        results = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
        GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd_text.heightHint = 150;
        results.setLayoutData(gd_text);

        Button btnAssemble = new Button(shell, SWT.NONE);
        btnAssemble.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
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

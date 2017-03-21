package com.asigner.cp1.ui;

import com.asigner.cp1.BuildInfo;
import com.asigner.cp1.BuildInfoImpl;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

    protected Object result;
    protected Shell shell;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public AboutDialog(Shell parent, int style) {
        super(parent, style);
        setText("SWT Dialog");
    }

    /**
     * Open the dialog.
     * @return the result
     */
    public Object open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), getStyle());
        shell.setSize(450, 300);
        shell.setText(getText());
        shell.setLayout(new GridLayout(2, false));

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
        lblNewLabel.setImage(SWTResources.getImage("/com/asigner/cp1/ui/about.png"));

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true,  true).create());
        composite.setLayout(new GridLayout(1, false));

        BuildInfo buildInfo = new BuildInfoImpl(); BuildInfo.create();

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("Kosmos CP1 Emulator");
        }

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("Version " + buildInfo.getVersion());
        }

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("Built on " + buildInfo.getBuildTime());
        }

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("");
        }

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("© 2017 Andreas Signer <asigner@gmail.com>");
        }

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("This software is published under GPLv3.");
        }

        {
            Button okBtn = new Button(shell, SWT.NONE);
            okBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
            okBtn.setText("OK");
        }
    }
}

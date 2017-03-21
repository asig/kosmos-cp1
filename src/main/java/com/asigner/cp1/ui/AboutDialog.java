package com.asigner.cp1.ui;

import com.asigner.cp1.BuildInfo;
import com.asigner.cp1.BuildInfoImpl;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class AboutDialog extends Dialog {

    protected Object result;
    protected Shell shlAbout;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public AboutDialog(Shell parent, int style) {
        super(parent, style);
        setText("About...");
    }

    /**
     * Open the dialog.
     * @return the result
     */
    public Object open() {
        createContents();
        shlAbout.pack();
        shlAbout.open();
        Display display = getParent().getDisplay();
        while (!shlAbout.isDisposed()) {
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
        shlAbout = new Shell(getParent(), SWT.DIALOG_TRIM);
        GridLayout gl_shlAbout = new GridLayout(2, false);
        gl_shlAbout.verticalSpacing = 20;
        gl_shlAbout.marginHeight = 20;
        gl_shlAbout.marginWidth = 20;
        gl_shlAbout.horizontalSpacing = 20;
        gl_shlAbout.marginRight = 20;
        gl_shlAbout.marginLeft = 20;
        shlAbout.setLayout(gl_shlAbout);

        Label lblNewLabel = new Label(shlAbout, SWT.CENTER);
        lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setImage(SWTResources.getImage("/com/asigner/cp1/ui/about.png"));

        Composite composite = new Composite(shlAbout, SWT.NONE);
        GridData gd_composite = GridDataFactory.fillDefaults().grab(true,  true).create();
        gd_composite.verticalAlignment = SWT.CENTER;
        gd_composite.grabExcessVerticalSpace = false;
        composite.setLayoutData(gd_composite);
        composite.setLayout(new GridLayout(1, false));

        BuildInfo buildInfo = new BuildInfoImpl(); //BuildInfo.create();

        {
            Label lbl = new Label(composite, SWT.CENTER);
            lbl.setFont(SWTResources.getFont("Montserrat", 24, true));
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("Kosmos CP1 Emulator");
        }

        {
            Label lbl = new Label(composite, SWT.CENTER);
            lbl.setFont(SWTResources.getFont("Ubuntu", 16, true));
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("Version " + buildInfo.getVersion());
        }

        {
            Label lbl = new Label(composite, SWT.SHADOW_NONE | SWT.CENTER);
            lbl.setFont(SWTResources.getFont("Ubuntu", 11, false));
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("Built on " + buildInfo.getBuildTime());
        }

        {
            Label lbl = new Label(composite, SWT.NONE);
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("");
        }

        {
            Label lbl = new Label(composite, SWT.CENTER);
            lbl.setFont(SWTResources.getFont("Ubuntu", 11, false));
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("© 2017 Andreas Signer <asigner@gmail.com>");
        }

        {
            Label lbl = new Label(composite, SWT.CENTER);
            lbl.setFont(SWTResources.getFont("Ubuntu", 11, false));
            lbl.setLayoutData(GridDataFactory.fillDefaults().grab(true,  false).create());
            lbl.setText("This software is published under GPLv3.");
        }

        {
            Button okBtn = new Button(shlAbout, SWT.NONE);
            GridData gd_okBtn = new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 2, 1);
            gd_okBtn.minimumWidth = 60;
            okBtn.setLayoutData(gd_okBtn);
            okBtn.setText("OK");
            okBtn.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    shlAbout.dispose();
                }
            });
        }
    }
}

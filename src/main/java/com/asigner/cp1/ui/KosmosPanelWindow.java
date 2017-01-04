// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.asigner.cp1.ui.widgets.KosmosControlPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import com.asigner.cp1.ui.widgets.CP1Display;
import org.eclipse.swt.widgets.Label;
import com.asigner.cp1.ui.widgets.KosmosLogoComposite;

public class KosmosPanelWindow {


    protected Shell shell;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            KosmosPanelWindow window = new KosmosPanelWindow();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        shell.pack();
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
        shell.setText("Kosmos CP1");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite composite = new Composite(shell, SWT.NONE);
        GridLayout gl_composite = new GridLayout(1, false);
        gl_composite.marginTop = 50;
        gl_composite.marginBottom = 50;
        gl_composite.marginRight = 50;
        gl_composite.marginLeft = 50;
        composite.setLayout(gl_composite);
        composite.setBackground(CP1Colors.GREEN);

        Composite composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setBackground(CP1Colors.GREEN);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        GridLayout gl_composite_1 = new GridLayout(2, false);
        gl_composite_1.horizontalSpacing = 0;
        gl_composite_1.verticalSpacing = 0;
        gl_composite_1.marginWidth = 0;
        gl_composite_1.marginHeight = 0;
        composite_1.setLayout(gl_composite_1);
        
        CP1Display p1Display = new CP1Display(composite_1, SWT.NONE);
        GridData gd_p1Display = GridDataFactory.swtDefaults().hint(-1, 80).create();
        gd_p1Display.heightHint = 100;
        p1Display.setLayoutData(gd_p1Display);
        
        KosmosLogoComposite kosmosLogo = new KosmosLogoComposite(composite_1, SWT.NONE);
        GridData gd_kosmosLogo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
        gd_kosmosLogo.heightHint = 100;
        kosmosLogo.setLayoutData(gd_kosmosLogo);
        p1Display.display("C12127");

        Label lblNewLabel = new Label(composite, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblNewLabel.heightHint = 50;
        lblNewLabel.setLayoutData(gd_lblNewLabel);
        lblNewLabel.setText("");

        KosmosControlPanel kosmosControlPanel = new KosmosControlPanel(composite, SWT.NONE);
    }
}

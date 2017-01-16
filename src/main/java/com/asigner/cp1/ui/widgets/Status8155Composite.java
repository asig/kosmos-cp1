// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.emulation.Intel8155;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;

public class Status8155Composite extends Composite implements Intel8155.StateListener {
    private CLabel lblPaMode;
    private CLabel lblPaVal;
    private Button btnPaInterruptEnabled;
    private BitsetWidget bitsetPaVal;


    private CLabel lblPbMode;
    private CLabel lblPbVal;
    private Button btnPbInterruptEnabled;
    private BitsetWidget bitsetPbVal;

    private CLabel lblPcMode;
    private CLabel lblPcVal;
    private BitsetWidget bitsetPcVal;
    private BitsetWidget bitsetCE;
    private BitsetWidget bitsetIO;
    private BitsetWidget bitsetALE;
    private BitsetWidget bitsetRD;
    private BitsetWidget bitsetWR;

    private Intel8155 pid;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public Status8155Composite(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(10, false));

        {
            Group grp = new Group(this, SWT.NONE);
            grp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 10, 1));
            grp.setText("Port A");
            grp.setLayout(new GridLayout(3, false));

            Label lblNewLabel = new Label(grp, SWT.NONE);
            lblNewLabel.setText("Mode:");

            lblPaMode = new CLabel(grp, SWT.BORDER);
            lblPaMode.setText("OUTPUT");

            btnPaInterruptEnabled = new Button(grp, SWT.CHECK);
            btnPaInterruptEnabled.setEnabled(false);
            btnPaInterruptEnabled.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            btnPaInterruptEnabled.setText("Interrupt Enabled");

            Label l2 = new Label(grp, SWT.NONE);
            l2.setText("Value:");

            lblPaVal = new CLabel(grp, SWT.BORDER);
            lblPaVal.setText("0xFF");

            bitsetPaVal = new BitsetWidget(grp, 8, SWT.NONE);
        }

        {
            Group grp = new Group(this, SWT.NONE);
            grp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 10, 1));
            grp.setText("Port B");
            grp.setLayout(new GridLayout(3, false));

            Label lblNewLabel = new Label(grp, SWT.NONE);
            lblNewLabel.setText("Mode:");

            lblPbMode = new CLabel(grp, SWT.BORDER);
            lblPbMode.setText("OUTPUT");

            btnPbInterruptEnabled = new Button(grp, SWT.CHECK);
            btnPbInterruptEnabled.setEnabled(false);
            btnPbInterruptEnabled.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            btnPbInterruptEnabled.setText("Interrupt Enabled");

            Label l2 = new Label(grp, SWT.NONE);
            l2.setText("Value:");

            lblPbVal = new CLabel(grp, SWT.BORDER);
            lblPbVal.setText("0xFF");

            bitsetPbVal = new BitsetWidget(grp, 8, SWT.NONE);
        }

        {
            Group grp = new Group(this, SWT.NONE);
            grp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 10, 1));
            grp.setText("Port C");
            grp.setLayout(new GridLayout(3, false));

            Label lblNewLabel = new Label(grp, SWT.NONE);
            lblNewLabel.setText("Mode:");

            lblPcMode = new CLabel(grp, SWT.BORDER);
            lblPcMode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
            lblPcMode.setText("ALT1");

            Label l2 = new Label(grp, SWT.NONE);
            l2.setText("Value:");

            lblPcVal = new CLabel(grp, SWT.BORDER);
            lblPcVal.setText("0xFF");

            bitsetPcVal = new BitsetWidget(grp, 6, SWT.NONE);
        }

        Label lblCE = new Label(this, SWT.CHECK);
        lblCE.setText("/CE");
        bitsetCE = new BitsetWidget(this, 1, SWT.NONE);
        bitsetCE.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Label lblIO = new Label(this, SWT.CHECK);
        lblIO.setText("IO");
        bitsetIO = new BitsetWidget(this, 1, SWT.NONE);
        bitsetIO.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Label lblALE = new Label(this, SWT.CHECK);
        lblALE.setText("ALE");
        bitsetALE = new BitsetWidget(this, 1, SWT.NONE);
        bitsetALE.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Label lblRD = new Label(this, SWT.CHECK);
        lblRD.setText("/RD");
        bitsetRD = new BitsetWidget(this, 1, SWT.NONE);
        bitsetRD.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Label lblWR = new Label(this, SWT.CHECK);
        lblWR.setText("/WR");
        bitsetWR = new BitsetWidget(this, 1, SWT.NONE);
        bitsetWR.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
    }

    public void setPID(Intel8155 pid) {
        if (this.pid != null) {
            this.pid.removeListener(this);
        }
        this.pid = pid;
        this.pid.addListener(this);
        refresh();
    }

    @Override
    public void commandRegisterWritten() {
        getDisplay().asyncExec(this::refresh);
    }

    @Override
    public void portWritten() {
        getDisplay().asyncExec(this::refresh);
    }

    @Override
    public void pinsChanged() {
        getDisplay().asyncExec(this::refresh);
    }

    @Override
    public void memoryWritten() {
    }

    @Override
    public void resetExecuted() {
        getDisplay().asyncExec(this::refresh);
    }

    private void refresh() {
        lblPaMode.setText(pid.getPaMode().toString());
        lblPaVal.setText(String.format("%02x", pid.getPaValue()));
        bitsetPaVal.setValue(pid.getPaValue());
        btnPaInterruptEnabled.setSelection(pid.isPaInterruptEnabled());

        lblPbMode.setText(pid.getPbMode().toString());
        lblPbVal.setText(String.format("%02x", pid.getPbValue()));
        bitsetPbVal.setValue(pid.getPbValue());
        btnPbInterruptEnabled.setSelection(pid.isPbInterruptEnabled());

        lblPcMode.setText(pid.getPcMode().toString());
        lblPcVal.setText(String.format("%02x", pid.getPcValue()));
        bitsetPcVal.setValue(pid.getPcValue());

        bitsetCE.setValue(pid.isCeValue() ? 1 : 0);
        bitsetIO.setValue(pid.isIoValue() ? 1 : 0);
        bitsetALE.setValue(pid.isAleValue() ? 1 : 0);
        bitsetRD.setValue(pid.isRdValue() ? 1 : 0);
        bitsetWR.setValue(pid.isWrValue() ? 1 : 0);
    }

}


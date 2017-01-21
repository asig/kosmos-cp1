/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

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

public class Status8155Composite extends Composite  {
    private CLabel lblPaMode;
    private CLabel lblPaVal;
    private ReadonlyCheckbox btnPaInterruptEnabled;
    private BitsetWidget bitsetPaVal;

    private CLabel lblPbMode;
    private CLabel lblPbVal;
    private ReadonlyCheckbox btnPbInterruptEnabled;
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

            btnPaInterruptEnabled = new ReadonlyCheckbox(grp, SWT.NONE);
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

            btnPbInterruptEnabled = new ReadonlyCheckbox(grp, SWT.NONE);
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
        this.pid = pid;
        updateState();
    }

    public void updateState() {
        lblPaMode.setText(pid.getPaMode().toString());
        lblPaVal.setText(String.format("%02x", pid.getPaValue()));
        bitsetPaVal.setValue(pid.getPaValue());
        btnPaInterruptEnabled.setChecked(pid.isPaInterruptEnabled());

        lblPbMode.setText(pid.getPbMode().toString());
        lblPbVal.setText(String.format("%02x", pid.getPbValue()));
        bitsetPbVal.setValue(pid.getPbValue());
        btnPbInterruptEnabled.setChecked(pid.isPbInterruptEnabled());

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


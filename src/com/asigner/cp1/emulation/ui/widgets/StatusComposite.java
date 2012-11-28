package com.asigner.cp1.emulation.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class StatusComposite extends Group {
    private CLabel lblCy;
    private CLabel lblAc;
    private CLabel lblF0;
    private CLabel lblBs;
    private CLabel lblConst1;
    private CLabel lblSp;

    private CLabel lblDbf;
    private CLabel lblF1;
    private CLabel lblA;
    private CLabel lblT;
    private CLabel lblPC;

    private int psw = 0;
    private int dbf = 0;
    private int f1 = 0;
    private int a = 0;
    private int t = 0;
    private int pc = 0;


    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public StatusComposite(Composite parent, int style) {
        super(parent, SWT.BORDER);
        setText("Status");
        setLayout(new GridLayout(6, true));

        Label l1 = new Label(this, SWT.CENTER);
        l1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l1.setText("CY");

        Label l2 = new Label(this, SWT.CENTER);
        l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l2.setText("AC");

        Label l3 = new Label(this, SWT.CENTER);
        l3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l3.setText("F0");

        Label l4 = new Label(this, SWT.CENTER);
        l4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l4.setText("BS");

        Label l5 = new Label(this, SWT.CENTER);
        l5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l5.setText("");

        Label l6 = new Label(this, SWT.CENTER);
        l6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l6.setText("SP");

        lblCy = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblCy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblCy.setText("0");

        lblAc = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblAc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblAc.setText("0");

        lblF0 = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblF0.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblF0.setText("0");

        lblBs = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblBs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblBs.setText("0");

        lblConst1 = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblConst1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblConst1.setText("1");

        lblSp = new CLabel(this, SWT.BORDER | SWT.CENTER);
        GridData gd_lblSp = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_lblSp.widthHint = 12;
        lblSp.setLayoutData(gd_lblSp);
        lblSp.setText("0");

        ///////////

        Label l7 = new Label(this, SWT.CENTER);
        l7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l7.setText("DBF");

        Label l8 = new Label(this, SWT.CENTER);
        l8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l8.setText("F1");

        Label l9 = new Label(this, SWT.CENTER);
        l9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l9.setText("A");

        Label l10 = new Label(this, SWT.CENTER);
        l10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l10.setText("T");

        Label l11 = new Label(this, SWT.CENTER);
        l11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        l11.setText("PC");

        lblDbf = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblDbf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblDbf.setText("0");

        lblF1 = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblF1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblF1.setText("0");

        lblA = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblA.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblA.setText("00");

        lblT = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblT.setText("00");

        lblPC = new CLabel(this, SWT.BORDER | SWT.CENTER);
        lblPC.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        lblPC.setText("000");
}

    public void setPsw(int psw) {
        if (this.psw != psw) {;
            this.psw = psw;
            lblCy.setText((psw & 0x80) > 0 ? "1" : "0");
            lblAc.setText((psw & 0x40) > 0 ? "1" : "0");
            lblF0.setText((psw & 0x20) > 0 ? "1" : "0");
            lblBs.setText((psw & 0x10) > 0 ? "1" : "0");
            lblConst1.setText((psw & 0x8) > 0 ? "1" : "0");
            lblSp.setText(Integer.toString(psw & 0x7));
        }
    }

    public void setDbf(int dbf) {
        if (this.dbf != dbf) {
            this.dbf = dbf;
            lblDbf.setText(Integer.toString(dbf));
        }
    }

    public void setA(int a) {
        if (this.a != a) {
            this.a = a;
            lblA.setText(Integer.toHexString(a));
        }
    }

    public void setT(int t) {
        if (this.t != t) {
            this.t = t;
            lblT.setText(Integer.toHexString(t));
        }
    }

    public void setPc(int pc) {
        if (this.pc != pc) {
            this.pc = pc;
            lblPC.setText(Integer.toHexString(pc));
        }
    }

    public void setF1(int f1) {
        if (this.f1 != f1) {
            this.f1 = f1;
            lblF1.setText(Integer.toString(f1));
        }
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}


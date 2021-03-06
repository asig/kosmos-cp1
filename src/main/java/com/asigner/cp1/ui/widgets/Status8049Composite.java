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

import com.asigner.cp1.emulation.Intel8049;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.util.function.Consumer;

public class Status8049Composite extends Group {
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

    private BitsetWidget busWidget;
    private BitsetWidget p1Widget;
    private BitsetWidget p2Widget;
    private MemoryComposite memoryComposite;

    private Intel8049 cpu;

    private int psw = 0;
    private int dbf = 0;
    private int f1 = 0;
    private int a = 0;
    private int t = 0;
    private int pc = 0;
    private int bus = 0;
    private int p1 = 0;
    private int p2 = 0;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public Status8049Composite(Composite parent, int style) {
        super(parent, style);

        setLayout(new GridLayout(3, false));

        Composite cmp = new Composite(this, SWT.NONE);
        cmp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        cmp.setLayout(new GridLayout(6, true));

        Label l1 = new Label(cmp, SWT.CENTER);
        l1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l1.setText("CY");

        Label l2 = new Label(cmp, SWT.CENTER);
        l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l2.setText("AC");

        Label l3 = new Label(cmp, SWT.CENTER);
        l3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l3.setText("F0");

        Label l4 = new Label(cmp, SWT.CENTER);
        l4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l4.setText("BS");

        Label l5 = new Label(cmp, SWT.CENTER);
        l5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l5.setText("");

        Label l6 = new Label(cmp, SWT.CENTER);
        l6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l6.setText("SP");

        lblCy = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblCy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblCy.setText("0");
        addInlineEdit(lblCy, 0, 1, val -> cpu.setPSW((cpu.getPSW() & ~0x80) | val*0x80) );

        lblAc = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblAc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblAc.setText("0");
        addInlineEdit(lblAc, 0, 1, val -> cpu.setPSW((cpu.getPSW() & ~0x40) | val*0x40) );

        lblF0 = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblF0.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblF0.setText("0");
        addInlineEdit(lblF0, 0, 1, val -> cpu.setPSW((cpu.getPSW() & ~0x20) | val*0x20) );

        lblBs = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblBs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblBs.setText("0");
        addInlineEdit(lblBs, 0, 1, val -> cpu.setPSW((cpu.getPSW() & ~0x10) | val*0x10));

        lblConst1 = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblConst1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblConst1.setText("1");

        lblSp = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        GridData gd_lblSp = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_lblSp.widthHint = 12;
        lblSp.setLayoutData(gd_lblSp);
        lblSp.setText("0");
        addInlineEdit(lblSp, 0, 7, val -> cpu.setPSW((cpu.getPSW() & ~0x7) | val) );

        ///////////

        Label l7 = new Label(cmp, SWT.CENTER);
        l7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l7.setText("DBF");

        Label l8 = new Label(cmp, SWT.CENTER);
        l8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l8.setText("F1");

        Label l9 = new Label(cmp, SWT.CENTER);
        l9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l9.setText("A");

        Label l10 = new Label(cmp, SWT.CENTER);
        l10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        l10.setText("T");

        Label l11 = new Label(cmp, SWT.CENTER);
        l11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        l11.setText("PC");

        lblDbf = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblDbf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblDbf.setText("0");
        addInlineEdit(lblDbf, 0,1,val -> cpu.setDBF(val));

        lblF1 = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblF1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblF1.setText("0");
        addInlineEdit(lblF1, 0, 1, val -> cpu.setF1(val) );


        lblA = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblA.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblA.setText("$00");
        addInlineEdit(lblA, 0,255, val -> cpu.setA(val));

        lblT = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblT.setText("$00");
        addInlineEdit(lblT, 0,255, val -> cpu.setT(val));

        lblPC = new CLabel(cmp, SWT.BORDER | SWT.CENTER);
        lblPC.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        lblPC.setText("$000");
        addInlineEdit(lblPC, 0,2047, val -> cpu.setPC(val));;

        //////

        Label lblBus = new Label(cmp, SWT.LEFT);
        lblBus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblBus.setText("Bus");

        busWidget = new BitsetWidget(cmp, 8, SWT.NONE);
        busWidget.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));

        Label lblP1 = new Label(cmp, SWT.LEFT);
        lblP1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblP1.setText("Port 1");

        p1Widget = new BitsetWidget(cmp, 8, SWT.NONE);
        p1Widget.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));

        Label lblP2 = new Label(cmp, SWT.LEFT);
        lblP2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblP2.setText("Port 2");

        p2Widget = new BitsetWidget(cmp, 8, SWT.NONE);
        p2Widget.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));

        Composite cmp2 = new Composite(this, SWT.NONE);
        cmp2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        cmp2.setLayout(new GridLayout(1, false));

        Label lblMemory = new Label(cmp2, SWT.NONE);
        lblMemory.setText("Memory");

        memoryComposite = new MemoryComposite(cmp2, SWT.NONE);
        memoryComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    }

    public void setTraceExecution(boolean traceExecution) {
        memoryComposite.setTraceExecution(traceExecution);
    }

    public void setCpu(Intel8049 cpu) {
        this.cpu = cpu;
        memoryComposite.setRam(cpu.getRam());
        updateState();
    }

    private void addInlineEdit(CLabel parent, int rangeLow, int rangeHi, Consumer<Integer> consumer) {
        parent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                InlineEdit edit = new InlineEdit(parent, SWT.NONE);
                edit.init(parent, rangeLow, rangeHi, consumer);
            }
        });
    }

    public void updateState() {
        if (cpu == null || isDisposed()) {
            return;
        }

        int psw = cpu.getPSW();
        if (this.psw != psw) {;
            this.psw = psw;
            lblCy.setText((psw & 0x80) > 0 ? "1" : "0");
            lblAc.setText((psw & 0x40) > 0 ? "1" : "0");
            lblF0.setText((psw & 0x20) > 0 ? "1" : "0");
            lblBs.setText((psw & 0x10) > 0 ? "1" : "0");
            lblConst1.setText((psw & 0x8) > 0 ? "1" : "0");
            lblSp.setText(Integer.toString(psw & 0x7));
        }

        int dbf = cpu.getDBF();
        if (this.dbf != dbf) {
            this.dbf = dbf;
            lblDbf.setText(Integer.toString(dbf));
        }

        int a = cpu.getA();
        if (this.a != a) {
            this.a = a;
            lblA.setText(String.format("$%02x", a));
        }

        int t = cpu.getT();
        if (this.t != t) {
            this.t = t;
            lblT.setText(String.format("$%02x", t));
        }

        int pc = cpu.getPC();
        if (this.pc != pc) {
            this.pc = pc;
            lblPC.setText(String.format("$%03x", pc));
        }


        int f1 = cpu.getF1();
        if (this.f1 != f1) {
            this.f1 = f1;
            lblF1.setText(Integer.toString(f1));
        }

        int bus = cpu.getPort(0).read();
        if (this.bus != bus) {
        	this.bus = bus;
        	this.busWidget.setValue(bus);
        }

        int p1 = cpu.getPort(1).read();
        if (this.p1 != p1) {
        	this.p1 = p1;
        	this.p1Widget.setValue(p1);
        }

        int p2 = cpu.getPort(2).read();
        if (this.p2 != p2) {
        	this.p2 = p2;
        	this.p2Widget.setValue(p2);
        }

        redraw();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}


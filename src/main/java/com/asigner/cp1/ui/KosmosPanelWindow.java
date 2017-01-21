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

package com.asigner.cp1.ui;

import com.asigner.cp1.emulation.DataPort;
import com.asigner.cp1.emulation.InputPin;
import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.Intel8155;
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

import java.util.logging.Level;
import java.util.logging.Logger;

public class KosmosPanelWindow {

    private static final Logger logger = Logger.getLogger(KosmosPanelWindow.class.getName());

    private static final String WINDOW_TITLE = "Kosmos CP1";

    private final Intel8049 cpu;
    private final Intel8155 pid;

    private Shell shell;
    private KosmosControlPanel kosmosControlPanel;

    public KosmosPanelWindow(Intel8049 cpu, Intel8155 pid, ExecutorThread executorThread) {
        this.cpu = cpu;
        this.pid = pid;

        // Hook up the 8049;s PROG to the Panel. CP1's ROM uses MOVD A, P4 to
        // read the keyboard state from the lower nibble of P2. MOVD will
        // pull PROG to 0 when the address is valid on the lower 4 pins of port 2,
        // so we connect that pin to the panel to allow it to pick this up and then
        // send its data to the port.
        cpu.pinPROG.connectTo(new InputPin("PROG", this::pinProgWritten));

        executorThread.addListener(new ExecutorThread.ExecutionListener() {
            @Override
            public void executionStarted() {
                shell.getDisplay().syncExec(() -> updateWindowTitle("running"));
            }

            @Override
            public void executionStopped() {
                shell.getDisplay().syncExec(() -> updateWindowTitle("stopped"));
            }

            @Override
            public void resetExecuted() {
            }

            @Override
            public void breakpointHit(int addr) {
                shell.getDisplay().syncExec(() -> updateWindowTitle("stopped"));
            }

            @Override
            public void performanceUpdate(double performance) {
                shell.getDisplay().syncExec(() -> updateWindowTitle(String.format("%d%%", (int)(performance*100+.5))));
            }
        });
    }

    private void updateWindowTitle(String status) {
        shell.setText(WINDOW_TITLE + " (" + status + ")");
    }

    private void pinProgWritten(int oldValue, int newValue) {
        if (oldValue == 1 && newValue == 0) {
            // Falling flag indicates that the address is valid on P2. Next, the CPU will read or write data.
            // We know that the writes to the port don't happen in the CPU, so we just write the keyboard state
            // to the port.

            int row = -1;
            int mask = pid.getPcValue();
            for (int i = 0; i < 8; i++) {
                if ((mask & (1 << i)) == 0) {
                    row = i;
                    break;
                }
            }
            int keyMask = kosmosControlPanel.getKeyMask(row);
            cpu.getPort(2).write(keyMask);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(String.format("Writing to Port 2: KeyMask for row %d == %02x", row, keyMask));
            }
        }

    }
    /**
     * Open the window.
     */
    public void open() {
        createContents();
        shell.open();
        shell.layout();
        shell.pack();
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        updateWindowTitle("stopped");
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
        p1Display.setPid(pid);
        GridData gd_p1Display = GridDataFactory.swtDefaults().hint(-1, 100).create();
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

        kosmosControlPanel = new KosmosControlPanel(composite, SWT.NONE);
    }
}

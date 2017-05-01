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
import com.asigner.cp1.ui.actions.Cp3ExtensionAction;
import com.asigner.cp1.ui.actions.Cp5ExtensionAction;
import com.asigner.cp1.ui.actions.LoadAction;
import com.asigner.cp1.ui.actions.SaveAction;
import com.asigner.cp1.ui.widgets.ActionMenuItem;
import com.asigner.cp1.ui.widgets.CP1Display;
import com.asigner.cp1.ui.widgets.CP5Panel;
import com.asigner.cp1.ui.widgets.KosmosControlPanel;
import com.asigner.cp1.ui.widgets.KosmosLogoComposite;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KosmosPanelWindow extends Window {

    private static final Logger logger = Logger.getLogger(KosmosPanelWindow.class.getName());

    public static final String NAME = "Panel";
    
    private static final String WINDOW_TITLE = "Kosmos CP1";

    private Shell shell;

    private LoadAction loadAction;
    private SaveAction saveAction;

    private Intel8049 cpu;
    private Intel8155 pid;
    private Intel8155 pidExtension;
    private ExecutorThread executorThread;

    private KosmosControlPanel kosmosControlPanel;
    private CP5Panel cp5Panel;

    private ExecutorThread.ExecutionListener executionListener;
    private DataPort.Listener port1Listener;
    private Intel8155.StateListener pidListener;

    public KosmosPanelWindow(WindowManager windowManager, Intel8049 cpu, Intel8155 pid, Intel8155 pidExtension, ExecutorThread executorThread) {
        super(windowManager, NAME);

        this.cpu = cpu;
        this.pid = pid;
        this.pidExtension = pidExtension;
        this.executorThread = executorThread;

        // Hook up the 8049;s PROG to the Panel. CP1's ROM uses MOVD A, P4 to
        // read the keyboard state from the lower nibble of P2. MOVD will
        // pull PROG to 0 when the address is valid on the lower 4 pins of port 2,
        // so we connect that pin to the panel to allow it to pick this up and then
        // send its data to the port.
        cpu.pinPROG.connectTo(new InputPin("PROG", this::pinProgWritten));

        executionListener = new ExecutorThread.ExecutionListener() {
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
        };
        port1Listener = new DataPort.Listener() {
            private boolean inValueChanged = false;

            @Override
            public void valueChanged(int oldValue, int newValue) {
                if (inValueChanged) {
                    // Called by our own write. Bail out.
                    return;
                }

                // CPU wrote to the port to prepare the pins for input. Write switch settings
                inValueChanged = true;
                cpu.getPort(1).write(cp5Panel.readSwitches());
                inValueChanged = false;
            }
        };
        pidListener = new Intel8155.StateListener() {
            @Override
            public void commandRegisterWritten() {
            }

            @Override
            public void portWritten(Port port, int value) {
                if (port == Port.B) {
                    shell.getDisplay().syncExec(() -> {
                        cp5Panel.writeLeds(value);
                    });
                }
            }

            @Override
            public void memoryWritten() {
            }

            @Override
            public void pinsChanged() {
            }

            @Override
            public void resetExecuted() {
            }
        };

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
            cpu.getPort(2).write(keyMask, 0x0f); // only the lower 4 bits of the port are connected to the key matrix. DO NOT TOUCH the upper nibble, as this is connected to the 8155s.
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(String.format("Writing to Port 2: KeyMask for row %d == $%02x", row, keyMask));
            }
        }

    }

    /**
     * Open the window.
     */
    public void open() {
        createShell();
        createActions();
        createContent();

        executorThread.addListener(executionListener);
        cpu.getPort(1).addListener(port1Listener);
        pid.addListener(pidListener);
        shell.addDisposeListener(ev -> {
            executorThread.removeListener(executionListener);
            cpu.getPort(1).removeListener(port1Listener);
            pid.removeListener(pidListener);
        });
        
        shell.setMenuBar(createMenuBar());
        shell.open();
        shell.layout();
        shell.pack();
        fireWindowOpened();
    }

    @Override
    protected Shell getShell() {
        return shell;
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    private void createShell() {
        shell = new Shell((Display)null, SWT.SHELL_TRIM | SWT.CENTER);
        updateWindowTitle("stopped");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        Image icon = SWTResources.getImage("/com/asigner/cp1/ui/icon-128x128.png");
        shell.setImage(icon);
        shell.addDisposeListener(disposeEvent -> fireWindowClosed());
    }

    private void createActions() {
        loadAction = new LoadAction(shell, pid, pidExtension, executorThread);
        saveAction = new SaveAction(shell, pid, pidExtension, executorThread);
    }

    /**
     * Create contents of the window.
     * @wbp.parser.entryPoint
     */
    private void createContent() {
        Composite composite = new Composite(shell, SWT.NONE);
        GridLayout gl_composite = new GridLayout(1, false);
        gl_composite.marginTop = 50;
        gl_composite.marginBottom = 50;
        gl_composite.marginRight = 50;
        gl_composite.marginLeft = 50;
        composite.setLayout(gl_composite);
        composite.setBackground(CP1Colors.GREEN);

        cp5Panel = new CP5Panel(composite, SWT.NONE);
        cp5Panel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        cp5Panel.writeLeds(pid.getPaValue());
        cpu.getPort(1).write(cp5Panel.readSwitches());
        cp5Panel.addSwitchesListener(() -> {
            cpu.getPort(1).write(cp5Panel.readSwitches());
        });

        Label spacer1 = new Label(composite, SWT.NONE);
        spacer1.setLayoutData(GridDataFactory.fillDefaults().hint(-1, 50).create());
        spacer1.setBackground(CP1Colors.GREEN);

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
        GridData gd_p1Display = GridDataFactory.swtDefaults().create();
        p1Display.setLayoutData(gd_p1Display);

        KosmosLogoComposite kosmosLogo = new KosmosLogoComposite(composite_1, SWT.NONE);
        GridData gd_kosmosLogo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
        gd_kosmosLogo.heightHint = 100;
        kosmosLogo.setLayoutData(gd_kosmosLogo);
        p1Display.display("C12127");

        Label spacer2 = new Label(composite, SWT.NONE);
        spacer2.setLayoutData(GridDataFactory.fillDefaults().hint(-1, 50).create());
        spacer2.setBackground(CP1Colors.GREEN);

        kosmosControlPanel = new KosmosControlPanel(composite, SWT.NONE);
    }

    private Menu createMenuBar() {
        Menu menu = new Menu(shell, SWT.BAR);

        createFileMenu(menu);

        Menu stateMenu = new Menu(menu);
        new ActionMenuItem(stateMenu, SWT.NONE, loadAction);
        new ActionMenuItem(stateMenu, SWT.NONE, saveAction);

        MenuItem stateItem = new MenuItem(menu, SWT.CASCADE);
        stateItem.setText("&State");
        stateItem.setMenu(stateMenu);

        createWindowMenu(menu);
        createHelpMenu(menu);

        return menu;
    }
}

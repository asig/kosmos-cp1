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

package com.asigner.cp1.ui.actions;

import com.asigner.cp1.emulation.Intel8155;
import com.asigner.cp1.ui.ExecutorThread;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.FileOutputStream;
import java.io.IOException;

public class SaveAction extends Action {
    private final Shell shell;
    private final Intel8155 pid;
    private final Intel8155 pidExtension;
    private final ExecutorThread executor;

    public SaveAction(Shell shell, Intel8155 pid, Intel8155 pidExtension, ExecutorThread executor) {
        super("Save");
        this.shell = shell;
        this.pid = pid;
        this.pidExtension = pidExtension;
        this.executor = executor;
    }

    @Override
    public void run() {
        executor.postCommand(ExecutorThread.Command.STOP);
        FileDialog fd = new FileDialog(shell, SWT.SAVE);
        fd.setFilterExtensions(new String [] {"*.bin"});
        String result = fd.open();
        if (result != null) {
            try {
                FileOutputStream fos = new FileOutputStream(result);
                for (int i = 0; i < 256; i++) {
                    fos.write((byte)pid.getRam().read(i));
                }
                for (int i = 0; i < 256; i++) {
                    fos.write((byte)pidExtension.getRam().read(i));
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

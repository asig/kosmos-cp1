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
import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.ui.AssemblerDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadAction extends Action {
    private final Shell shell;
    private final Intel8155 pid;
    private final Intel8155 pidExtension;

    public LoadAction(Shell shell, Intel8155 pid, Intel8155 pidExtension) {
        super("Load");
        this.shell = shell;
        this.pid = pid;
        this.pidExtension = pidExtension;
    }


    @Override
    public void run() {
        FileDialog fd = new FileDialog(shell, SWT.OPEN);
        fd.setFilterExtensions(new String [] {"*.bin"});
        String result = fd.open();
        if (result != null) {
            try {
                File f = new File(result);
                int len = (int)f.length();
                if (len != 256 && len != 512) {
                    System.err.println("Wrong length!");
                    return;
                }
                byte[] buf = new byte[len];
                FileInputStream fis = new FileInputStream(f);
                fis.read(buf);

                for (int i = 0; i < 256; i++) {
                    pid.getRam().write(i, buf[i]);
                }
                for (int i = 256; i < len; i++) {
                    pidExtension.getRam().write(i - 256, buf[i]);
                }
                for (int i = len; i < 512; i++) {
                    pidExtension.getRam().write(i - 256, 0);
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

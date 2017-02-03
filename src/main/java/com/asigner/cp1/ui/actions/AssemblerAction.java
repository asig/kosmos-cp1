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
import org.eclipse.swt.widgets.Shell;

public class AssemblerAction extends Action {

    private final Shell shell;
    private final Intel8155 pid;
    private final Intel8155 pidExtension;

    public AssemblerAction(Shell shell, Intel8155 pid, Intel8155 pidExtension) {
        super("Assembler");
        this.shell = shell;
        this.pid = pid;
        this.pidExtension = pidExtension;
    }


    @Override
    public void run() {
        AssemblerDialog dlg = new AssemblerDialog(shell);
        dlg.setResultListener(code -> {
            Ram ram = pid.getRam();
            for (int i = 0; i < 256; i++) {
                ram.write(i, code[i]);
            }
            if (code.length > 256) {
                ram = pidExtension.getRam();
                for (int i = 0; i < 256; i++) {
                    ram.write(i, code[256 + i]);
                }
            }
        });
        dlg.open();
    }
}

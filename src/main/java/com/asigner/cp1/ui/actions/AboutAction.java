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

import com.asigner.cp1.ui.AboutDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AboutAction extends Action {

    public AboutAction() {
        super("About");
    }

    @Override
    public void run() {
        Display display = Display.getDefault();
        Shell result = display.getActiveShell();
        if (result == null) {
            Shell[] shells = display.getShells();
            for (Shell shell : shells) {
                if (shell.getShells().length == 0) {
                    result = shell;
                }
            }
        }

    	AboutDialog dlg = new AboutDialog(result, SWT.DIALOG_TRIM);
        dlg.open();
    }

}

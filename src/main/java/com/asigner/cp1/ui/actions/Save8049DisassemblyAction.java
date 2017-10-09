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

import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.util.Disassembler;
import com.asigner.cp1.emulation.util.Disassembler.Line;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Save8049DisassemblyAction extends Action {

    private final Intel8049 cpu;

    public Save8049DisassemblyAction(Intel8049 cpu) {
        super("Save 8049 disassembly...");
        this.cpu = cpu;
    }

    @Override
    public void run() {
        FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        fd.setText("Save");
        String[] filterExt = { "*.asm", "*.*" };
        fd.setFilterExtensions(filterExt);
        String selected = fd.open();
        if (selected != null) {
            try {
                FileWriter w = new FileWriter(selected);
                Disassembler disassembler = new Disassembler(cpu.getRom());
                List<Line> lines = disassembler.disassemble();
                for (Line l : lines) {
                    String s = String.format("%04x: [ %s ] %s", l.getAddress(), l.getBytes(), l.getDisassembly());
                    w.write(s);
                    w.write("\n");
                }
                w.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

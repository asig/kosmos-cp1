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
import com.asigner.cp1.ui.ExecutorThread;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AssemblerAction extends Action {

    private final Shell shell;
    private final Intel8155 pid;
    private final Intel8155 pidExtension;
    private final ExecutorThread executor;
    private final List<AssemblerDialog.SampleCode> sampleListings;

    public AssemblerAction(Shell shell, Intel8155 pid, Intel8155 pidExtension, ExecutorThread executor) {
        super("Assembler");
        this.shell = shell;
        this.pid = pid;
        this.pidExtension = pidExtension;
        this.executor = executor;

        this.sampleListings = Lists.newArrayListWithCapacity(100);
        for (int i = 0; i < 100; i++) {
            InputStream is = this.getClass().getResourceAsStream(String.format("/com/asigner/cp1/listings/listing%d.asm", i));
            if (is != null) {
                try {
                    List<String> text = IOUtils.readLines(is, "UTF-8");
                    String name = text.get(0).substring(1).trim();
                    sampleListings.add(new AssemblerDialog.SampleCode(name, text));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void run() {
        AssemblerDialog dlg = new AssemblerDialog(shell);
        dlg.setSampleListings(sampleListings);
        dlg.setResultListener(code -> {
            boolean running = executor.isRunning();
            if (running) {
                executor.postCommand(ExecutorThread.Command.STOP);
            }
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
            if (running) {
                executor.postCommand(ExecutorThread.Command.START);
            }
        });
        dlg.open();
    }
}

package com.asigner.cp1.ui.actions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.asigner.cp1.emulation.Intel8049;
import com.asigner.cp1.emulation.util.Disassembler;
import com.asigner.cp1.emulation.util.Disassembler.Line;

public class SaveDisassemblyAction extends Action {

    private final Intel8049 cpu;

    public SaveDisassemblyAction(Intel8049 cpu) {
        super("Save disassembly...");
        this.cpu = cpu;
    }

    @Override
    public void run() {
        FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        fd.setText("Save");
        String[] filterExt = { "*.txt", "*.*" };
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

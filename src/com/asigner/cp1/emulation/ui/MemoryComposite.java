package com.asigner.cp1.emulation.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.MemoryModifiedListener;
import com.asigner.cp1.emulation.Ram;

public class MemoryComposite extends Composite implements MemoryModifiedListener {

    private static final int BYTES_PER_LINE = 16;

    private static final Color BG = SWTResourceManager.getColor(SWT.COLOR_WHITE);
    private static final Color FG = SWTResourceManager.getColor(SWT.COLOR_BLACK);
    private static final Color BG_SEL = SWTResourceManager.getColor(SWT.COLOR_RED);
    private static final Color FG_SEL = SWTResourceManager.getColor(SWT.COLOR_YELLOW);

    private Ram ram;
    private int lastWritten = -1;

    private final FontMetrics fontMetrics;
    private final int totalLineHeight;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public MemoryComposite(Composite parent, int style) {
        super(parent, style);

        Font terminalFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        GC gc = new GC(Display.getDefault());
        gc.setFont(terminalFont);
        fontMetrics = gc.getFontMetrics();
        totalLineHeight = 6*fontMetrics.getHeight()/6;
        gc.dispose();
        setFont(terminalFont);

        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent evt) {
                paint(evt.gc);
            }
        });

    }

    public void setRam(Ram ram) {
        if (this.ram != null) {
            this.ram.removeListener(this);
        }
        this.ram = ram;
        if (this.ram != null) {
            this.ram.addListener(this);
        }
        setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        int cw = fontMetrics.getAverageCharWidth();
        int w = cw * (6 + BYTES_PER_LINE * 3 - 1);
        int lines = ram != null ? (ram.size() + BYTES_PER_LINE - 1)/BYTES_PER_LINE : 0;
        return new Point(w,lines * totalLineHeight);
    }

    private void paint(GC gc) {
        gc.setBackground(BG);
        gc.setForeground(FG);
        int lines = getLineCount();
        int cw = fontMetrics.getAverageCharWidth();
        int curX = 0;
        for (int i = 0; i < lines; i++) {
            int curY =  i * totalLineHeight;
            curX = 0;
            gc.drawText(String.format("%04x:", i * BYTES_PER_LINE), curX, curY);
            curX = 5 * cw;
            for(int j = 0; j < BYTES_PER_LINE; j++) {
                int pos = i * BYTES_PER_LINE + j;
                if (pos < ram.size()) {
                    if (pos != lastWritten) {
                        gc.drawText(String.format(" %02x", ram.read(pos)), curX, i * totalLineHeight);
                        curX += 3 * cw;
                    } else {
                        gc.drawText(String.format(" ", ram.read(pos)), curX, i * totalLineHeight);
                        curX += cw;
                        gc.setBackground(BG_SEL);
                        gc.setForeground(FG_SEL);
                        gc.drawText(String.format("%02x", ram.read(pos)), curX, i * totalLineHeight);
                        curX += 2 * cw;
                        gc.setBackground(BG);
                        gc.setForeground(FG);
                    }
                }
            }
        }
    }

    private int getLineCount() {
        return ram != null ? (ram.size() + BYTES_PER_LINE - 1)/BYTES_PER_LINE : 0;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void memoryWritten(int addr, int value) {
        if (addr != lastWritten) {
            lastWritten = addr;
            redraw();
        }
    }

    @Override
    public void memoryCleared() {
        if (lastWritten != -1) {
            lastWritten = -1;
            redraw();
        }
    }

}

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

import com.asigner.cp1.emulation.Ram;

public class MemoryComposite extends Composite {

    private static final int BYTES_PER_LINE = 16;

    private static final Color BG = SWTResourceManager.getColor(SWT.COLOR_WHITE);
    private static final Color FG = SWTResourceManager.getColor(SWT.COLOR_BLACK);
    private static final Color BG_SEL = SWTResourceManager.getColor(SWT.COLOR_RED);
    private static final Color FG_SEL = SWTResourceManager.getColor(SWT.COLOR_YELLOW);

    private Ram ram;
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
        this.ram = ram;
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
        for (int i = 0; i < lines; i++) {
            StringBuffer lineBuf = new StringBuffer();
            lineBuf.append(String.format("%04x:", i * BYTES_PER_LINE));
            for(int j = 0; j < BYTES_PER_LINE; j++) {
                int pos = i * BYTES_PER_LINE + j;
                if (pos < ram.size()) {
                    lineBuf.append(String.format(" %02x", ram.read(pos)));
                } else {
                    lineBuf.append("   ");
                }
            }
            gc.drawText(lineBuf.toString(), 0, i * totalLineHeight);
        }
    }

    private int getLineCount() {
        return ram != null ? (ram.size() + BYTES_PER_LINE - 1)/BYTES_PER_LINE : 0;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}

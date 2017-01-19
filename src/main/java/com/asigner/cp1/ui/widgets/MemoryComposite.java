package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.SWTResources;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.asigner.cp1.emulation.MemoryModifiedListener;
import com.asigner.cp1.emulation.Ram;

public class MemoryComposite extends Composite implements MemoryModifiedListener {

    private static final int BYTES_PER_LINE = 16;

    private static final Color BG = SWTResources.WHITE;
    private static final Color FG = SWTResources.BLACK;
    private static final Color BG_SEL = SWTResources.RED;
    private static final Color FG_SEL = SWTResources.YELLOW;

    private Ram ram;
    private int lastWritten = -1;

    private final FontMetrics fontMetrics;
    private final Font font;
    private final int totalLineHeight;

    private boolean traceExecution;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public MemoryComposite(Composite parent, int style) {
        super(parent, style);

        font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        GC gc = new GC(Display.getDefault());
        gc.setFont(font);
        fontMetrics = gc.getFontMetrics();
        totalLineHeight = 6*fontMetrics.getHeight()/6;
        gc.dispose();
        setFont(font);

        addPaintListener(this::paint);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                int bytePos = getBytePos(new Point(e.x, e.y));
                if (bytePos != -1) {
                    InlineEdit edit = new InlineEdit(MemoryComposite.this, SWT.NONE);
                    edit.init(getByteRect(bytePos), String.format("%02x", ram.read(bytePos)), 0, 255, i -> ram.write(bytePos,i));
                    edit.setFont(font);
                }
            }
        });

    }

    public boolean isTraceExecution() {
        return traceExecution;
    }

    public void setTraceExecution(boolean traceExecution) {
        this.traceExecution = traceExecution;
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

    private int getBytePos(Point p) {
        int line = p.y / totalLineHeight;
        int cw = fontMetrics.getAverageCharWidth();
        int x = p.x / cw;
        if (x < 5) {
            // in address range
            return -1;
        }
        x -= 5;
        if (x % 3 == 0) {
            // in white space
            return -1;
        }
        return line * BYTES_PER_LINE + (x/3);
    }

    private Rectangle getByteRect(int b) {
        int cw = fontMetrics.getAverageCharWidth();
        int y = (b/BYTES_PER_LINE) * totalLineHeight;
        int x = cw * (5 + (b%BYTES_PER_LINE)*3 + 1);
        return new Rectangle(x - 2, y - 2, 2*cw + 4, totalLineHeight + 4);
    }

    private void paint(PaintEvent event) {
        GC gc = event.gc;
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
    public void memoryWritten(final int addr, int value) {
        if (!isTraceExecution()) {
            return;
        }
        if (addr != lastWritten) {
            lastWritten = addr;
        }
        getDisplay().syncExec(this::redraw);
    }

    @Override
    public void memoryCleared() {
        if (!isTraceExecution()) {
            return;
        }
        if (lastWritten != -1) {
            lastWritten = -1;
        }
        getDisplay().syncExec(this::redraw);
    }

}

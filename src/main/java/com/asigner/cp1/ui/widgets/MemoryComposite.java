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

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.emulation.Ram;
import com.asigner.cp1.ui.OS;
import com.asigner.cp1.ui.util.SWTResources;
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

public class MemoryComposite extends Composite {

    private static final int BYTES_PER_LINE = 16;

    private static final Color BG = SWTResources.WHITE;
    private static final Color FG = SWTResources.BLACK;
    private static final Color BG_SEL = SWTResources.RED;
    private static final Color FG_SEL = SWTResources.YELLOW;

    private static final boolean isMac = OS.isMac();

    private Ram ram;
    private int lastWritten = -1;

    private final int avgCharWidth;
    private final Font font;
    private final int totalLineHeight;

    private boolean traceExecution;

    private Ram.MemoryModifiedListener memoryModifiedListener;

    public MemoryComposite(Composite parent, int style) {
        super(parent, style | SWT.NO_BACKGROUND);

        font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        GC gc = new GC(Display.getDefault());
        gc.setFont(font);
        FontMetrics fontMetrics = gc.getFontMetrics();
        totalLineHeight = 6*fontMetrics.getHeight()/6;
        avgCharWidth = fontMetrics.getAverageCharWidth();
        gc.dispose();
        setFont(font);

        addPaintListener(this::paint);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                int bytePos = getBytePos(new Point(e.x, e.y));
                if (bytePos != -1) {
                    InlineEdit edit = new InlineEdit(MemoryComposite.this, SWT.NONE);
                    edit.init(getByteRect(bytePos, 2), String.format("%02x", ram.read(bytePos)), 0, 255, i -> ram.write(bytePos,i));
                    edit.setFont(font);
                }
            }
        });

        memoryModifiedListener = new Ram.MemoryModifiedListener() {
            @Override
            public void memoryWritten(final int addr, int value) {
                if (isDisposed() || !isTraceExecution()) {
                    return;
                }
                if (addr != lastWritten) {
                    lastWritten = addr;
                }
                getDisplay().syncExec(MemoryComposite.this::redraw);
            }

            @Override
            public void memoryCleared() {
                if (isDisposed() || !isTraceExecution()) {
                    return;
                }
                if (lastWritten != -1) {
                    lastWritten = -1;
                }
                getDisplay().syncExec(MemoryComposite.this::redraw);
            }
        };

        this.addDisposeListener(disposeEvent -> {
            if (ram != null) {
                ram.removeListener(memoryModifiedListener);
            }
        });
    }

    public void redraw() {
        if (!isDisposed()) {
            super.redraw();
        }
    }
    
    public boolean isTraceExecution() {
        return traceExecution;
    }

    public void setTraceExecution(boolean traceExecution) {
        this.traceExecution = traceExecution;
    }

    public void setRam(Ram ram) {
        if (this.ram != null) {
            this.ram.removeListener(memoryModifiedListener);
        }
        this.ram = ram;
        if (this.ram != null) {
            this.ram.addListener(memoryModifiedListener);
        }
        setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        int adjustment = isMac ? (BYTES_PER_LINE * 2) : 0; // Some pixel adjustments for Mac.
        int w = avgCharWidth * (6 + BYTES_PER_LINE * 3 - 1) + adjustment;
        int lines = ram != null ? (ram.size() + BYTES_PER_LINE - 1)/BYTES_PER_LINE : 0;
        return new Point(w,lines * totalLineHeight);
    }

    private int getBytePos(Point p) {
        int line = p.y / totalLineHeight;

        // Thanks to some minor differences on Macs, we can't just look at the character pos,
        // but we rather need to check for each byte...
        for (int i = 0; i < BYTES_PER_LINE; i++) {
            int b = line * BYTES_PER_LINE + i;
            Rectangle r = getByteRect(b, 0);
            if (p.x >= r.x && p.x < r.x + r.width && p.y >= r.y && p.y < r.y + r.height) {
                return b;
            }
        }
        return -1;
    }

    private Rectangle getByteRect(int b, int margin) {
        int bOfs = b % BYTES_PER_LINE;
        int adjustment = isMac ? (bOfs * 2) : 0; // Some pixel adjustments for Mac.
        int y = (b/BYTES_PER_LINE) * totalLineHeight;
        int x = avgCharWidth * (5 + bOfs*3 + 1) + adjustment;
        return new Rectangle(x - margin, y - margin, 2 * avgCharWidth + 2 * margin, totalLineHeight + 2 * margin);
    }

    private void paint(PaintEvent event) {
        GC gc = event.gc;
        boolean isEnabled = isEnabled();
        if (isEnabled) {
            gc.setBackground(BG);
            gc.setForeground(FG);
        } else {
            gc.setBackground(getBackground());
            gc.setForeground(SWTResources.getColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
        }
        int lines = getLineCount();
        int curX = 0;
        int adjustment = isMac ? 2 : 0; // Some pixel adjustments for Mac.
        for (int i = 0; i < lines; i++) {
            int curY =  i * totalLineHeight;
            curX = 0;
            gc.drawText(String.format("%04x:", i * BYTES_PER_LINE), curX, curY);
            curX = 5 * avgCharWidth;
            for(int j = 0; j < BYTES_PER_LINE; j++) {
                int pos = i * BYTES_PER_LINE + j;
                if (pos < ram.size()) {
                    if (pos != lastWritten || !isEnabled) {
                        gc.drawText(String.format(" %02x", ram.read(pos)), curX, i * totalLineHeight);
                        curX += 3 * avgCharWidth + adjustment;
                    } else {
                        gc.drawText(" ", curX, i * totalLineHeight);
                        curX += avgCharWidth;
                        gc.setBackground(BG_SEL);
                        gc.setForeground(FG_SEL);
                        gc.drawText(String.format("%02x", ram.read(pos)), curX, i * totalLineHeight);
                        curX += 2 * avgCharWidth + adjustment;
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

}

package com.asigner.cp1.emulation.ui;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.Rom;
import com.asigner.cp1.emulation.util.Disassembler;

public class DisassemblyComposite extends Composite {

    private static final Color BG = SWTResourceManager.getColor(SWT.COLOR_WHITE);
    private static final Color FG = SWTResourceManager.getColor(SWT.COLOR_BLACK);
    private static final Color BG_SEL = SWTResourceManager.getColor(SWT.COLOR_RED);
    private static final Color FG_SEL = SWTResourceManager.getColor(SWT.COLOR_YELLOW);

    private static final int MAX_LINE_WIDTH = 29; // Depends on the formatting

    private final FontMetrics fontMetrics;
    private final int totalLineHeight;

    private Disassembler.Line[] lines = new Disassembler.Line[0];
    private int[] pcToLine;
    private String emptyLine;
    private int selectedLine;
    private int lineOfs;
    private int colOfs;

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public DisassemblyComposite(Composite parent, int style) {
        super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);

        Font terminalFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        GC gc = new GC(Display.getDefault());
        gc.setFont(terminalFont);
        fontMetrics = gc.getFontMetrics();
        totalLineHeight = 6*fontMetrics.getHeight()/6;
        gc.dispose();

        setFont(terminalFont);

        addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                onResize();
            }
        });

        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent evt) {
                paint(evt.gc);
            }
        });

        getVerticalBar().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                onVerticalScrollbarSelected(((ScrollBar)evt.getSource()).getSelection());
            }});
        getHorizontalBar().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                onHorizontalScrollbarSelected(((ScrollBar)evt.getSource()).getSelection());
            }});
    }

    public void selectAddress(int address) {
        this.selectedLine = pcToLine[address];
        // make sure the selected line is visible
        if (selectedLine < lineOfs) {
            lineOfs = selectedLine;
        } else if (selectedLine >= lineOfs + getVisibleLineCount()) {
            lineOfs = selectedLine - getVisibleLineCount()/2; // set it in the middle of the screen
        }
        redraw();
    }

    public void setRom(Rom rom) {
        List<Disassembler.Line> lines = new Disassembler(rom).disassemble(0, rom.size());
        this.lines = lines.toArray(new Disassembler.Line[lines.size()]);
        this.pcToLine = new int[rom.size()];
        int filledTo = rom.size();
        for(int i = lines.size()-1; i>=0; i--) {
            Disassembler.Line l = this.lines[i];
            for(int j = l.getAddress(); j < filledTo; j++) {
                pcToLine[j] = i;
            }
            filledTo = l.getAddress();
        }
        lineOfs = 0;
        colOfs = 0;
        selectedLine = 0;
        onResize();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        int w = MAX_LINE_WIDTH * fontMetrics.getAverageCharWidth();
//        int lines = this.lines.length;
//        int h = lines * fontMetrics.getHeight() + (lines - 1) * fontMetrics.getHeight()/2;
        return new Point(w, 0); // make sure we're small enough to be resized by the parent.
    }

    private void paint(GC gc) {
        int visibleLines = getVisibleLineCount();
        for (int i = 0; i < visibleLines; i++) {
            String s = "";
            int line = lineOfs + i;
            if (line < lines.length) {
                if (line == selectedLine) {
                    gc.setBackground(BG_SEL);
                    gc.setForeground(FG_SEL);
                } else {
                    gc.setBackground(BG);
                    gc.setForeground(FG);
                }
                Disassembler.Line l = lines[lineOfs + i];
                s = String.format("%04x: [ %s ] %s", l.getAddress(), l.getBytes(), l.getDisassembly());
            }

            if (s.length() < emptyLine.length()) {
                s += emptyLine.substring(s.length());
            }
            gc.drawText(s.substring(colOfs), 0, i * totalLineHeight);
        }
    }

    private void onVerticalScrollbarSelected(int selection) {
        lineOfs = selection;
        redraw();
    }

    private void onHorizontalScrollbarSelected(int selection) {
        colOfs = selection;
        redraw();
    }

    private void onResize() {
        int visibleLines = getVisibleLineCount();
        int lines = this.lines.length;

        int visibleCols = getVisibleColCount();
        int cols = MAX_LINE_WIDTH;
        StringBuffer emptyLineBuf = new StringBuffer();
        for (int i = 0; i < visibleCols; i++) {
            emptyLineBuf.append(' ');
        }
        emptyLine = emptyLineBuf.toString();

        ScrollBar vBar = this.getVerticalBar();
        vBar.setMinimum(0);
        vBar.setMaximum(lines);
        vBar.setIncrement(1);
        vBar.setPageIncrement(visibleLines);
        vBar.setThumb(visibleLines);

        ScrollBar hBar = this.getHorizontalBar();
        hBar.setMinimum(0);
        hBar.setMaximum(cols);
        hBar.setIncrement(1);
        hBar.setPageIncrement(visibleCols);
        hBar.setThumb(visibleCols);

//        Rectangle r = this.getClientArea();
//        System.err.println(" ==== w = " + r.width + "  === h = " + r.height);
    }

    private int getVisibleLineCount() {
        Rectangle clientArea = getClientArea();
        return (clientArea.height + totalLineHeight - 1) / totalLineHeight;
    }

    private int getVisibleColCount() {
        Rectangle clientArea = getClientArea();
        return (clientArea.width + fontMetrics.getAverageCharWidth() - 1) / fontMetrics.getAverageCharWidth();
    }
}


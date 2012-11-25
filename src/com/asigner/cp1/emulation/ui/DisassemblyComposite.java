package com.asigner.cp1.emulation.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.wb.swt.SWTResourceManager;

import com.asigner.cp1.emulation.Rom;
import com.asigner.cp1.emulation.util.Disassembler;

public class DisassemblyComposite extends Composite {

	private static final Logger logger = Logger.getLogger(DisassemblyComposite.class.getName());
	
    private static final Color BG = SWTResourceManager.getColor(SWT.COLOR_WHITE);
    private static final Color FG = SWTResourceManager.getColor(SWT.COLOR_BLACK);
    private static final Color BG_SEL = SWTResourceManager.getColor(SWT.COLOR_RED);
    private static final Color FG_SEL = SWTResourceManager.getColor(SWT.COLOR_YELLOW);

    private static final int DECORATION_WIDTH = 2;
    private static final int MAX_LINE_WIDTH = 29 + DECORATION_WIDTH; // Depends on the formatting

    private final List<BreakpointChangedListener> listeners = new LinkedList<BreakpointChangedListener>();

    private final Image breakpointImage = SWTResourceManager.getImage(DisassemblyComposite.class, "bullet_red.png");
    private final int breakpointImgWidth = breakpointImage.getBounds().width;
    private final int breakpointImgHeight = breakpointImage.getBounds().height;
    private final FontMetrics fontMetrics;
    private final int totalLineHeight;

    private Disassembler.Line[] lines = new Disassembler.Line[0];
    private int[] pcToLine;
    private Set<Integer> breakpoints = new HashSet<Integer>();

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
        super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND);

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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                toggleBreakpoint(e.x, e.y);
            }

//            @Override
//            public void mouseDown(MouseEvent e) {
//                showContextMenu(e.x, e.y);
//            }
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

    public void addListener(BreakpointChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BreakpointChangedListener listener) {
        listeners.remove(listener);
    }

    public void selectAddress(int address) {
        this.selectedLine = pcToLine[address];
        // make sure the selected line is visible
        if (selectedLine < lineOfs) {
            lineOfs = selectedLine;
        } else if (selectedLine >= lineOfs + getVisibleLineCount()) {
            lineOfs = selectedLine - getVisibleLineCount()/2; // set it in the middle of the screen
        }
        getVerticalBar().setSelection(lineOfs);
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
        long start = System.currentTimeMillis();
        int visibleLines = getVisibleLineCount();
        for (int i = 0; i < visibleLines; i++) {
            drawLine(gc, lineOfs + i, i * totalLineHeight);
        }
        long end = System.currentTimeMillis();
        logger.finest("painting took " + (end-start) + " millis");
    }

    private void drawLine(GC gc, int line, int y) {
        String s = "";
        Disassembler.Line l = null;
        if (line < lines.length) {
            l = lines[line];
            if (line == selectedLine) {
                gc.setBackground(BG_SEL);
                gc.setForeground(FG_SEL);
            } else {
                gc.setBackground(BG);
                gc.setForeground(FG);
            }

            // draw Text
            s = String.format("%04x: [ %s ] %s", l.getAddress(), l.getBytes(), l.getDisassembly());
        }

        // Draw decoration
        int decoWidth = DECORATION_WIDTH * fontMetrics.getAverageCharWidth();
        gc.fillRectangle(0, y, decoWidth, totalLineHeight);
        if (l != null && breakpoints.contains(l.getAddress())) {
            gc.drawImage(breakpointImage, (decoWidth - breakpointImgWidth)/2, y + (fontMetrics.getAscent()  + fontMetrics.getLeading() - breakpointImgHeight)/2);
        }

        // Draw text
        if (s.length() < emptyLine.length()) {
            s += emptyLine.substring(s.length());
        }
        gc.drawText(s.substring(colOfs), DECORATION_WIDTH * fontMetrics.getAverageCharWidth(), y);
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

    private void showContextMenu(int x, int y) {
        int line = y / totalLineHeight + lineOfs;
        if (line < 0 || line >= lines.length) {
            return;
        }
        final int addr = lines[line].getAddress();
        Menu menu = new Menu (this.getShell(), SWT.POP_UP);
        final MenuItem item = new MenuItem (menu, SWT.CHECK);
        item.setText ("Enable breakpoint");
        item.setSelection(breakpoints.contains(addr));
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean enabled = item.getSelection();
                fireBreakpointChanged(addr, enabled);
            }});
        menu.setLocation(toDisplay(x, y));
        menu.setVisible(true);
        while (!menu.isDisposed () && menu.isVisible ()) {
            if (!this.getDisplay().readAndDispatch ()) this.getDisplay().sleep ();
        }
        menu.dispose ();
    }

    private void toggleBreakpoint(int x, int y) {
        int line = y / totalLineHeight + lineOfs;
        if (line < 0 || line >= lines.length) {
            return;
        }
        int addr = lines[line].getAddress();
        boolean enabled = breakpoints.contains(addr);
        fireBreakpointChanged(addr, !enabled);
    }

    private void fireBreakpointChanged(int addr, boolean enabled) {
        if (enabled) {
            breakpoints.add(addr);
        } else {
            breakpoints.remove(addr);
        }
        redraw();
        for (BreakpointChangedListener listener : listeners) {
            listener.breakpointChanged(addr, enabled);
        }
    }
}


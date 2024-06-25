#include "ui/cpu/i8049disassemblywidget.h"

#include <QVBoxLayout>
#include <QFontDatabase>
#include <QMouseEvent>
#include <QPainter>
#include <QScrollBar>

#include "ui/resources.h"

namespace kosmos_cp1::ui::cpu {

namespace {

const int kDecorationBorder =  8;
const char* kSeparator = "  ";

QColor kDecorationBg = QColor(Qt::lightGray);
QColor kDecorationBgDisabled = QColor(Qt::lightGray).lighter(120);

QColor kDisassemblyBg = QColor(Qt::white);
QColor kDisassemblyBgDisabled = QColor(Qt::lightGray).lighter(120);
QColor kDisassemblyBgSelected = QColor(Qt::red);

QColor kDisassemblyFg = QColor(Qt::black);
QColor kDisassemblyFgDisabled = QColor(Qt::lightGray).lighter(80);
QColor kDisassemblyFgSelected = QColor(Qt::yellow);
QColor kBreakpointFg = QColor(Qt::red);
QColor kBreakpointFgDisabled = QColor(Qt::lightGray).lighter(80);

}



I8049DisassemblyWidget::I8049DisassemblyWidget(const std::vector<std::uint8_t>& rom, ExecutorThread *executorThread, QWidget *parent)
    : QGroupBox("Disassembly", parent) {

    // Set up disassembly content
    scrollArea_ = new QScrollArea(this);
    content_ = new DisassemblyContent(rom, executorThread, scrollArea_);
    scrollArea_->setWidgetResizable(true);
    scrollArea_->setWidget(content_);

    // Set up final layout
    QVBoxLayout* layout = new QVBoxLayout();
    layout->addWidget(scrollArea_);

    setLayout(layout);
}




I8049DisassemblyWidget::~I8049DisassemblyWidget() {
}

void I8049DisassemblyWidget::setAddress(std::uint16_t addr) {
    content_->goTo(addr);
}


//// Move this into a util function
//std::optional<std::uint16_t> DisassemblyWidget::parseAddress(QString s) {
//    s = s.trimmed().toLower();
//    if (s == "pc") {
//        return content_->getPc();
//    }
//    int base = 16;
//    if (s.startsWith("+")) {
//        s = s.right(s.length()-1).trimmed();
//        base = 10;
//    }
//    bool ok;
//    int res = s.toInt(&ok, base);
//    return ok ? std::optional<std::uint16_t>(res) : std::nullopt;
//}

// ------------------------------------------------------------
//
// DisassemblyContent
//
// ------------------------------------------------------------

DisassemblyContent::DisassemblyContent(const std::vector<std::uint8_t>& rom, ExecutorThread *executorThread, QScrollArea* parent) :
    QWidget(parent), mouseDown_(false), highlightedLine_(-1), executorThread_(executorThread), scrollArea_(parent) {

    setFont(Resources::dejaVuSansFont());

    // Compute the size of the widget:
    QFontMetrics fm(Resources::dejaVuSansFont());
    lineH_ = fm.height();
    ascent_ = fm.ascent();
    decorationsW_ = fm.horizontalAdvance("    ");

    Disassembler disassembler(rom);
    lines_ = disassembler.disassemble(0, rom.size());
    for (int i = 0; i < lines_.size(); i++) {
        const Disassembler::Line& l = lines_[i];
        addressToLine_[l.addr] = i;
    }

    setMinimumWidth(decorationsW_ + 30 * fm.horizontalAdvance("0"));
    setMinimumHeight(lines_.size() * lineH_);
    setMaximumHeight(lines_.size() * lineH_);

}

QSize DisassemblyContent::sizeHint() const {
    return minimumSize();
}

void DisassemblyContent::mousePressEvent(QMouseEvent* event) {
    if (event->buttons() != Qt::LeftButton) {
        // Wrong mouse button, not interested
        event->ignore();
        return;
    }
    int x = event->position().x();
    if (x < 0 || x >= decorationsW_) {
        // Not in decorations area.
        event->ignore();
        return;
    }
    mouseDown_ = true;
    event->accept();
}

void DisassemblyContent::mouseReleaseEvent(QMouseEvent* event) {
    if (!mouseDown_) {
        // left button was never pressed, ignore.
        return;
    }

    int x = event->position().x();
    if (x < 0 || x >= decorationsW_) {
        // Not in decorations area.
        event->ignore();
        return;
    }

    int y = event->position().y();
    int lineIdx = y/lineH_;
    if (lineIdx >= lines_.size()) {
        // not a valid line
        event->ignore();
        return;
    }

    std::uint16_t addr = lines_[lineIdx].addr;
    auto it = breakpoints_.find(addr);
    if (it != breakpoints_.end()) {
        // We *do* have a breakpoint here! Remove it.
        executorThread_->enableBreakpoint(addr, false);
        breakpoints_.erase(it);
    } else {
        // No breakpoint, create one
        executorThread_->enableBreakpoint(addr, true);
        breakpoints_.insert(addr);
    }

    mouseDown_ = false;
    event->accept();

    update();
}

void DisassemblyContent::paintEvent(QPaintEvent* event) {
    QPainter painter(this);
    painter.setFont(Resources::dejaVuSansFont());
    painter.setBackgroundMode(Qt::OpaqueMode);

    int firstLine = event->rect().top()/lineH_;
    int lastLine = event->rect().bottom()/lineH_;

    for (int y = firstLine*lineH_ + ascent_, i = firstLine; i <= lastLine; ++i, y += lineH_) {
        paintLine(painter, event->rect(), i);
    }

}

void DisassemblyContent::paintLine(QPainter& painter, const QRect& updateRect, int lineIdx) {
    // Pick colors
    QColor decorationBg;
    QColor disassemblyBg;
    QColor disassemblyFg;
    QColor breakpointFg;
    if (isEnabled()) {
        bool isSelected = lineIdx == highlightedLine_;
        decorationBg = kDecorationBg;
        disassemblyBg = isSelected ? kDisassemblyBgSelected : kDisassemblyBg;
        disassemblyFg = isSelected ? kDisassemblyFgSelected : kDisassemblyFg;
        breakpointFg = kBreakpointFg;
    } else {
        decorationBg = kDecorationBgDisabled;
        disassemblyBg = kDisassemblyBgDisabled;
        disassemblyFg = kDisassemblyFgDisabled;
        breakpointFg = kBreakpointFgDisabled;
    }

    QRect decoR(0, lineIdx * lineH_, decorationsW_, lineH_);
    painter.fillRect(decoR, decorationBg);

    QRect lineR(decorationsW_, lineIdx * lineH_, updateRect.right(), lineH_);
    painter.fillRect(lineR, disassemblyBg);

    if (lineIdx >= lines_.size()) {
        return;
    }

    const Disassembler::Line& line = lines_[lineIdx];

    // Decorations
    auto it = breakpoints_.find(line.addr);
    if (it != breakpoints_.end()) {
        int cx = (decoR.left()+decoR.right())/2;
        int cy = (decoR.top()+decoR.bottom())/2;
        int radius = lineH_/2 - 3;

        painter.setPen(breakpointFg);
        painter.setBrush(breakpointFg);
        painter.drawEllipse(QPoint{cx,cy},radius,radius);
    }

    // Disassembly
    painter.setPen(disassemblyFg);
    painter.setBackground(disassemblyBg);
    QString str = QString::asprintf(" %04X: %s", line.addr, line.disassembly().c_str());
    painter.drawText(lineR.left(), lineR.top() + ascent_, str);
}

DisassemblyContent::~DisassemblyContent() {
}

void DisassemblyContent::enableControls(bool enable) {
    setEnabled(enable);
}

void DisassemblyContent::highlightLine(int line) {
    highlightedLine_ = line;

    int y = highlightedLine_ * lineH_ + ascent_;
    int x = scrollArea_->horizontalScrollBar()->value();

    scrollArea_->ensureVisible(x, y, 0, 50);
}

void DisassemblyContent::goTo(std::uint16_t addr) {
    auto it = addressToLine_.find(addr);
    while (addr > 0 && it == addressToLine_.end()) {
        addr--;
        it = addressToLine_.find(addr);
    }
    auto l = it->second;
    highlightLine(l);
    update();
}

} // namespace kosmos_cp1::ui::cpu


//public class DisassemblyComposite extends Composite {


//    private static final Logger logger = Logger.getLogger(DisassemblyComposite.class.getName());

//    private static final Color BG = SWTResources.WHITE;
//    private static final Color FG = SWTResources.BLACK;
//    private static final Color BG_SEL = SWTResources.RED;
//    private static final Color FG_SEL = SWTResources.YELLOW;

//    private static final int DECORATION_WIDTH = 2;
//    private static final int MARGIN = 1;
//    private static final int MAX_LINE_WIDTH = 30 + DECORATION_WIDTH + 2 * MARGIN; // Depends on the formatting

//    private final List<BreakpointChangedListener> listeners = new LinkedList<BreakpointChangedListener>();

//    private final Image breakpointImage = SWTResources.getImage("/com/asigner/cp1/ui/widgets/bullet_red.png");
//    private final int breakpointImgWidth = breakpointImage.getBounds().width;
//    private final int breakpointImgHeight = breakpointImage.getBounds().height;
//    private final FontMetrics fontMetrics;
//    private final int totalLineHeight;

//    private Disassembler.Line[] lines = new Disassembler.Line[0];
//    private int[] pcToLine;
//    private Set<Integer> breakpoints = new HashSet<Integer>();

//    private String emptyLine;
//    private int selectedLine;
//    private int selectedAddress;
//    private int lineOfs;
//    private int colOfs;

//    /**
//     * Create the composite.
//     * @param parent
//     * @param style
//     */
//    public DisassemblyComposite(Composite parent, int style) {
//        super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND);

//        Font terminalFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
//        GC gc = new GC(Display.getDefault());
//        gc.setFont(terminalFont);
//        fontMetrics = gc.getFontMetrics();
//        totalLineHeight = 6*fontMetrics.getHeight()/6;
//        gc.dispose();

//        setFont(terminalFont);

//        addControlListener(new ControlAdapter() {
//            @Override
//            public void controlResized(ControlEvent e) {
//                onResize();
//            }
//        });

//        addPaintListener(new PaintListener() {
//            @Override
//            public void paintControl(PaintEvent evt) {
//                paint(evt.gc);
//            }
//        });

//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseDoubleClick(MouseEvent e) {
//                toggleBreakpoint(e.x, e.y);
//            }

////            @Override
////            public void mouseDown(MouseEvent e) {
////                showContextMenu(e.x, e.y);
////            }
//        });

//        getVerticalBar().addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent evt) {
//                onVerticalScrollbarSelected(((ScrollBar)evt.getSource()).getSelection());
//            }});
//        getHorizontalBar().addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent evt) {
//                onHorizontalScrollbarSelected(((ScrollBar)evt.getSource()).getSelection());
//            }});
//    }

//    public void addListener(BreakpointChangedListener listener) {
//        listeners.add(listener);
//    }

//    public void removeListener(BreakpointChangedListener listener) {
//        listeners.remove(listener);
//    }

//    public int getSelectedAddress() {
//        return selectedAddress;
//    }

//    public void selectAddress(int address) {
//        if (this.selectedAddress == address) {
//            return;
//        }
//        if (isDisposed()) {
//            return;
//        }
//        this.selectedAddress = address;
//        this.selectedLine = pcToLine[address];
//        // make sure the selected line is visible
//        if (selectedLine < lineOfs) {
//            lineOfs = selectedLine;
//        } else if (selectedLine >= lineOfs + getVisibleLineCount()) {
//            lineOfs = selectedLine - getVisibleLineCount() / 2; // set it in the middle of the screen
//        }
//        getVerticalBar().setSelection(lineOfs);
//        redraw();
//    }

//    public void setRom(Rom rom) {
//        List<Disassembler.Line> lines = new Disassembler(rom).disassemble(0, rom.size());
//        this.lines = lines.toArray(new Disassembler.Line[lines.size()]);
//        this.pcToLine = new int[rom.size()];
//        int filledTo = rom.size();
//        for(int i = lines.size()-1; i>=0; i--) {
//            Disassembler.Line l = this.lines[i];
//            for(int j = l.getAddress(); j < filledTo; j++) {
//                pcToLine[j] = i;
//            }
//            filledTo = l.getAddress();
//        }
//        lineOfs = 0;
//        colOfs = 0;
//        selectedLine = 0;
//        selectedAddress = 0;
//        onResize();
//    }

//    @Override
//    protected void checkSubclass() {
//        // Disable the check that prevents subclassing of SWT components
//    }

//    @Override
//    public Point computeSize(int wHint, int hHint, boolean changed) {
//        int w = MAX_LINE_WIDTH * fontMetrics.getAverageCharWidth();
////        int lines = this.lines.length;
////        int h = lines * fontMetrics.getHeight() + (lines - 1) * fontMetrics.getHeight()/2;
//        return new Point(w, 0); // make sure we're small enough to be resized by the parent.
//    }

//    private void paint(GC gc) {
//        long start = System.currentTimeMillis();
//        int visibleLines = getVisibleLineCount();
//        for (int i = 0; i < visibleLines; i++) {
//            drawLine(gc, lineOfs + i, i * totalLineHeight);
//        }
//        long end = System.currentTimeMillis();
//        logger.finest("painting took " + (end-start) + " millis");
//    }

//    private void drawLine(GC gc, int line, int y) {
//        String s = "";
//        Disassembler.Line l = null;
//        if (line < lines.length) {
//            l = lines[line];
//            if (line == selectedLine) {
//                gc.setBackground(BG_SEL);
//                gc.setForeground(FG_SEL);
//            } else {
//                gc.setBackground(BG);
//                gc.setForeground(FG);
//            }

//            // draw Text
//            s = String.format("%04x: [ %s ] %s", l.getAddress(), l.getBytes(), l.getDisassembly());
//        }

//        // Draw decoration
//        int marginWidth = MARGIN * fontMetrics.getAverageCharWidth();
//        int decoWidth = DECORATION_WIDTH * fontMetrics.getAverageCharWidth();
//        gc.fillRectangle(marginWidth, y, decoWidth, totalLineHeight);
//        if (l != null && breakpoints.contains(l.getAddress())) {
//            gc.drawImage(breakpointImage, marginWidth + (decoWidth - breakpointImgWidth)/2, y + (fontMetrics.getAscent()  + fontMetrics.getLeading() - breakpointImgHeight)/2);
//        }

//        // Draw text
//        if (s.length() < emptyLine.length()) {
//            s += emptyLine.substring(s.length());
//        }
//        gc.drawText(s.substring(colOfs), (MARGIN + DECORATION_WIDTH) * fontMetrics.getAverageCharWidth(), y);
//    }

//    private void onVerticalScrollbarSelected(int selection) {
//        lineOfs = selection;
//        redraw();
//    }

//    private void onHorizontalScrollbarSelected(int selection) {
//        colOfs = selection;
//        redraw();
//    }

//    private void onResize() {
//        int visibleLines = getVisibleLineCount();
//        int lines = this.lines.length;

//        int visibleCols = getVisibleColCount();
//        int cols = MAX_LINE_WIDTH;
//        StringBuffer emptyLineBuf = new StringBuffer();
//        for (int i = 0; i < visibleCols; i++) {
//            emptyLineBuf.append(' ');
//        }
//        emptyLine = emptyLineBuf.toString();

//        ScrollBar vBar = this.getVerticalBar();
//        vBar.setMinimum(0);
//        vBar.setMaximum(lines);
//        vBar.setIncrement(1);
//        vBar.setPageIncrement(visibleLines);
//        vBar.setThumb(visibleLines);

//        ScrollBar hBar = this.getHorizontalBar();
//        hBar.setMinimum(0);
//        hBar.setMaximum(cols);
//        hBar.setIncrement(1);
//        hBar.setPageIncrement(visibleCols);
//        hBar.setThumb(visibleCols);

////        Rectangle r = this.getClientArea();
////        System.err.println(" ==== w = " + r.width + "  === h = " + r.height);
//    }

//    private int getVisibleLineCount() {
//        Rectangle clientArea = getClientArea();
//        return (clientArea.height + totalLineHeight - 1) / totalLineHeight;
//    }

//    private int getVisibleColCount() {
//        Rectangle clientArea = getClientArea();
//        return (clientArea.width + fontMetrics.getAverageCharWidth() - 1) / fontMetrics.getAverageCharWidth();
//    }

//    private void showContextMenu(int x, int y) {
//        int line = y / totalLineHeight + lineOfs;
//        if (line < 0 || line >= lines.length) {
//            return;
//        }
//        final int addr = lines[line].getAddress();
//        Menu menu = new Menu (this.getShell(), SWT.POP_UP);
//        final MenuItem item = new MenuItem (menu, SWT.CHECK);
//        item.setText ("Enable breakpoint");
//        item.setSelection(breakpoints.contains(addr));
//        item.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                boolean enabled = item.getSelection();
//                fireBreakpointChanged(addr, enabled);
//            }});
//        menu.setLocation(toDisplay(x, y));
//        menu.setVisible(true);
//        while (!menu.isDisposed () && menu.isVisible ()) {
//            if (!this.getDisplay().readAndDispatch ()) this.getDisplay().sleep ();
//        }
//        menu.dispose ();
//    }

//    private void toggleBreakpoint(int x, int y) {
//        int line = y / totalLineHeight + lineOfs;
//        if (line < 0 || line >= lines.length) {
//            return;
//        }
//        int addr = lines[line].getAddress();
//        boolean enabled = breakpoints.contains(addr);
//        fireBreakpointChanged(addr, !enabled);
//    }

//    private void fireBreakpointChanged(int addr, boolean enabled) {
//        if (enabled) {
//            breakpoints.add(addr);
//        } else {
//            breakpoints.remove(addr);
//        }
//        redraw();
//        for (BreakpointChangedListener listener : listeners) {
//            listener.breakpointChanged(addr, enabled);
//        }
//    }
//}


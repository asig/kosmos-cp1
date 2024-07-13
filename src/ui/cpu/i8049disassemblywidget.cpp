/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
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

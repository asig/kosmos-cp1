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

#pragma once

#include <set>

#include <QGroupBox>
#include <QScrollArea>

#include "emulation/intel8049.h"
#include "util/disassembler.h"
#include "executorthread.h"

namespace kosmos_cp1::ui::cpu {

using ::kosmos_cp1::emulation::Intel8049;
using ::kosmos_cp1::util::Disassembler;
using ::kosmos_cp1::ExecutorThread;

class DisassemblyContent;

class I8049DisassemblyWidget : public QGroupBox {
    Q_OBJECT

public:
    I8049DisassemblyWidget(const std::vector<std::uint8_t>& rom, ExecutorThread *executorThread, QWidget *parent = nullptr);
    virtual ~I8049DisassemblyWidget();

    void setAddress(std::uint16_t addr);

private:
    QScrollArea* scrollArea_;
    DisassemblyContent* content_;
};

class DisassemblyContent : public QWidget {
    Q_OBJECT

public:
    DisassemblyContent(const std::vector<std::uint8_t>& rom, ExecutorThread *executorThread, QScrollArea* parent);
    virtual ~DisassemblyContent();

    void highlightLine(int line);
    void goTo(std::uint16_t address);

    void updateDisassembly();

protected:
    QSize sizeHint() const override;
    void mousePressEvent(QMouseEvent* event) override;
    void mouseReleaseEvent(QMouseEvent* event) override;
    void paintEvent(QPaintEvent* event) override;

private:
    void paintLine(QPainter& painter, const QRect& updateRect, int line);

    void enableControls(bool enable);

    std::vector<Disassembler::Line> lines_;
    std::map<std::uint16_t, int> addressToLine_;
    std::set<std::uint16_t> breakpoints_;
    ExecutorThread *executorThread_;

    QScrollArea* scrollArea_;

    int highlightedLine_;
    bool mouseDown_;

    // Widths of parts of the widget
    int lineH_; // Height of a line
    int ascent_;
    int decorationsW_; // Width of decoration part
};

} // namespace kosmos_cp1::ui::cpu

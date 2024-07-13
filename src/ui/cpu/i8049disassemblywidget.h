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

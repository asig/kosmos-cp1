#pragma once

#include <QWidget>

#include "emulation/ram.h"

namespace kosmos_cp1::ui::cpu {

using kosmos_cp1::emulation::Ram;

class MemoryWidget : public QWidget
{
    Q_OBJECT
public:
    explicit MemoryWidget(const Ram *ram, QWidget *parent = nullptr);

signals:

protected:
    void paintEvent(QPaintEvent* event) override;

private:
    const Ram *ram_;

    int lineH_;
    int ascent_;
    int charW_;
    int lines_;

    int lastWritten_;

    void paintLine(QPainter& painter, int lineIdx);
};

} // namespace kosmos_cp1::ui::cpu

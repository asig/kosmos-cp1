#pragma once

#include <QGroupBox>
#include <QLabel>

#include "emulation/intel8049.h"
#include "ui/memorywidget.h"


namespace kosmos_cp1::ui {

using ::kosmos_cp1::emulation::Intel8049;


class I8049StatusWidget : public QGroupBox
{
    Q_OBJECT
public:
    explicit I8049StatusWidget(const QString& title, Intel8049 *cpu, QWidget *parent = nullptr);

    void updateState();

signals:

private:
    Intel8049 *cpu_;

    QLabel *cy_;
    QLabel *ac_;
    QLabel *f0_;
    QLabel *bs_;
    QLabel *sp_;
    QLabel *dbf_;
    QLabel *f1_;
    QLabel *a_;
    QLabel *t_;
    QLabel *pc_;
    QLabel *bus_;
    QLabel *port1_;
    QLabel *port2_;
    MemoryWidget *memory_;

};

} // namespace kosmos_cp1::ui


#pragma once

#include <QGroupBox>
#include <QLabel>
#include <QCheckBox>

#include "emulation/intel8155.h"
#include "ui/cpu/memorywidget.h"


namespace kosmos_cp1::ui::cpu {

using ::kosmos_cp1::emulation::Intel8155;


class I8155StatusWidget : public QGroupBox
{
    Q_OBJECT
public:
    explicit I8155StatusWidget(const QString& title, Intel8155 *pid, QWidget *parent = nullptr);

    void updateState();

signals:

private:
    Intel8155 *pid_;

    QLabel *portAMode_;
    QCheckBox *portAInterruptEnabled_;
    QLabel *portAVal_;

    QLabel *portBMode_;
    QCheckBox *portBInterruptEnabled_;
    QLabel *portBVal_;

    QLabel *portCMode_;
    QLabel *portCVal_;

    QCheckBox *ceLA_;
    QCheckBox *io_;
    QCheckBox *ale_;
    QCheckBox *rdLA_;
    QCheckBox *wrLA_;

    MemoryWidget *memory_;
};

} // namespace kosmos_cp1::ui::cpu


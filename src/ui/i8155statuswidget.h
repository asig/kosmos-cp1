#pragma once

#include <QGroupBox>

#include "emulation/ram.h"

namespace kosmos_cp1::ui {

using ::kosmos_cp1::emulation::Ram;

class I8155StatusWidget : public QGroupBox
{
    Q_OBJECT
public:
    explicit I8155StatusWidget(const QString& title, const Ram *ram, QWidget *parent = nullptr);

    void updateState();

signals:

};

} // namespace kosmos_cp1::ui


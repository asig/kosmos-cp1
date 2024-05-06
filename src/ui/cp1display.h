#pragma once

#include <cstdint>
#include <string>

#include <QWidget>

#include "ui/cp1sevensegmentwidget.h"
#include "emulation/intel8155.h"

namespace kosmos_cp1 {
namespace ui {

using std::uint8_t;

using ::kosmos_cp1::emulation::Intel8155;
using ::kosmos_cp1::emulation::Port;

class CP1Display : public QWidget
{
    Q_OBJECT
public:
    explicit CP1Display(Intel8155 *pid, QWidget *parent = nullptr);

    QSize sizeHint() const override;

    void display(const std::string& str);

public slots:
    void onPidPortWritten(Port port, uint8_t val);

signals:

protected:
    void paintEvent(QPaintEvent *event) override;

private:
    Intel8155 *pid_;
    CP1SevenSegmentWidget *digits_[6];

    uint8_t activeDigit_; // set by writes to Intel 8155's port C
    Port lastPortWritten_;


};

} // namespace ui
} // namespace kosmos_cp1

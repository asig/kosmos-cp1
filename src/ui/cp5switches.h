#ifndef KOSMOS_CP1_UI_CP5SWITCHES_H
#define KOSMOS_CP1_UI_CP5SWITCHES_H

#include <QWidget>

#include "ui/cp5switch.h"

namespace kosmos_cp1 {
namespace ui {

class CP5Switches : public QWidget
{
    Q_OBJECT
public:
    explicit CP5Switches(QWidget *parent = nullptr);

signals:

private:
    CP5Switch *switches_[8];

};

} // namespace ui
} // namespace kosmos_cp1

#endif // KOSMOS_CP1_UI_CP5SWITCHES_H

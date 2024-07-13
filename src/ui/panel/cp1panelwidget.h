#pragma once

#include <QWidget>

#include "ui/panel/cp1display.h"
#include "ui/panel/cp1keyboard.h"

namespace kosmos_cp1::ui::panel {

class CP1PanelWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP1PanelWidget(Intel8155 *pid, QWidget *parent = nullptr);

    CP1Display *cp1Display() {
        return display_;
    }

    CP1Keyboard *cp1Keyboard() {
        return keyboard_;
    }

private:
    CP1Display *display_;
    CP1Keyboard *keyboard_;
};

} // namespace kosmos_cp1::ui::panel

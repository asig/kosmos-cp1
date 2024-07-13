#include "ui/panel/cp1panelwidget.h"

#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QGridLayout>
#include <QSvgWidget>

namespace kosmos_cp1::ui::panel {

namespace {
constexpr const int kLogoWidth = 489;
constexpr const int kLogoHeight = 212;

}

CP1PanelWidget::CP1PanelWidget(Intel8155 *pid, QWidget *parent)
    : QWidget{parent}
{
    display_ = new CP1Display(pid, this);
    keyboard_ = new CP1Keyboard(this);

    QSvgWidget *logo = new QSvgWidget(":/ui/kosmos-logo.svg", this);
    // Make logo the same height as the display.
    int dispHeight = display_->size().height();
    QSize ls = logo->sizeHint();
    double r = ls.width()/(double)ls.height();
    logo->setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    logo->setFixedSize(dispHeight * r, dispHeight);


    QGridLayout *layout = new QGridLayout();
    layout->setContentsMargins(50,50,50,50);
    layout->setVerticalSpacing(50);

    layout->addWidget(display_, 0, 0);
    layout->addWidget(logo,0,1,Qt::AlignRight);
    layout->addWidget(keyboard_,1,0,1,2);

    setLayout(layout);
}

} // namespace kosmos_cp1::ui::panel

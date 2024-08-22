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

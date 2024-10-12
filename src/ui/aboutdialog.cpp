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

#include "aboutdialog.h"

#include <QVBoxLayout>
#include <QPushButton>
#include <QLabel>

#include "config.h"
#include "resources.h"

namespace kosmos_cp1::ui {

AboutDialog::AboutDialog(QWidget* parent) : QDialog(parent) {
    setupUI();
    setWindowTitle("About...");
}

void AboutDialog::setupUI() {
    QVBoxLayout* vLayout = new QVBoxLayout();

    auto logo = QPixmap(":/ui/about.png");
    auto logoLabel = new QLabel();
    logoLabel->setPixmap(logo);
    vLayout->addWidget(logoLabel, 0, Qt::AlignHCenter);

    QLabel *title = new QLabel("Kosmos CP1 Emulator");
    QFont font = Resources::aboutFont();
    font.setPixelSize(28);
    title->setFont(font);

    vLayout->addWidget(title, 0, Qt::AlignHCenter);
    vLayout->addWidget(new QLabel(QString::asprintf("Version %d.%d.%d", VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH)), 0, Qt::AlignHCenter);
    vLayout->addWidget(new QLabel("© 2024 Andreas Signer <asigner@gmail.com>"), 0, Qt::AlignHCenter);
    vLayout->addWidget(new QLabel("<a href=\"https://github.com/asig/kosmos-cp1\">https://github.com/asig/kosmos-cp1</a>"), 0, Qt::AlignHCenter);
    auto okBtn = new QPushButton("Ok");
    connect(okBtn, &QPushButton::clicked, this, [this] {
        accept();
    });

    vLayout->addWidget(okBtn, Qt::AlignHCenter);
    setLayout(vLayout);
}

}

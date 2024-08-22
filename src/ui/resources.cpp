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

#include "ui/resources.h"

#include <QFontDatabase>
#include <QString>
#include <QList>
#include <QColor>
#include <QFile>
#include <QIcon>
#include <QTextStream>
#include <QTemporaryFile>

namespace kosmos_cp1::ui {

QFont Resources::dejaVuSans_;
QFont Resources::about_;

void Resources::init() {
    dejaVuSans_ = makeFont(":/ui/fonts/DejaVuSansMono.ttf");
    about_ = makeFont("://ui/fonts/Ubuntu/Ubuntu-Bold.ttf");
}

QFont Resources::makeFont(const QString& path) {
    int id = QFontDatabase::addApplicationFont(path);
    QString family = QFontDatabase::applicationFontFamilies(id).at(0);
    return QFont(family);
}

const QFont& Resources::dejaVuSansFont() {
    return dejaVuSans_;
}

const QFont& Resources::aboutFont() {
    return about_;
}

}

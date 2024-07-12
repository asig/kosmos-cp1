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

#pragma once

#include <QFont>

namespace kosmos_cp1::ui {

class Resources {

public:
    static void init();
    static const QFont& dejaVuSansFont();

private:
    static QFont makeFont(const QString& path);

    static QFont dejaVuSans_;
};

}

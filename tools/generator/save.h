#pragma once

#include <string>
#include <QImage>

namespace kosmos_cp1::generator {

bool save(const QImage &image, const std::string &filename);

}

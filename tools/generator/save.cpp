#include "save.h"

#include <iostream>

namespace kosmos_cp1::generator {

bool save(const QImage &image, const std::string &filename) {
    std::string format = filename.substr(filename.find_last_of(".") + 1);

    std::cout << "Saving " << filename;
    bool res = image.save(filename.c_str(), format.c_str());
    std::cout << (res ? " succeeded." : " FAILED!") << std::endl;
    return res;
}

}

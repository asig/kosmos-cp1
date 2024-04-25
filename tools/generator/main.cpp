#include <QApplication>
#include <QSvgRenderer>
#include <QPainter>
#include <QImage>
#include <QDir>

#include <iostream>

#include "buttons.h"
#include "seven_segment.h"

int main(int argc, char **argv)
{
    // A QApplication instance is necessary if fonts are used in the SVG
    QApplication app(argc, argv);

    std::string root = "resources/ui";
    // Should be executed from root dir, but for debugging convenience, we
    // adjust for "build/debug"...
    for(int i = 0; i < 5; i++) {
        if (QDir(root.c_str()).exists()) {
            break;
        }
        root = "../" + root;
    }
    if (!QDir(root.c_str()).exists()) {
        std::cerr << "Can't find resources directory. Please run the generator in the root directory." << std::endl;
        exit(1);
    }

    std::cout << "Current directory is " << std::filesystem::current_path().string() << std::endl;
    std::cout << "Generating resources into " << root << std::endl;

    kosmos_cp1::generator::generate_buttons(root + "/buttons");
    kosmos_cp1::generator::generate_7segment(root + "/digits");

    return 0;
}

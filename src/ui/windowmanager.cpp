#include "ui/windowmanager.h"

#include "ui/basewindow.h"

namespace kosmos_cp1 {
namespace ui {

WindowManager::WindowManager(QObject *parent) : QObject{parent}
{
}

WindowManager::~WindowManager() {
}

void WindowManager::addWindow(BaseWindow* w) {
    windows_.push_back(w);
}

void WindowManager::closeAllWindows() {
    for (auto *w : windows_) {
        w->close();
    }
}

const std::vector<BaseWindow*>& WindowManager::windows() const {
    return windows_;
}


} // namespace ui
} // namespace kosmos_cp1

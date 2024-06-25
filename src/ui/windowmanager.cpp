#include "windowmanager.h"

namespace kosmos_cp1 {
namespace ui {

WindowManager::WindowManager(QObject *parent) : QObject{parent}
{
}

WindowManager::~WindowManager() {
}

void WindowManager::addWindow(QMainWindow* w) {
    windows_[w->winId()] = w;
}

} // namespace ui
} // namespace kosmos_cp1

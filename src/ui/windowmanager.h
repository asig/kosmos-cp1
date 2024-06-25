#pragma once

#include <unordered_map>

#include <QObject>
#include <QMainWindow>

namespace kosmos_cp1 {
namespace ui {

class WindowManager : public QObject
{
    Q_OBJECT
public:
    explicit WindowManager(QObject *parent = nullptr);
    virtual ~WindowManager();

    void addWindow(QMainWindow* w);

signals:

private:
    std::unordered_map<WId, QMainWindow*> windows_;
};

} // namespace ui
} // namespace kosmos_cp1


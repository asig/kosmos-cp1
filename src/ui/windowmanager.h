#pragma once

#include <vector>

#include <QObject>


namespace kosmos_cp1::ui {

class BaseWindow;

class WindowManager : public QObject
{
    Q_OBJECT
public:
    explicit WindowManager(QObject *parent = nullptr);
    virtual ~WindowManager();

    void addWindow(BaseWindow* w);
    void closeAllWindows();

    const std::vector<BaseWindow*>& windows() const;

signals:

private:
    std::vector<BaseWindow*> windows_;
};

} // namespace kosmos_cp1::ui


#pragma once

#include <QWidget>
#include <QString>

namespace kosmos_cp1 {
namespace ui {

class CP1Button : public QWidget
{
    Q_OBJECT
public:
    explicit CP1Button(const QString& str, int row, int col, QWidget *parent = nullptr);

    bool pressed() const {
        return pressed_;
    }

    int row() const {
        return row_;
    }

    int col() const {
        return col_;
    }

    void setPressed(bool pressed) {
        if (pressed == pressed_) return;
        pressed_ = pressed;
        if (pressed_) {
            emit keyPressed(this);
        } else {
            emit keyReleased(this);
        }
    }

signals:
    void keyPressed(CP1Button *btn);
    void keyReleased(CP1Button *btn);

protected:
    void mousePressEvent(QMouseEvent *event) override;
    void mouseMoveEvent(QMouseEvent *event) override;
    void mouseReleaseEvent(QMouseEvent *event) override;
    void paintEvent(QPaintEvent *event) override;

private:
    int row_;
    int col_;

    QImage img_;
    QImage imgPressed_;

    bool mouseOverControl_;
    bool pressed_;
};

} // namespace ui
} // namespace kosmos_cp1

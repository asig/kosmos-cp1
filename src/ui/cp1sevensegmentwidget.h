#pragma once

#include <cstdint>

#include <QWidget>

namespace kosmos_cp1 {
namespace ui {

using std::uint8_t;

//    --a--
//   |     |
//   f     b
//   |     |
//    --g--
//   |     |
//   e     c
//   |     |
//    --d--  .dp
//
// a == 0, b == 1, ... dp == 7

class CP1SevenSegmentWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP1SevenSegmentWidget(bool showDot = false, QWidget *parent = nullptr);

    void setShowDot(bool showDot) {
        if (showDot == showDot_) return;
        showDot_ = showDot;
        update();
    }

    void setSegments(uint8_t mask) {
        if (mask == mask_) return;
        mask_ = mask;
        update();
    }

    QSize sizeHint() const override;

signals:

protected:
    void paintEvent(QPaintEvent *event) override;

private:
    bool showDot_;
    uint8_t mask_;

    int w_;
    int h_;
};

} // namespace ui
} // namespace kosmos_cp1

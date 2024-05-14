#pragma once

#include <QWidget>

#include "fmt/format.h"
#include "ui/cp1button.h"

namespace kosmos_cp1 {
namespace ui {

/**
 * Keyboard matrix according to the schematics:
 *
 *
 *   3-----2-----1-----0-------------------------   Row 4
 *   |     |     |     |
 *   7-----6-----5-----4-------------------------   Row 3
 *   |     |     |     |
 *  INP---OUT----9-----8-------------------------   Row 2
 *   |     |     |     |
 *  RUN---STP--STEP---CAS------------------------   Row 1
 *   |     |     |     |
 *  ACC----PC---CLR---CAL------------------------   Row 0
 *   |     |     |     |
 *   |     |     |     |
 *   |     |     |     |
 *  Col   Col   Col   Col
 *   3     2     1     0
 *
 *
 * However, it seems that CAS and CAL are actually swapped. This is
 * also visible when you compare the picture of the CP1 printed
 * on the original box (CAL in the middle line, and CAS in the top
 * line) with the actual computer (CAS in the middle line, CAL
 * in the top line).
 * Therefore, we use row 0 for CAS and row 1 for CAL.
 */

class CP1Keyboard : public QWidget
{
    Q_OBJECT

private:
    enum {
        BTN_0    = 0,
        BTN_1    = 1,
        BTN_2    = 2,
        BTN_3    = 3,
        BTN_4    = 4,
        BTN_5    = 5,
        BTN_6    = 6,
        BTN_7    = 7,
        BTN_8    = 8,
        BTN_9    = 9,
        BTN_OUT  = 10,
        BTN_INP  = 11,
        BTN_CAL  = 12,
        BTN_STEP = 13,
        BTN_STP  = 14,
        BTN_RUN  = 15,
        BTN_CAS  = 16,
        BTN_CLR  = 17,
        BTN_PC   = 18,
        BTN_ACC  = 19,
        BTNS_SIZE = 20,
    };

    enum {
        kRows = 6,
    };

public:
    explicit CP1Keyboard(QWidget *parent = nullptr);

    uint8_t keyMask(int row) const {
        // qDebug() << fmt::format("keyMask[{}] == {:02x}",row, keyMask_[row]).c_str();
        return keyMask_[row];
    }

public slots:
    void onKeyPressed(CP1Button *btn);
    void onKeyReleased(CP1Button *btn);

signals:

protected:
    void paintEvent(QPaintEvent *event) override;
    void keyPressEvent(QKeyEvent *event) override;
    void keyReleaseEvent(QKeyEvent *event) override;

private:
    CP1Button *makeBtn(const QString& str, int row, int col, int btnCode = -1);
    bool handleKey(QKeyEvent *e, bool pressed);

    uint8_t keyMask_[kRows];
    CP1Button *buttons_[BTNS_SIZE];
};

} // namespace ui
} // namespace kosmos_cp1

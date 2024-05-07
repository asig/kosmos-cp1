#include "cp1keyboard.h"

#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QPainter>
#include <QPainterPath>
#include <QPalette>

#include "ui/cp1colors.h"

namespace kosmos_cp1 {
namespace ui {

CP1Keyboard::CP1Keyboard(QWidget *parent)
    : QWidget{parent}
{
    QHBoxLayout *topLayout = new QHBoxLayout();

    QWidget *left = new QWidget();
    QVBoxLayout *leftLayout = new QVBoxLayout();
    leftLayout->setSpacing(10);

    QHBoxLayout *leftRow0 = new QHBoxLayout();
    leftRow0->addWidget(makeBtn("0", 4, 0));
    leftRow0->addWidget(makeBtn("1", 4, 1));
    leftRow0->addWidget(makeBtn("2", 4, 2));
    leftRow0->addWidget(makeBtn("3", 4, 3));
    leftRow0->addWidget(makeBtn("4", 3, 0));
    leftRow0->addWidget(makeBtn("5", 3, 1));
    leftRow0->addWidget(makeBtn("6", 3, 2));
    leftRow0->addWidget(makeBtn("7", 3, 3));
    leftRow0->addWidget(makeBtn("8", 2, 0));
    leftRow0->addWidget(makeBtn("9", 2, 1));
    leftLayout->addLayout(leftRow0);

    QHBoxLayout *leftRow1 = new QHBoxLayout();
    leftRow1->addStretch();
    leftRow1->addWidget(makeBtn("step", 1, 1, BTN_STEP));
    leftRow1->addWidget(makeBtn("stp", 1, 2, BTN_STP));
    leftRow1->addWidget(makeBtn("run", 1, 3, BTN_RUN));
    leftRow1->addWidget(makeBtn("cal", 1, 9, BTN_CAL));
    leftRow1->addStretch();
    leftLayout->addLayout(leftRow1);

    QHBoxLayout *leftRow2 = new QHBoxLayout();
    leftRow2->addStretch();
    leftRow2->addWidget(makeBtn("clr", 0, 1, BTN_CLR));
    leftRow2->addWidget(makeBtn("acc", 0, 3, BTN_ACC));
    leftRow2->addWidget(makeBtn("cas", 0, 0, BTN_CAS));
    leftRow2->addStretch();
    leftLayout->addLayout(leftRow2);

    QHBoxLayout *leftRow3 = new QHBoxLayout();
    leftRow3->addStretch();
    leftRow3->addWidget(makeBtn("pc", 0, 2, BTN_PC));
    leftRow3->addWidget(makeBtn("out", 2, 2, BTN_OUT));
    leftRow3->addWidget(makeBtn("inp", 2, 3, BTN_INP));
    leftRow3->addStretch();
    leftLayout->addLayout(leftRow3);

    left->setLayout(leftLayout);

    QWidget *right = new QWidget();
    QVBoxLayout *rightLayout = new QVBoxLayout();
    rightLayout->setSpacing(10);

    QHBoxLayout *rightRow0 = new QHBoxLayout();
    rightRow0->addWidget(makeBtn("7w", 3, 3, BTN_7));
    rightRow0->addWidget(makeBtn("8w", 2, 0, BTN_8));
    rightRow0->addWidget(makeBtn("9w", 2, 1, BTN_9));
    rightLayout->addLayout(rightRow0);

    QHBoxLayout *rightRow1 = new QHBoxLayout();
    rightRow1->addWidget(makeBtn("4w", 3, 0, BTN_4));
    rightRow1->addWidget(makeBtn("5w", 3, 1, BTN_5));
    rightRow1->addWidget(makeBtn("6w", 3, 2, BTN_6));
    rightLayout->addLayout(rightRow1);

    QHBoxLayout *rightRow2 = new QHBoxLayout();
    rightRow2->addWidget(makeBtn("1w", 4, 1, BTN_1));
    rightRow2->addWidget(makeBtn("2w", 4, 2, BTN_2));
    rightRow2->addWidget(makeBtn("3w", 4, 3, BTN_3));
    rightLayout->addLayout(rightRow2);

    QHBoxLayout *rightRow3 = new QHBoxLayout();
    rightRow3->addWidget(makeBtn("0w", 4, 0, BTN_0), Qt::AlignLeft);
    rightRow3->addStretch();
    rightLayout->addLayout(rightRow3);
    right->setLayout(rightLayout);

    topLayout->addWidget(left);
    topLayout->addWidget(right);
    topLayout->setContentsMargins(MARGIN_WIDTH,MARGIN_WIDTH,MARGIN_WIDTH,MARGIN_WIDTH);
    topLayout->setSpacing(40);


    setLayout(topLayout);
    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);

//    QPalette pal = QPalette();
//    pal.setColor(QPalette::Window, CP1Color::PANEL_BACKGROUND);
//    setAutoFillBackground(true);
//    setPalette(pal);
}

CP1Button* CP1Keyboard::makeBtn(const QString& str, int row, int col, int btnCode) {
    CP1Button *btn = new CP1Button(str, row, col);
    connect(btn, &CP1Button::keyPressed, this, &CP1Keyboard::onKeyPressed);
    connect(btn, &CP1Button::keyReleased, this, &CP1Keyboard::onKeyReleased);
    if (btnCode != -1) {
        buttons_[btnCode] = btn;
    }
    return btn;
}



void CP1Keyboard::paintEvent(QPaintEvent *) {
    QPainter painter(this);
    QRect r = rect();

    int x = r.x();
    int y = r.y();
    int w = r.width();
    int h = r.height();

    painter.setBrush(CP1Color::PANEL_BACKGROUND);

    // Draw green corners
    painter.fillRect(x, y, w, MARGIN_WIDTH, CP1Color::GREEN);
    painter.fillRect(x, y + w - MARGIN_WIDTH, w, MARGIN_WIDTH, CP1Color::GREEN);
    painter.fillRect(x, y, MARGIN_WIDTH, h, CP1Color::GREEN);
    painter.fillRect(x + w - MARGIN_WIDTH, y , MARGIN_WIDTH, h, CP1Color::GREEN);

    // Draw full border in light color
    painter.setPen(QPen(CP1Color::GREEN_LIGHT, MARGIN_WIDTH));
    painter.drawRoundedRect(x+MARGIN_WIDTH/2, y+MARGIN_WIDTH/2, w-MARGIN_WIDTH, h-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);

    // redraw top left in dark color
    QPainterPath path;
    path.moveTo(x + 2*MARGIN_WIDTH, y + h - 2*MARGIN_WIDTH);
    path.lineTo(x , y + h);
    path.lineTo(x , y);
    path.lineTo(x + w, y);
    path.lineTo(x + w - 2*MARGIN_WIDTH, y + 2*MARGIN_WIDTH);
    path.closeSubpath();

    painter.setClipPath(path);
    painter.setPen(QPen(CP1Color::GREEN_DARK, MARGIN_WIDTH));
    painter.drawRoundedRect(x+MARGIN_WIDTH/2, y+MARGIN_WIDTH/2, w-MARGIN_WIDTH, h-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);
}

void CP1Keyboard::onKeyPressed(CP1Button *btn) {
    keyMask_[btn->row()] |= (1 << btn->col());
}

void CP1Keyboard::onKeyReleased(CP1Button *btn) {
    keyMask_[btn->row()] &= ~(1 << btn->col());
}






    //        super(parent, style);
    //        GridLayout gridLayout = new GridLayout(2, false);
    //        gridLayout.marginRight = MARGIN_WIDTH;
    //        gridLayout.marginTop = MARGIN_WIDTH;
    //        gridLayout.marginLeft = MARGIN_WIDTH;
    //        gridLayout.marginBottom = MARGIN_WIDTH;
    //        gridLayout.horizontalSpacing = 40;
    //        setLayout(gridLayout);
    //        this.setBackground(CP1Colors.PANEL_BACKGROUND);
    //        this.addPaintListener(this::paint);

    //        Composite composite = new Composite(this, SWT.NONE);
    //        composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    //        GridLayout gl_composite = new GridLayout(10, true);
    //        gl_composite.horizontalSpacing = 10;
    //        composite.setLayout(gl_composite);
    //        composite.setBackground(CP1Colors.PANEL_BACKGROUND);
    //        {
    //        }


    //        Composite rh_composite = new Composite(this, SWT.NONE);
    //        rh_composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
    //        GridLayout rh_gl_composite = new GridLayout(3, false);
    //        rh_gl_composite.horizontalSpacing = 10;

    //        rh_composite.setLayout(rh_gl_composite);
    //        rh_composite.setBackground(CP1Colors.PANEL_BACKGROUND);

    //        Composite composite_1 = new Composite(this, SWT.NONE);
    //        composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    //        GridLayout gl_composite_1 = new GridLayout(4, true);
    //        gl_composite_1.horizontalSpacing = 10;
    //        composite_1.setLayout(gl_composite_1);
    //        composite_1.setBackground(CP1Colors.PANEL_BACKGROUND);

    //        Composite rh_composite_1 = new Composite(this, SWT.NONE);
    //        GridLayout rh_gl_composite_1 = new GridLayout(3, false);
    //        rh_gl_composite_1.horizontalSpacing = 10;
    //        rh_composite_1.setLayout(rh_gl_composite_1);
    //        rh_composite_1.setBackground(CP1Colors.PANEL_BACKGROUND);


    //        Composite composite_2 = new Composite(this, SWT.NONE);
    //        composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    //        GridLayout gl_composite_2 = new GridLayout(3, false);
    //        gl_composite_2.horizontalSpacing = 10;
    //        composite_2.setLayout(gl_composite_2);
    //        composite_2.setBackground(CP1Colors.PANEL_BACKGROUND);

    //        Composite rh_composite_2 = new Composite(this, SWT.NONE);
    //        GridLayout rh_gl_composite_2 = new GridLayout(3, false);
    //        rh_gl_composite_2.horizontalSpacing = 10;
    //        rh_composite_2.setLayout(rh_gl_composite_2);
    //        rh_composite_2.setBackground(CP1Colors.PANEL_BACKGROUND);


    //        Composite composite_3 = new Composite(this, SWT.NONE);
    //        composite_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
    //        GridLayout gl_composite_3 = new GridLayout(3, false);
    //        gl_composite_3.horizontalSpacing = 10;
    //        composite_3.setLayout(gl_composite_3);
    //        composite_3.setBackground(CP1Colors.PANEL_BACKGROUND);

    //        Composite rh_composite_3 = new Composite(this, SWT.NONE);
    //        GridLayout rh_gl_composite_3 = new GridLayout(3, false);
    //        rh_gl_composite_3.horizontalSpacing = 10;
    //        rh_composite_3.setLayout(rh_gl_composite_3);
    //        rh_composite_3.setBackground(CP1Colors.PANEL_BACKGROUND);
    //        new Label(rh_composite_3, SWT.NONE);
    //        new Label(rh_composite_3, SWT.NONE);

    //        this.addKeyListener(new KeyAdapter() {
    //            @Override
    //                public void keyPressed(KeyEvent e) {
    //                if (!handleKey(e, true)) {
    //                    super.keyPressed(e);
    //                }
    //            }

    //            @Override
    //                public void keyReleased(KeyEvent e) {
    //                if (!handleKey(e, false)) {
    //                    super.keyReleased(e);
    //                };
    //            }

    //        private boolean handleKey(KeyEvent e, boolean pressState) {
    //                CP1Button btn = null;
    //                switch(e.character) {
    //                case '0':
    //                case '1':
    //                case '2':
    //                case '3':
    //                case '4':
    //                case '5':
    //                case '6':
    //                case '7':
    //                case '8':
    //                case '9':
    //                    btn = buttons[e.character - '0'];
    //                    break;
    //                case 'o':
    //                    btn = buttons[BTN_OUT];
    //                    break;
    //                case '\n':
    //                case '\r':
    //                    btn = buttons[BTN_INP];
    //                    break;
    //                case 'l':
    //                    btn = buttons[BTN_CAL];
    //                    break;
    //                case 't':
    //                    btn = buttons[BTN_STEP];
    //                    break;
    //                case '.':
    //                    btn = buttons[BTN_STP];
    //                    break;
    //                case 'r':
    //                    btn = buttons[BTN_RUN];
    //                    break;
    //                case 's':
    //                    btn = buttons[BTN_CAS];
    //                    break;
    //                case '\b':
    //                case 0x7f:
    //                    btn = buttons[BTN_CLR];
    //                    break;
    //                case 'p':
    //                    btn = buttons[BTN_PC];
    //                    break;
    //                case 'a':
    //                    btn = buttons[BTN_ACC];
    //                    break;
    //                }
    //                if (btn != null) {
    //                    e.doit = false;
    //                    btn.setPressed(pressState);
    //                }
    //                return btn != null;
    //            }
    //        });


} // namespace ui
} // namespace kosmos_cp1


//private static final int BTNS_SIZE = 20;

//private static final Logger logger = Logger.getLogger(CP1Keyboard.class.getName());

//private static final int BUTTON_HEIGHT = 50;
//private static final int MARGIN_WIDTH = 10;
//private static final int ARC_WIDTH = 30;

//private int keyMask[] = new int[6];
//private CP1Button buttons[] = new CP1Button[BTNS_SIZE];

//private class KeyListener implements CP1Button.KeyListener {
//    private final int row;
//    private final int col;

//    public KeyListener(int row, int col) {
//            this.row = row;
//            this.col = col;
//        }

//        @Override
//            public void keyPressed(CP1Button btn) {
//            keyMask[row] |= (1 << col);
//            logger.finest(String.format("Key pressed: row %d, col %d", row, col));
//            System.out.println(String.format("Key pressed: row %d, col %d", row, col));
//            System.out.flush();
//        }

//        @Override
//            public void keyReleased(CP1Button btn) {
//            keyMask[row] &= ~(1 << col);
//            logger.finest(String.format("Key released: row %d, col %d", row, col));
//            System.out.println(String.format("Key released: row %d, col %d", row, col));
//            System.out.flush();
//        }
//    }

//    /**
//     * Create the composite.
//     * @param parent
//     * @param style
//     */
//public CP1Keyboard(Composite parent, int style) {
//        super(parent, style);
//        GridLayout gridLayout = new GridLayout(2, false);
//        gridLayout.marginRight = MARGIN_WIDTH;
//        gridLayout.marginTop = MARGIN_WIDTH;
//        gridLayout.marginLeft = MARGIN_WIDTH;
//        gridLayout.marginBottom = MARGIN_WIDTH;
//        gridLayout.horizontalSpacing = 40;
//        setLayout(gridLayout);
//        this.setBackground(CP1Colors.PANEL_BACKGROUND);
//        this.addPaintListener(this::paint);

//        Composite composite = new Composite(this, SWT.NONE);
//        composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
//        GridLayout gl_composite = new GridLayout(10, true);
//        gl_composite.horizontalSpacing = 10;
//        composite.setLayout(gl_composite);
//        composite.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {


//        Composite rh_composite = new Composite(this, SWT.NONE);
//        rh_composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
//        GridLayout rh_gl_composite = new GridLayout(3, false);
//        rh_gl_composite.horizontalSpacing = 10;

//        rh_composite.setLayout(rh_gl_composite);
//        rh_composite.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(rh_composite, "7w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 3));
//            buttons[BTN_7] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(rh_composite, "8w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(2, 0));
//            buttons[BTN_8] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(rh_composite, "9w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(2, 1));
//            buttons[BTN_9] = btn;
//        }

//        Composite composite_1 = new Composite(this, SWT.NONE);
//        composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
//        GridLayout gl_composite_1 = new GridLayout(4, true);
//        gl_composite_1.horizontalSpacing = 10;
//        composite_1.setLayout(gl_composite_1);
//        composite_1.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(composite_1, "step", SWT.NONE);
//            btn.addKeyListener(new KeyListener(1, 1));
//            buttons[BTN_STEP] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_1, "stp", SWT.NONE);
//            btn.addKeyListener(new KeyListener(1, 2));
//            buttons[BTN_STP] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_1, "run", SWT.NONE);
//            btn.addKeyListener(new KeyListener(1, 3));
//            buttons[BTN_RUN] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_1, "cal", SWT.NONE);
//            btn.addKeyListener(new KeyListener(1, 0));
//            buttons[BTN_CAL] = btn;
//        }

//        Composite rh_composite_1 = new Composite(this, SWT.NONE);
//        GridLayout rh_gl_composite_1 = new GridLayout(3, false);
//        rh_gl_composite_1.horizontalSpacing = 10;
//        rh_composite_1.setLayout(rh_gl_composite_1);
//        rh_composite_1.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(rh_composite_1, "4w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 0));
//            buttons[BTN_4] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(rh_composite_1, "5w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 1));
//            buttons[BTN_5] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(rh_composite_1, "6w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 2));
//            buttons[BTN_6] = btn;
//        }


//        Composite composite_2 = new Composite(this, SWT.NONE);
//        composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
//        GridLayout gl_composite_2 = new GridLayout(3, false);
//        gl_composite_2.horizontalSpacing = 10;
//        composite_2.setLayout(gl_composite_2);
//        composite_2.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(composite_2, "clr", SWT.NONE);
//            btn.addKeyListener(new KeyListener(0, 1));
//            buttons[BTN_CLR] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_2, "acc", SWT.NONE);
//            btn.addKeyListener(new KeyListener(0, 3));
//            buttons[BTN_ACC] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_2, "cas", SWT.NONE);
//            btn.addKeyListener(new KeyListener(0, 0));
//            buttons[BTN_CAS] = btn;
//        }

//        Composite rh_composite_2 = new Composite(this, SWT.NONE);
//        GridLayout rh_gl_composite_2 = new GridLayout(3, false);
//        rh_gl_composite_2.horizontalSpacing = 10;
//        rh_composite_2.setLayout(rh_gl_composite_2);
//        rh_composite_2.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(rh_composite_2, "1w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 1));
//            buttons[BTN_1] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(rh_composite_2, "2w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 2));
//            buttons[BTN_2] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(rh_composite_2, "3w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 3));
//            buttons[BTN_3] = btn;
//        }


//        Composite composite_3 = new Composite(this, SWT.NONE);
//        composite_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
//        GridLayout gl_composite_3 = new GridLayout(3, false);
//        gl_composite_3.horizontalSpacing = 10;
//        composite_3.setLayout(gl_composite_3);
//        composite_3.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(composite_3, "pc", SWT.NONE);
//            btn.addKeyListener(new KeyListener(0, 2));
//            buttons[BTN_PC] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_3, "out", SWT.NONE);
//            btn.addKeyListener(new KeyListener(2, 2));
//            buttons[BTN_OUT] = btn;
//        }
//        {
//            CP1Button btn = new CP1Button(composite_3, "inp", SWT.NONE);
//            btn.addKeyListener(new KeyListener(2, 3));
//            buttons[BTN_INP] = btn;
//        }

//        Composite rh_composite_3 = new Composite(this, SWT.NONE);
//        GridLayout rh_gl_composite_3 = new GridLayout(3, false);
//        rh_gl_composite_3.horizontalSpacing = 10;
//        rh_composite_3.setLayout(rh_gl_composite_3);
//        rh_composite_3.setBackground(CP1Colors.PANEL_BACKGROUND);
//        {
//            CP1Button btn = new CP1Button(rh_composite_3, "0w", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 0));
//            buttons[BTN_0] = btn;
//        }
//        new Label(rh_composite_3, SWT.NONE);
//        new Label(rh_composite_3, SWT.NONE);

//        this.addKeyListener(new KeyAdapter() {
//            @Override
//                public void keyPressed(KeyEvent e) {
//                if (!handleKey(e, true)) {
//                    super.keyPressed(e);
//                }
//            }

//            @Override
//                public void keyReleased(KeyEvent e) {
//                if (!handleKey(e, false)) {
//                    super.keyReleased(e);
//                };
//            }

//        private boolean handleKey(KeyEvent e, boolean pressState) {
//                CP1Button btn = null;
//                switch(e.character) {
//                case '0':
//                case '1':
//                case '2':
//                case '3':
//                case '4':
//                case '5':
//                case '6':
//                case '7':
//                case '8':
//                case '9':
//                    btn = buttons[e.character - '0'];
//                    break;
//                case 'o':
//                    btn = buttons[BTN_OUT];
//                    break;
//                case '\n':
//                case '\r':
//                    btn = buttons[BTN_INP];
//                    break;
//                case 'l':
//                    btn = buttons[BTN_CAL];
//                    break;
//                case 't':
//                    btn = buttons[BTN_STEP];
//                    break;
//                case '.':
//                    btn = buttons[BTN_STP];
//                    break;
//                case 'r':
//                    btn = buttons[BTN_RUN];
//                    break;
//                case 's':
//                    btn = buttons[BTN_CAS];
//                    break;
//                case '\b':
//                case 0x7f:
//                    btn = buttons[BTN_CLR];
//                    break;
//                case 'p':
//                    btn = buttons[BTN_PC];
//                    break;
//                case 'a':
//                    btn = buttons[BTN_ACC];
//                    break;
//                }
//                if (btn != null) {
//                    e.doit = false;
//                    btn.setPressed(pressState);
//                }
//                return btn != null;
//            }
//        });
//    }

//public int getKeyMask(int row) {
//        return keyMask[row];
//    }


//    @Override
//        protected void checkSubclass() {
//        // Disable the check that prevents subclassing of SWT components
//    }
//}

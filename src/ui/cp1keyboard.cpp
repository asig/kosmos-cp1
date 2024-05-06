#include "cp1keyboard.h"

namespace kosmos_cp1 {
namespace ui {

CP1Keyboard::CP1Keyboard(QWidget *parent)
    : QWidget{parent}
{

}

} // namespace ui
} // namespace kosmos_cp1


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


//public class CP1Keyboard extends Composite {

//private static final int BTN_0    = 0;
//private static final int BTN_1    = 1;
//private static final int BTN_2    = 2;
//private static final int BTN_3    = 3;
//private static final int BTN_4    = 4;
//private static final int BTN_5    = 5;
//private static final int BTN_6    = 6;
//private static final int BTN_7    = 7;
//private static final int BTN_8    = 8;
//private static final int BTN_9    = 9;
//private static final int BTN_OUT  = 10;
//private static final int BTN_INP  = 11;
//private static final int BTN_CAL  = 12;
//private static final int BTN_STEP = 13;
//private static final int BTN_STP  = 14;
//private static final int BTN_RUN  = 15;
//private static final int BTN_CAS  = 16;
//private static final int BTN_CLR  = 17;
//private static final int BTN_PC   = 18;
//private static final int BTN_ACC  = 19;
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
//            CP1Button btn = new CP1Button(composite, "0", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 0));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "1", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 1));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "2", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 2));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "3", SWT.NONE);
//            btn.addKeyListener(new KeyListener(4, 3));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "4", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 0));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "5", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 1));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "6", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 2));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "7", SWT.NONE);
//            btn.addKeyListener(new KeyListener(3, 3));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "8", SWT.NONE);
//            btn.addKeyListener(new KeyListener(2, 0));
//        }
//        {
//            CP1Button btn = new CP1Button(composite, "9", SWT.NONE);
//            btn.addKeyListener(new KeyListener(2, 1));
//        }


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

//private void paint(PaintEvent paintEvent) {
//        GC gc = paintEvent.gc;
//        Rectangle r = getClientArea();

//        // Draw green corners
//        gc.setBackground(CP1Colors.GREEN);
//        gc.fillRectangle(r.x, r.y, r.width, MARGIN_WIDTH);
//        gc.fillRectangle(r.x, r.y + r.height - MARGIN_WIDTH, r.width, MARGIN_WIDTH);
//        gc.fillRectangle(r.x, r.y, MARGIN_WIDTH, r.height);
//        gc.fillRectangle(r.x + r.width - MARGIN_WIDTH, r.y , MARGIN_WIDTH, r.height);

//        // Draw full border in light color
//        gc.setLineWidth(MARGIN_WIDTH);
//        gc.setForeground(CP1Colors.GREEN_LIGHT);
//        gc.drawRoundRectangle(r.x+MARGIN_WIDTH/2, r.y+MARGIN_WIDTH/2, r.width-MARGIN_WIDTH, r.height-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);

//        // redraw top left in dark color
//        Path path = new Path(gc.getDevice());
//        path.moveTo(r.x + 2*MARGIN_WIDTH, r.y + r.height - 2*MARGIN_WIDTH);
//        path.lineTo(r.x , r.y + r.height);
//        path.lineTo(r.x , r.y);
//        path.lineTo(r.x + r.width, r.y);
//        path.lineTo(r.x + r.width - 2*MARGIN_WIDTH, r.y + 2*MARGIN_WIDTH);
//        path.close();
//        gc.setClipping(path);
//        gc.setForeground(CP1Colors.GREEN_DARK);
//        gc.drawRoundRectangle(r.x+MARGIN_WIDTH/2, r.y+MARGIN_WIDTH/2, r.width-MARGIN_WIDTH, r.height-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);
//        path.dispose();
//    }

//    @Override
//        protected void checkSubclass() {
//        // Disable the check that prevents subclassing of SWT components
//    }
//}

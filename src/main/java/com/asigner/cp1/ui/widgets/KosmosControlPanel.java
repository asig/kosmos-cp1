// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.logging.Logger;

/**
 * Keyboard matrix:
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
 */


public class KosmosControlPanel extends Composite {

    private static final Logger logger = Logger.getLogger(KosmosControlPanel.class.getName());

    private static final int BUTTON_HEIGHT = 50;
    private static final int MARGIN_WIDTH = 10;
    private static final int ARC_WIDTH = 30;

    private int keyMask[] = new int[6];

    private CP1Button digitButtons[] = new CP1Button[10];

    private class KeyListener implements CP1Button.KeyListener {
        private final int row;
        private final int col;

        public KeyListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void keyPressed(CP1Button btn) {
            keyMask[row] |= (1 << col);
            logger.finest(String.format("Key pressed: row %d, col %d", row, col));
        }

        @Override
        public void keyReleased(CP1Button btn) {
            keyMask[row] &= ~(1 << col);
            logger.finest(String.format("Key released: row %d, col %d", row, col));
        }
    }

    /**
     * Create the composite.
     * @param parent
     * @param style
     */
    public KosmosControlPanel(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginRight = MARGIN_WIDTH;
        gridLayout.marginTop = MARGIN_WIDTH;
        gridLayout.marginLeft = MARGIN_WIDTH;
        gridLayout.marginBottom = MARGIN_WIDTH;
        gridLayout.horizontalSpacing = 40;
        setLayout(gridLayout);
        this.setBackground(CP1Colors.PANEL_BACKGROUND);
        this.addPaintListener(this::paint);

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        GridLayout gl_composite = new GridLayout(10, true);
        gl_composite.horizontalSpacing = 10;
        composite.setLayout(gl_composite);
        composite.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("0");
            btn.addKeyListener(new KeyListener(4, 0));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("1");
            btn.addKeyListener(new KeyListener(4, 1));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("2");
            btn.addKeyListener(new KeyListener(4, 2));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("3");
            btn.addKeyListener(new KeyListener(4, 3));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("4");
            btn.addKeyListener(new KeyListener(3, 0));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("5");
            btn.addKeyListener(new KeyListener(3, 1));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("6");
            btn.addKeyListener(new KeyListener(3, 2));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("7");
            btn.addKeyListener(new KeyListener(3, 3));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("8");
            btn.addKeyListener(new KeyListener(2, 0));
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("9");
            btn.addKeyListener(new KeyListener(2, 1));
        }


        Composite rh_composite = new Composite(this, SWT.NONE);
        rh_composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        GridLayout rh_gl_composite = new GridLayout(3, false);
        rh_gl_composite.horizontalSpacing = 10;

        rh_composite.setLayout(rh_gl_composite);
        rh_composite.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(rh_composite, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("7");
            btn.addKeyListener(new KeyListener(3, 3));
            digitButtons[7] = btn;
        }
        {
            CP1Button btn = new CP1Button(rh_composite, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("8");
            btn.addKeyListener(new KeyListener(2, 0));
            digitButtons[8] = btn;
        }
        {
            CP1Button btn = new CP1Button(rh_composite, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("9");
            btn.addKeyListener(new KeyListener(2, 1));
            digitButtons[9] = btn;
        }

        Composite composite_1 = new Composite(this, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        GridLayout gl_composite_1 = new GridLayout(4, true);
        gl_composite_1.horizontalSpacing = 10;
        composite_1.setLayout(gl_composite_1);
        composite_1.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("STEP");
            btn.setSubText("Schritt");
            btn.addKeyListener(new KeyListener(1, 1));
        }
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("STP");
            btn.setSubText("Stopp");
            btn.addKeyListener(new KeyListener(1, 2));
        }
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("RUN");
            btn.setSubText("Lauf");
            btn.addKeyListener(new KeyListener(1, 3));
        }
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("CAL");
            btn.setSubText("Cass. lesen");
            btn.addKeyListener(new KeyListener(0, 0));
        }

        Composite rh_composite_1 = new Composite(this, SWT.NONE);
        GridLayout rh_gl_composite_1 = new GridLayout(3, false);
        rh_gl_composite_1.horizontalSpacing = 10;
        rh_composite_1.setLayout(rh_gl_composite_1);
        rh_composite_1.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(rh_composite_1, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("4");
            btn.addKeyListener(new KeyListener(3, 0));
            digitButtons[4] = btn;
        }
        {
            CP1Button btn = new CP1Button(rh_composite_1, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("5");
            btn.addKeyListener(new KeyListener(3, 1));
            digitButtons[5] = btn;
        }
        {
            CP1Button btn = new CP1Button(rh_composite_1, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("6");
            btn.addKeyListener(new KeyListener(3, 2));
            digitButtons[6] = btn;
        }


        Composite composite_2 = new Composite(this, SWT.NONE);
        composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        GridLayout gl_composite_2 = new GridLayout(3, false);
        gl_composite_2.horizontalSpacing = 10;
        composite_2.setLayout(gl_composite_2);
        composite_2.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(composite_2, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("CLR");
            btn.setSubText("Irrtum");
            btn.addKeyListener(new KeyListener(0, 1));
        }
        {
            CP1Button btn = new CP1Button(composite_2, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("ACC");
            btn.setSubText("Akku");
            btn.addKeyListener(new KeyListener(0, 3));
        }
        {
            CP1Button btn = new CP1Button(composite_2, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("CAS");
            btn.setSubText("Cass. speichern");
            btn.addKeyListener(new KeyListener(1, 0));
        }

        Composite rh_composite_2 = new Composite(this, SWT.NONE);
        GridLayout rh_gl_composite_2 = new GridLayout(3, false);
        rh_gl_composite_2.horizontalSpacing = 10;
        rh_composite_2.setLayout(rh_gl_composite_2);
        rh_composite_2.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(rh_composite_2, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("1");
            btn.addKeyListener(new KeyListener(4, 1));
            digitButtons[1] = btn;
        }
        {
            CP1Button btn = new CP1Button(rh_composite_2, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("2");
            btn.addKeyListener(new KeyListener(4, 2));
            digitButtons[2] = btn;
        }
        {
            CP1Button btn = new CP1Button(rh_composite_2, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("3");
            btn.addKeyListener(new KeyListener(4, 3));
            digitButtons[3] = btn;
        }


        Composite composite_3 = new Composite(this, SWT.NONE);
        composite_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
        GridLayout gl_composite_3 = new GridLayout(3, false);
        gl_composite_3.horizontalSpacing = 10;
        composite_3.setLayout(gl_composite_3);
        composite_3.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(composite_3, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("PC");
            btn.setSubText("Programmzähler");
            btn.addKeyListener(new KeyListener(0, 2));
        }
        {
            CP1Button btn = new CP1Button(composite_3, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("OUT");
            btn.setSubText("auslesen");
            btn.addKeyListener(new KeyListener(2, 2));
        }
        {
            CP1Button btn = new CP1Button(composite_3, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("INP");
            btn.setSubText("eingeben");
            btn.addKeyListener(new KeyListener(2, 3));
        }

        Composite rh_composite_3 = new Composite(this, SWT.NONE);
        GridLayout rh_gl_composite_3 = new GridLayout(3, false);
        rh_gl_composite_3.horizontalSpacing = 10;
        rh_composite_3.setLayout(rh_gl_composite_3);
        rh_composite_3.setBackground(CP1Colors.PANEL_BACKGROUND);
        {
            CP1Button btn = new CP1Button(rh_composite_3, SWT.NONE);
            btn.setSize((int)(3.0 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("0");
            btn.addKeyListener(new KeyListener(4, 0));
            digitButtons[0] = btn;
        }
        new Label(rh_composite_3, SWT.NONE);
        new Label(rh_composite_3, SWT.NONE);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKey(e, false);
            }

            private void handleKey(KeyEvent e, boolean pressState) {
                int digit = e.character - '0';
                if (digit >= 0 && digit <= 9) {
                    e.doit = false;
                    CP1Button btn = digitButtons[digit];
                    btn.setPressed(pressState);
                } else {
                    super.keyPressed(e);
                }
            }
        });
    }

    public int getKeyMask(int row) {
        return keyMask[row];
    }

    private void paint(PaintEvent paintEvent) {
        GC gc = paintEvent.gc;
        Rectangle r = getClientArea();

        // Draw green corners
        gc.setBackground(CP1Colors.GREEN);
        gc.fillRectangle(r.x, r.y, r.width, MARGIN_WIDTH);
        gc.fillRectangle(r.x, r.y + r.height - MARGIN_WIDTH, r.width, MARGIN_WIDTH);
        gc.fillRectangle(r.x, r.y, MARGIN_WIDTH, r.height);
        gc.fillRectangle(r.x + r.width - MARGIN_WIDTH, r.y , MARGIN_WIDTH, r.height);

        // Draw full border in light color
        gc.setLineWidth(MARGIN_WIDTH);
        gc.setForeground(CP1Colors.GREEN_LIGHT);
        gc.drawRoundRectangle(r.x+MARGIN_WIDTH/2, r.y+MARGIN_WIDTH/2, r.width-MARGIN_WIDTH, r.height-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);

        // redraw top left in dark color
        Path path = new Path(gc.getDevice());
        path.moveTo(r.x + 2*MARGIN_WIDTH, r.y + r.height - 2*MARGIN_WIDTH);
        path.lineTo(r.x , r.y + r.height);
        path.lineTo(r.x , r.y);
        path.lineTo(r.x + r.width, r.y);
        path.lineTo(r.x + r.width - 2*MARGIN_WIDTH, r.y + 2*MARGIN_WIDTH);
        path.close();
        gc.setClipping(path);
        gc.setForeground(CP1Colors.GREEN_DARK);
        gc.drawRoundRectangle(r.x+MARGIN_WIDTH/2, r.y+MARGIN_WIDTH/2, r.width-MARGIN_WIDTH, r.height-MARGIN_WIDTH, ARC_WIDTH, ARC_WIDTH);
        path.dispose();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}

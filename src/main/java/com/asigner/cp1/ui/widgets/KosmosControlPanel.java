// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.ui.widgets;

import com.asigner.cp1.ui.CP1Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class KosmosControlPanel extends Composite {

    private static final int BUTTON_HEIGHT = 50;
    private static final int MARGIN_WIDTH = 10;
    private static final int ARC_WIDTH = 30;

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
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("1");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("2");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("3");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("4");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("5");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("6");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("7");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("8");
        }
        {
            CP1Button btn = new CP1Button(composite, SWT.NONE);
            btn.setSize((int)(1.1 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("9");
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
        }
        {
            CP1Button btn = new CP1Button(rh_composite, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("8");
        }
        {
            CP1Button btn = new CP1Button(rh_composite, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("9");
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
        }
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("STP");
            btn.setSubText("Stopp");
        }
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("RUN");
            btn.setSubText("Lauf");
        }
        {
            CP1Button btn = new CP1Button(composite_1, SWT.NONE);
            btn.setSize((int)(2.6 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("CAL");
            btn.setSubText("Cass. lesen");
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
        }
        {
            CP1Button btn = new CP1Button(rh_composite_1, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("5");
        }
        {
            CP1Button btn = new CP1Button(rh_composite_1, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("6");
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
        }
        {
            CP1Button btn = new CP1Button(composite_2, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("ACC");
            btn.setSubText("Akku");
        }
        {
            CP1Button btn = new CP1Button(composite_2, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("CAS");
            btn.setSubText("Cass. speichern");
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
        }
        {
            CP1Button btn = new CP1Button(rh_composite_2, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("2");
        }
        {
            CP1Button btn = new CP1Button(rh_composite_2, SWT.NONE);
            btn.setSize((int)(1.4 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("3");
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
        }
        {
            CP1Button btn = new CP1Button(composite_3, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("OUT");
            btn.setSubText("auslesen");
        }
        {
            CP1Button btn = new CP1Button(composite_3, SWT.NONE);
            btn.setSize((int)(3.2 * BUTTON_HEIGHT), BUTTON_HEIGHT);
            btn.setText("INP");
            btn.setSubText("eingeben");
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
        }
        new Label(rh_composite_3, SWT.NONE);
        new Label(rh_composite_3, SWT.NONE);
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

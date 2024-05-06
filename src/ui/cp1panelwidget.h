#pragma once

#include <QWidget>

#include "ui/cp1display.h"
#include "ui/cp1keyboard.h"

namespace kosmos_cp1 {
namespace ui {

class CP1PanelWidget : public QWidget
{
    Q_OBJECT
public:
    explicit CP1PanelWidget(Intel8155 *pid, QWidget *parent = nullptr);

    CP1Display *cp1Display() {
        return display_;
    }

    CP1Keyboard *cp1Keyboard() {
        return keyboard_;
    }

signals:

private:
    CP1Display *display_;
    CP1Keyboard *keyboard_;
};

} // namespace ui
} // namespace kosmos_cp1


///*
// * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
// *
// * This file is part of kosmos-cp1.
// *
// * kosmos-cp1 is free software: you can redistribute it and/or
// * modify it under the terms of the GNU General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * kosmos-cp1 is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
// */

//package com.asigner.cp1.ui.widgets;

//import com.asigner.cp1.ui.CP1Colors;
//import com.asigner.cp1.ui.util.SWTResources;
//import org.eclipse.jface.layout.GridDataFactory;
//import org.eclipse.jface.layout.GridLayoutFactory;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Label;

//import java.util.logging.Logger;

//public class CP1Panel extends Composite {
//private static final Logger logger = Logger.getLogger(CP1Panel.class.getName());

//private static final Image CONTACT_TOP = SWTResources.getImage("/com/asigner/cp1/ui/contact_top.png");
//private static final int CONTACT_W = CONTACT_TOP.getBounds().width;
//private static final int CONTACT_H = CONTACT_TOP.getBounds().height;

//private CP1Keyboard keyboard;
//private CP1Display display;

//public CP1Panel(Composite parent, int style) {
//        super(parent, style);

//        setLayout(GridLayoutFactory.fillDefaults()
//                      .numColumns(1)
//                      .equalWidth(false)
//                      .spacing(0, 0)
//                      .margins(50, 50)
//                      .create()
//                  );
//        setBackground(CP1Colors.GREEN);

//        Composite composite_1 = new Composite(this, SWT.NONE);
//        composite_1.setBackground(CP1Colors.GREEN);
//        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
//        composite_1.setLayout(GridLayoutFactory.fillDefaults()
//                                  .numColumns(2)
//                                  .equalWidth(false)
//                                  .spacing(0, 0)
//                                  .margins(0, 0)
//                                  .create()
//                              );

//        display = new CP1Display(composite_1, SWT.NONE);
//        GridData gd_p1Display = GridDataFactory.swtDefaults().create();
//        display.setLayoutData(gd_p1Display);
//        display.display("C12127");

//        KosmosLogoComposite kosmosLogo = new KosmosLogoComposite(composite_1, SWT.NONE);
//        GridData gd_kosmosLogo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
//        gd_kosmosLogo.heightHint = 100;
//        kosmosLogo.setLayoutData(gd_kosmosLogo);

//        Label spacer2 = new Label(this, SWT.NONE);
//        spacer2.setLayoutData(GridDataFactory.fillDefaults().hint(-1, 50).create());
//        spacer2.setBackground(CP1Colors.GREEN);

//        keyboard = new CP1Keyboard(this, SWT.NONE);

//        addPaintListener(this::paint);
//    }

//private void paint(PaintEvent ev) {
//        //        GC gc = ev.gc;
//        //
//        //        int w = getClientArea().width;
//        //        int px = getClientArea().width - CONTACT_W;
//        //        for (int i = 0; i < 31; i++) {
//        //            gc.drawImage(CONTACT_TOP, px, 0);
//        //            px -= CONTACT_W;
//        //        }
//    }

//public CP1Display getCP1Display() {
//        return display;
//    }

//public CP1Keyboard getCP1Keyboard() {
//        return keyboard;
//    }
//}

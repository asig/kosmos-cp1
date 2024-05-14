#include "cp5panelwidget.h"

#include <QHBoxLayout>

namespace kosmos_cp1 {
namespace ui {

CP5PanelWidget::CP5PanelWidget(QWidget *parent)
    : QWidget{parent}
{
    leds_ = new CP5Leds();
    switches_ = new CP5Switches();

    QHBoxLayout *layout = new QHBoxLayout();
    layout->addStretch();
    layout->addWidget(leds_);
    layout->addSpacing(50);
    layout->addWidget(switches_, 0, Qt::AlignCenter);
    setLayout(layout);
}

void CP5PanelWidget::writeLeds(uint8_t leds) {
    leds_->setValue(leds);
}

uint8_t CP5PanelWidget::readSwitches() {
    return 0;
}

} // namespace ui
} // namespace kosmos_cp1


//        setLayout(GridLayoutFactory.fillDefaults()
//                      .numColumns(4)
//                      .equalWidth(false)
//                      .margins(50, 50)
//                      .spacing(0, 0)
//                      .create()
//                  );
//        setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

//        setBackground(CP1Colors.GREEN);

//        Label placeholder1 = new Label(this, SWT.NONE);
//        placeholder1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

//        leds = new CP5Leds(this, SWT.NONE);
//        leds.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

//        Label placeholder2 = new Label(this, SWT.NONE);
//        placeholder2.setBackground(CP1Colors.GREEN);
//        placeholder2.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).hint(50, -1).create());

//        switches = new CP5Switches(this, SWT.NONE);
//        switches.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

//        addPaintListener(this::paint);
//    }

//public void addSwitchesListener(CP5Switches.Listener listener) {
//        switches.addListener(listener);
//    }

//public void writeLeds(int value) {
//        leds.setValue(value);
//    }

//public int readSwitches() {
//        return switches.getValue();
//    }

//private void paint(PaintEvent ev) {
//        //        GC gc = ev.gc;
//        //        gc.setForeground(SWTResources.RED);
//        //        Rectangle r = getBounds();
//        //        r.x = r.y = 0;
//        //        r.width = r.width - 1;
//        //        r.height = r.height - 1;
//        //        gc.drawRectangle(r);
//    }
//}

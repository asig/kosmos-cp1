#include "ui/cp5switches.h"

#include <QHBoxLayout>

#include "ui/cp1colors.h"

namespace kosmos_cp1 {
namespace ui {

CP5Switches::CP5Switches(QWidget *parent)
    : QWidget{parent}
{
    QHBoxLayout *layout = new QHBoxLayout();
    layout->setContentsMargins(8,5,8,5);
    layout->setSpacing(0);
    for (int i = 0; i < 8; i++) {
        switches_[i] = new CP5Switch();
        layout->addWidget(switches_[i]);
    }
    setLayout(layout);

    QPalette pal = QPalette();
    pal.setColor(QPalette::Window, CP1Color::SWITCH_BG);
    setAutoFillBackground(true);
    setPalette(pal);

//    QSize sz = switches_[0]->size();
//    sz = QSize( 8 * sz.width() + 7 * layout->spacing() + 2 * 3, sz.height());
//    setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
//    setFixedSize(sz);

}

} // namespace ui
} // namespace kosmos_cp1


//public CP5Switches(Composite parent, int style) {
//        super(parent, style);

//        GridLayout layout1 = new GridLayout(8, false);
//        layout1.horizontalSpacing = 5;
//        setLayout(layout1);
//        setBackground(CP1Colors.GREEN);

//        GridLayout layout2 = new GridLayout(8, false);
//        layout2.horizontalSpacing = 0;
//        layout2.marginLeft = 3;
//        layout2.marginTop = 0;
//        layout2.marginBottom = 0;
//        layout2.marginRight = 3;
//        setLayout(layout2);
//        setBackground(CP1Colors.SWITCH_BG);

//        // Note that LSB is on the left-most side
//        for (int i = 0; i < 8; i++) {
//            CP5Switch swtch = new CP5Switch(this, SWT.NONE);
//            swtch.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(-1, 60).create());
//            swtch.addListener(() -> {
//                listeners.forEach(Listener::switchesChanged);
//            });
//            switches[i] = swtch;

//        }
//    }

//public void addListener(Listener listener) {
//        this.listeners.add(listener);
//    }


//    @Override
//        protected void checkSubclass() {
//        // Disable the check that prevents subclassing of SWT components
//    }

//public int getValue() {
//        int value = 0;
//        for (int i = 0; i < 8; i++) {
//            if (switches[i].isOn()) {
//                value |= 1 << i;
//            }
//        }
//        return value;
//    }

//}

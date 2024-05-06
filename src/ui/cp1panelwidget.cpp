#include "cp1panelwidget.h"

#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QGridLayout>
#include <QSvgWidget>

#include "ui/cp1colors.h"

namespace kosmos_cp1 {
namespace ui {

namespace {
constexpr const int kLogoWidth = 489;
constexpr const int kLogoHeight = 212;

}

//private static final Image CONTACT_TOP = SWTResources.getImage("/com/asigner/cp1/ui/contact_top.png");
//private static final int CONTACT_W = CONTACT_TOP.getBounds().width;
//private static final int CONTACT_H = CONTACT_TOP.getBounds().height;

CP1PanelWidget::CP1PanelWidget(Intel8155 *pid, QWidget *parent)
    : QWidget{parent}
{
    display_ = new CP1Display(pid, this);
    keyboard_ = new CP1Keyboard(this);

    QSvgWidget *logo = new QSvgWidget(":/ui/kosmos-logo.svg", this);
    // Make logo the same height as the display.
    int dispHeight = display_->size().height();
    QSize ls = logo->sizeHint();
    double r = ls.width()/(double)ls.height();
    logo->setSizePolicy(QSizePolicy::Fixed, QSizePolicy::Fixed);
    logo->setFixedSize(dispHeight * r, dispHeight);


    QGridLayout *layout = new QGridLayout();
    layout->setContentsMargins(50,50,50,50);

    layout->addWidget(display_, 0, 0);
    layout->addWidget(logo,0,1);
    layout->addWidget(keyboard_,1,0,1,2);

    setLayout(layout);

    display_->display("C12127");

//    setLayout(GridLayoutFactory.fillDefaults()
//                  .numColumns(1)
//                  .equalWidth(false)
//                  .spacing(0, 0)
//                  .margins(50, 50)
//                  .create()
//              );
//    setBackground(CP1Color::GREEN);
//    Composite composite_1 = new Composite(this, SWT.NONE);
//    composite_1.setBackground(CP1Colors.GREEN);
//    composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
//    composite_1.setLayout(GridLayoutFactory.fillDefaults()
//                              .numColumns(2)
//                              .equalWidth(false)
//                              .spacing(0, 0)
//                              .margins(0, 0)
//                              .create()
//                          );

//    display = new CP1Display(composite_1, SWT.NONE);
//    GridData gd_p1Display = GridDataFactory.swtDefaults().create();
//    display.setLayoutData(gd_p1Display);
//    display.display("C12127");

//    KosmosLogoComposite kosmosLogo = new KosmosLogoComposite(composite_1, SWT.NONE);
//    GridData gd_kosmosLogo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
//    gd_kosmosLogo.heightHint = 100;
//    kosmosLogo.setLayoutData(gd_kosmosLogo);

//    Label spacer2 = new Label(this, SWT.NONE);
//    spacer2.setLayoutData(GridDataFactory.fillDefaults().hint(-1, 50).create());
//    spacer2.setBackground(CP1Colors.GREEN);

//    keyboard = new CP1Keyboard(this, SWT.NONE);

//    addPaintListener(this::paint);
}

} // namespace ui
} // namespace kosmos_cp1

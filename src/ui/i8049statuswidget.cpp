#include "i8049statuswidget.h"

#include <QGridLayout>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QLabel>
#include <QTextEdit>

namespace kosmos_cp1::ui {

namespace {

QLabel *makeLabel(const QString& t) {
    QLabel *lbl = new QLabel(t);
    lbl->setFrameStyle(QFrame::Panel | QFrame::Sunken);
    return lbl;
}

}

I8049StatusWidget::I8049StatusWidget(const QString& title, Intel8049 *cpu, QWidget *parent)
    : cpu_(cpu), QGroupBox(title, parent)
{
    QFontMetrics fm{font()};
    int minWidth = fm.horizontalAdvance("XXXXX");


    QGridLayout *layout = new QGridLayout();

    memory_ = new MemoryWidget(cpu->ram());

    cy_ = makeLabel("0");
    ac_ = makeLabel("0");
    f0_ = makeLabel("0");
    bs_ = makeLabel("0");
    sp_ = makeLabel("0");

    dbf_ = makeLabel("0");
    f1_ = makeLabel("0");
    a_ = makeLabel("$00");
    t_ = makeLabel("$00");
    pc_ = makeLabel("$000");

    layout->addWidget(new QLabel("CY"), 0, 0);
    layout->addWidget(new QLabel("AC"), 0, 1);
    layout->addWidget(new QLabel("F0"), 0, 2);
    layout->addWidget(new QLabel("BS"), 0, 3);
    layout->addWidget(new QLabel("SP"), 0, 4);
    layout->addWidget(new QLabel("Memory"), 0, 5);

    layout->setColumnMinimumWidth(0,minWidth);
    layout->setColumnMinimumWidth(1,minWidth);
    layout->setColumnMinimumWidth(2,minWidth);
    layout->setColumnMinimumWidth(3,minWidth);
    layout->setColumnMinimumWidth(4,minWidth);

    layout->addWidget(cy_, 1, 0);
    layout->addWidget(ac_, 1, 1);
    layout->addWidget(f0_, 1, 2);
    layout->addWidget(bs_, 1, 3);
    layout->addWidget(sp_, 1, 4);
    layout->addWidget(memory_, 1, 5, 6, 1);

    layout->addWidget(new QLabel("DBF"), 2, 0);
    layout->addWidget(new QLabel("F1"), 2, 1);
    layout->addWidget(new QLabel("A"), 2, 2);
    layout->addWidget(new QLabel("T"), 2, 3);
    layout->addWidget(new QLabel("PC"), 2, 4);

    layout->addWidget(dbf_, 3, 0);
    layout->addWidget(f1_, 3, 1);
    layout->addWidget(a_, 3, 2);
    layout->addWidget(t_, 3, 3);
    layout->addWidget(pc_, 3, 4);

    setLayout(layout);

}

void I8049StatusWidget::updateState() {
    memory_->update();
    Intel8049::State state = cpu_->state();

    cy_->setText(state.psw & 0x80 ? "1" : "0");
    ac_->setText(state.psw & 0x40 ? "1" : "0");
    f0_->setText(state.psw & 0x20 ? "1" : "0");
    bs_->setText(state.psw & 0x10 ? "1" : "0");
    sp_->setText(QString::asprintf("%d", state.psw & 0x7));

    dbf_->setText(QString::asprintf("%d", state.dbf));
    a_->setText(QString::asprintf("$%02x", state.a));
    t_->setText(QString::asprintf("$%02x", state.t));
    pc_->setText(QString::asprintf("$%03x", state.pc));
    f1_->setText(QString::asprintf("%d", state.f1));


//    int bus = cpu.getPort(0).read();
//    if (this.bus != bus) {
//        this.bus = bus;
//        this.busWidget.setValue(bus);
//    }

//    int p1 = cpu.getPort(1).read();
//    if (this.p1 != p1) {
//        this.p1 = p1;
//        this.p1Widget.setValue(p1);
//    }

//    int p2 = cpu.getPort(2).read();
//    if (this.p2 != p2) {
//        this.p2 = p2;
//        this.p2Widget.setValue(p2);
//    }

}

} // namespace kosmos_cp1::ui

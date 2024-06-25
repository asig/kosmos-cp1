#include "ui/cpu/i8049statuswidget.h"

#include <QGridLayout>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QLabel>
#include <QTextEdit>

#include "ui/resources.h"
#include "fmt/format.h"


namespace kosmos_cp1::ui::cpu {

namespace {

QLabel *makeLabel(const QString& t) {
    QLabel *lbl = new QLabel(t);
    lbl->setFrameStyle(QFrame::Panel | QFrame::Sunken);
    lbl->setFont(Resources::dejaVuSansFont());
    return lbl;
}

}

I8049StatusWidget::I8049StatusWidget(const QString& title, Intel8049 *cpu, QWidget *parent)
    : cpu_(cpu), QGroupBox(title, parent)
{
    QFontMetrics fm{Resources::dejaVuSansFont()};
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

    bus_ = makeLabel("$ff (b00000000)");
    port1_ = makeLabel("$ff (b00000000)");
    port2_ = makeLabel("$ff (b00000000)");

    layout->addWidget(new QLabel("CY"), 0, 0, Qt::AlignTop );
    layout->addWidget(new QLabel("AC"), 0, 1, Qt::AlignTop );
    layout->addWidget(new QLabel("F0"), 0, 2, Qt::AlignTop );
    layout->addWidget(new QLabel("BS"), 0, 3, Qt::AlignTop );
    layout->addWidget(new QLabel("SP"), 0, 4, Qt::AlignTop );
    layout->addWidget(new QLabel("Memory"), 0, 5, Qt::AlignTop );

    layout->setColumnMinimumWidth(0,minWidth);
    layout->setColumnMinimumWidth(1,minWidth);
    layout->setColumnMinimumWidth(2,minWidth);
    layout->setColumnMinimumWidth(3,minWidth);
    layout->setColumnMinimumWidth(4,minWidth);

    layout->addWidget(cy_, 1, 0, Qt::AlignTop);
    layout->addWidget(ac_, 1, 1, Qt::AlignTop);
    layout->addWidget(f0_, 1, 2, Qt::AlignTop);
    layout->addWidget(bs_, 1, 3, Qt::AlignTop);
    layout->addWidget(sp_, 1, 4, Qt::AlignTop);
    layout->addWidget(memory_, 1, 5, 6, 1, Qt::AlignTop);

    layout->addWidget(new QLabel("DBF"), 2, 0, Qt::AlignTop);
    layout->addWidget(new QLabel("F1"), 2, 1, Qt::AlignTop);
    layout->addWidget(new QLabel("A"), 2, 2, Qt::AlignTop);
    layout->addWidget(new QLabel("T"), 2, 3, Qt::AlignTop);
    layout->addWidget(new QLabel("PC"), 2, 4, Qt::AlignTop);

    layout->addWidget(dbf_, 3, 0, Qt::AlignTop);
    layout->addWidget(f1_, 3, 1, Qt::AlignTop);
    layout->addWidget(a_, 3, 2, Qt::AlignTop);
    layout->addWidget(t_, 3, 3, Qt::AlignTop);
    layout->addWidget(pc_, 3, 4, Qt::AlignTop);

    layout->addWidget(new QLabel("Bus"), 4, 0, Qt::AlignTop);
    layout->addWidget(bus_, 4, 1, 1, 4, Qt::AlignTop);

    layout->addWidget(new QLabel("Port 1"), 5, 0, Qt::AlignTop);
    layout->addWidget(port1_, 5, 1, 1, 4, Qt::AlignTop);

    layout->addWidget(new QLabel("Port 2"), 6, 0, Qt::AlignTop);
    layout->addWidget(port2_, 6, 1, 1, 4, Qt::AlignTop);

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

    auto val = cpu_->port(0)->read();
    bus_->setText(fmt::format("${:02x} (b{:08b})",val,val).c_str());

    val = cpu_->port(1)->read();
    port1_->setText(fmt::format("${:02x} (b{:08b})",val,val).c_str());

    val = cpu_->port(2)->read();
    port2_->setText(fmt::format("${:02x} (b{:08b})",val,val).c_str());
}

} // namespace kosmos_cp1::ui::cpu

#include "ui/cpu/i8155statuswidget.h"

#include <QGridLayout>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QLabel>
#include <QTextEdit>

#include "ui/resources.h"
#include "fmt/format.h"


namespace kosmos_cp1::ui::cpu {

using ::kosmos_cp1::emulation::PortMode;
using ::kosmos_cp1::emulation::PortCMode;

namespace {

QLabel *makeLabel(const QString& t) {
    QLabel *lbl = new QLabel(t);
    lbl->setFrameStyle(QFrame::Panel | QFrame::Sunken);
    lbl->setFont(Resources::dejaVuSansFont());
    return lbl;
}

QCheckBox *makeCheckBox(const QString& t) {
    QCheckBox *cb = new QCheckBox(t);
    cb->setAttribute(Qt::WA_TransparentForMouseEvents);
    cb->setFocusPolicy(Qt::NoFocus);
    return cb;
}

QGroupBox *makeGrp(const QString& title, QLabel *mode, QCheckBox *interruptEnabled, QLabel *val) {

    QGroupBox *grp = new QGroupBox(title);

    QGridLayout *layout = new QGridLayout();

//    QFontMetrics fm{Resources::dejaVuSansFont()};
//    int row0W = fm.horizontalAdvance("VALUE: ");

    // Row 0
    layout->addWidget(new QLabel("Mode:"), 0, 0, Qt::AlignTop );
    layout->addWidget(mode , 0, 1, Qt::AlignTop );
    QWidget *w = interruptEnabled != nullptr ? (QWidget*)interruptEnabled : new QLabel("");
    layout->addWidget(w , 0, 2, Qt::AlignTop );

    // Row 1
    layout->addWidget(new QLabel("Value:"), 1, 0, Qt::AlignTop );
    layout->addWidget(val, 1, 1, 1, 2, Qt::AlignTop );

    // Force col 0 to use min width by stretching the last col.
    layout->setColumnStretch(2, 100);

    grp->setLayout(layout);
    return grp;
}

}

I8155StatusWidget::I8155StatusWidget(const QString& title, Intel8155 *pid, QWidget *parent)
    : pid_(pid), QGroupBox(title, parent)
{
    QFontMetrics fm{Resources::dejaVuSansFont()};
    int minWidth = fm.horizontalAdvance("XXXXX");

    memory_ = new MemoryWidget(pid->ram());

    portAMode_ = makeLabel("XXXXX");
    portAInterruptEnabled_ = makeCheckBox("Interrupt enabled");
    portAVal_ = makeLabel("$ff (b00000000)");

    portBMode_ = makeLabel("XXXXX");
    portBInterruptEnabled_ = makeCheckBox("Interrupt enabled");
    portBVal_ = makeLabel("$ff (b00000000)");

    portCMode_ = makeLabel("XXXXX");
    portCVal_ = makeLabel("$ff (b00000000)");

    ceLA_ = makeCheckBox("/CE");
    io_ = makeCheckBox("IO");
    ale_ = makeCheckBox("ALE");
    rdLA_ = makeCheckBox("/RD");
    wrLA_ = makeCheckBox("/WR");

    QVBoxLayout *left = new QVBoxLayout();
    left->addWidget(makeGrp("Port A", portAMode_, portAInterruptEnabled_, portAVal_));
    left->addWidget(makeGrp("Port B", portBMode_, portBInterruptEnabled_, portBVal_));
    left->addWidget(makeGrp("Port C", portCMode_, nullptr, portCVal_));
    QHBoxLayout *row = new QHBoxLayout();
    row->addWidget(ceLA_);
    row->addWidget(io_);
    row->addWidget(ale_);
    row->addWidget(rdLA_);
    row->addWidget(wrLA_);
    left->addLayout(row);

    QGridLayout *layout = new QGridLayout();
    layout->addLayout(left, 0, 0, 2, 1, Qt::AlignTop);

    layout->addWidget(new QLabel("Memory"), 0, 1, Qt::AlignTop );
    layout->addWidget(memory_, 1, 1, Qt::AlignTop );

    setLayout(layout);
}

void I8155StatusWidget::updateState() {
    static std::map<PortMode, const char*> modeToString = {
        {PortMode::INPUT, "INPUT"},
        {PortMode::OUTPUT, "OUTPUT"}
    };

    static std::map<PortCMode, const char*> cModeToString = {
        {PortCMode::ALT1, "ALT1"},
        {PortCMode::ALT2, "ALT2"},
        {PortCMode::ALT3, "ALT3"},
        {PortCMode::ALT4, "ALT4"},
    };


    memory_->update();

    portAMode_->setText(modeToString[pid_->paMode()]);
    portAInterruptEnabled_->setCheckState(pid_->paInterruptEnabled() ? Qt::Checked : Qt::Unchecked);
    QString s = QString::fromStdString(fmt::format("${:02x} (b{:08b})",pid_->paValue(),pid_->paValue()));
    portAVal_->setText(s);

    portBMode_->setText(modeToString[pid_->pbMode()]);
    portBInterruptEnabled_->setCheckState(pid_->pbInterruptEnabled() ? Qt::Checked : Qt::Unchecked);
    s = QString::fromStdString(fmt::format("${:02x} (b{:08b})",pid_->pbValue(),pid_->pbValue()));
    portBVal_->setText(s);

    portCMode_->setText(cModeToString[pid_->pcMode()]);
    s = QString::fromStdString(fmt::format("${:02x} (b{:08b})",pid_->pcValue(),pid_->pcValue()));
    portCVal_->setText(s);

    ceLA_->setCheckState(pid_->ceValue() ? Qt::Checked : Qt::Unchecked);
    io_->setCheckState(pid_->ioValue() ? Qt::Checked : Qt::Unchecked);
    ale_->setCheckState(pid_->aleValue() ? Qt::Checked : Qt::Unchecked);
    rdLA_->setCheckState(pid_->rdValue() ? Qt::Checked : Qt::Unchecked);
    wrLA_->setCheckState(pid_->wrValue() ? Qt::Checked : Qt::Unchecked);
}

} // namespace kosmos_cp1::ui::cpu

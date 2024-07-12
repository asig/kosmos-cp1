#include "aboutdialog.h"

#include <QVBoxLayout>
#include <QPushButton>
#include <QLabel>

#include "config.h"
#include "resources.h"

namespace kosmos_cp1::ui {

AboutDialog::AboutDialog(QWidget* parent) : QDialog(parent) {
    setupUI();
    setWindowTitle("About...");
}

void AboutDialog::setupUI() {
    QVBoxLayout* vLayout = new QVBoxLayout();

    auto logo = QPixmap(":/ui/about.png");
    auto logoLabel = new QLabel();
    logoLabel->setPixmap(logo);
    vLayout->addWidget(logoLabel, 0, Qt::AlignHCenter);

    QLabel *title = new QLabel("Kosmos CP1 Emulator");
    QFont font = Resources::aboutFont();
    font.setPixelSize(28);
    title->setFont(font);

    vLayout->addWidget(title, 0, Qt::AlignHCenter);
    vLayout->addWidget(new QLabel(QString::asprintf("Version %d.%d", VERSION_MAJOR, VERSION_MINOR)), 0, Qt::AlignHCenter);
    vLayout->addWidget(new QLabel("© 2024 Andreas Signer <asigner@gmail.com>"), 0, Qt::AlignHCenter);
    vLayout->addWidget(new QLabel("<a href=\"https://github.com/asig/kosmos-cp1\">https://github.com/asig/kosmos-cp1</a>"), 0, Qt::AlignHCenter);
    auto okBtn = new QPushButton("Ok");
    connect(okBtn, &QPushButton::clicked, this, [this] {
        accept();
    });

    vLayout->addWidget(okBtn, Qt::AlignHCenter);
    setLayout(vLayout);
}

}

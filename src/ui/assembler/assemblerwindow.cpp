#include "ui/assembler/assemblerwindow.h"

#include <QToolBar>
#include <QSplitter>
#include <QVBoxLayout>
#include <QLabel>
#include <QDirIterator>

#include "assembler.h"
#include "emulation/ram.h"
#include "ui/resources.h"

namespace kosmos_cp1::ui::assembler {

using kosmos_cp1::assembler::Assembler;
using kosmos_cp1::emulation::Ram;

AssemblerWindow::AssemblerWindow(Intel8155 *pid, Intel8155 *pidExtension, ExecutorThread *executorThread, WindowManager *windowManager, QWidget *parent)
    : pid_(pid), pidExtension_(pidExtension), executorThread_(executorThread), BaseWindow{windowManager, parent}
{
    QDirIterator it(":/listings");
    std::vector<QString> names;
    while (it.hasNext()) {
        names.push_back(it.next());
    }
    std::sort(names.begin(), names.end());
    for (const QString& name : names) {
        SampleCode sc = loadSample(name);
        samples_.push_back(sc);
    }
}

AssemblerWindow::SampleCode AssemblerWindow::loadSample(const QString& name) {
    SampleCode sc;
    QFile file(name);
    file.open(QIODevice::ReadOnly);
    QTextStream in(&file);
    while(!in.atEnd()) {
        QString line = in.readLine();
        if (line.startsWith("; ") && sc.name.isEmpty()) {
            sc.name = line.last(line.length() - 2).trimmed();
        }
        sc.code += line + "\n";
    }
    file.close();
    return sc;
}

void AssemblerWindow::createWindow() {
    createMenuBar();
    createMainUI();
    setWindowTitle("Assembler");
}

QString AssemblerWindow::windowName() {
    return "Assembler";
}

void AssemblerWindow::createMainUI() {

    sampleCombo_ = new QComboBox();
    sampleCombo_->addItem("", QVariant(""));
    for (const SampleCode& sc : samples_) {
        sampleCombo_->addItem(sc.name, QVariant(sc.code));
    }
    sourceCode_ = new QTextEdit();
    sourceCode_->setFont(Resources::dejaVuSansFont());
    results_ = new QTextEdit();
    assembleBtn_ = new QPushButton("Assemble");

    QVBoxLayout *layout = new QVBoxLayout();

    QHBoxLayout *line = new QHBoxLayout();
    line->addWidget(new QLabel("Sample Listings"));
    line->addWidget(sampleCombo_, 100);
    layout->addLayout(line);
    layout->addWidget(new QLabel("Source"));
    layout->addWidget(sourceCode_, 2);
    layout->addWidget(new QLabel("Assembler output"));
    layout->addWidget(results_);
    layout->addWidget(assembleBtn_,0,Qt::AlignCenter);

    QWidget *center = new QWidget();
    center->setLayout(layout);
    setCentralWidget(center);

    connect(sampleCombo_, &QComboBox::currentIndexChanged, [this](int i) {
        sourceCode_->setText(sampleCombo_->currentData().toString());
        results_->clear();
    });

    connect(assembleBtn_, &QPushButton::clicked, this, &AssemblerWindow::onAssembleClicked);
}

void AssemblerWindow::onAssembleClicked() {
    Assembler assembler(sourceCode_->toPlainText());
    std::vector<QString> errors = assembler.assemble();
    if (errors.size() > 0) {
        QString res;
        for (auto e : errors) {
            res += e + "\n";
        }
        results_->setText(res);
        return;
    }

    results_->setText("Assembly succeeded.");
    std::vector<std::uint8_t> code = assembler.code();
    bool running = executorThread_->isRunning();
    if (running) {
        executorThread_->stopExectuion();
    }
    Ram *ram = pid_->ram();
    for (int i = 0; i < 256; i++) {
        ram->write(i, code[i]);
    }
    if (code.size() > 256) {
        Ram *ram = pidExtension_->ram();
        for (int i = 0; i < 256; i++) {
            ram->write(i, code[256 + i]);
        }
    }
    if (running) {
        executorThread_->startExecution();
    }
}

} //namespace kosmos_cp1::ui::assembler

#pragma once

#include <QWidgetAction>

namespace kosmos_cp1::ui::cpu {

class CheckboxAction : public QWidgetAction
{
    Q_OBJECT
public:
    explicit CheckboxAction(const QString& text, bool checked = false, QWidget *parent = nullptr);

signals:

private slots:
    void onStateChanged(int state);

protected:
    virtual QWidget *createWidget(QWidget *parent) override;
    virtual void deleteWidget(QWidget *widget) override;

private:
    QString text_;
};

} // namespace kosmos_cp1::ui::cpu

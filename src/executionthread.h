#pragma once

#include <deque>

#include <QObject>

namespace kosmos_cp1 {

class ExecutionThread : public QObject {
    Q_OBJECT

    enum class Command {
        NIL,
        SINGLE_STEP,
        START,
        STOP,
        RESET,
        QUIT
    };

public:    
    explicit ExecutionThread(QObject *parent = nullptr);

    void start();
    void join();

signals:

private:
    std::mutex commandsMutex_;
    std::deque<Command> commands_;

    void threadLoop();

};

} // namespace

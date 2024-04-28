#include "throttler.h"

#include <chrono>
#include <thread>

namespace kosmos_cp1 {

using std::chrono::duration_cast;
using std::chrono::milliseconds;
using std::chrono::system_clock;

namespace {
uint32_t now() {
    return duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
}

}

Throttler::Throttler(uint32_t millis, QObject *parent)
    : QObject{parent}, millis_(millis), next_(now() + millis)
{
}

void Throttler::throttle() {
    while (now() < next_) {
        std::this_thread::yield();
    }
    next_ = now() + millis_;
}

} // namespace kosmos_cp1

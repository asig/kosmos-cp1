/*
 * Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
 *
 * This file is part of kosmos-cp1.
 *
 * kosmos-cp1 is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kosmos-cp1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.
 */

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

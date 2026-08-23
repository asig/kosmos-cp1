/*
 * Copyright (c) 2026 Andreas Signer <asigner@gmail.com>
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

#pragma once

#include "intel8049.h"
#include "intel8155.h"

#include <istream>
#include <ostream>

using kosmos_cp1::emulation::Intel8049;
using kosmos_cp1::emulation::Intel8155;

struct EmulatorState {
    Intel8049::State cpu;
    Intel8155::State pid;
    Intel8155::State pidExtension;
};

std::ostream& operator<<(std::ostream& ds, const EmulatorState& state);
std::istream& operator>>(std::istream& ds, EmulatorState& state);


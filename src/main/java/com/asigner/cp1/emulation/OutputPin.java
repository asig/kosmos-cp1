/*
 * Copyright (c) 2017 Andreas Signer <asigner@gmail.com>
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

package com.asigner.cp1.emulation;

import com.google.common.collect.Lists;

import java.util.List;

public class OutputPin extends Pin {

    private List<Pin> sinks = Lists.newArrayList();
    private int value;

    public OutputPin(String name) {
        super(name);
    }

    public void write(int value) {
        if (this.value != value) {
            this.value = value;
            sinks.forEach(s -> s.write(value));
        }
    }

    public void connectTo(Pin other) {
        sinks.add(other);
    }
}

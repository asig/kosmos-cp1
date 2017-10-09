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

package com.asigner.cp1;

import com.google.common.base.Preconditions;

public class Version implements Comparable {
    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(String version) {
        String parts[] = version.split("\\.");
        Preconditions.checkState(parts.length == 3);
        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public int compareTo(Object o) {
        if (o.getClass() != this.getClass()) {
            return -1;
        }
        Version other = (Version)o;
        int delta = this.major - other.major;
        if (delta == 0) {
            delta = this.minor - other.major;
        }
        if (delta == 0) {
            delta = this.patch - other.patch;
        }
        return delta;
    }
}

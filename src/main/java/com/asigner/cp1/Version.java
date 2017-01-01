// Copyright 2016 Andreas Signer. All rights reserved.

package com.asigner.cp1;

import com.google.common.base.Preconditions;

import java.util.Comparator;

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

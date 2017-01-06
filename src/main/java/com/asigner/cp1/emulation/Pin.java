// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class Pin {

    protected final String name;

    public Pin(String name) {
        this.name = name;
    }

    abstract void write(int value);
}

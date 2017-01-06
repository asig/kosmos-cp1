// Copyright 2017 Andreas Signer. All rights reserved.

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

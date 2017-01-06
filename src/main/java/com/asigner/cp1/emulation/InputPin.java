// Copyright 2017 Andreas Signer. All rights reserved.

package com.asigner.cp1.emulation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InputPin extends Pin {

    private final BiConsumer<Integer, Integer> consumer;
    private int prevValue;

    public InputPin(String name, BiConsumer<Integer, Integer> consumer) {
        super(name);
        this.consumer = consumer;
    }

    @Override
    void write(int value) {
        consumer.accept(prevValue, value);
        this.prevValue = value;
    }
}

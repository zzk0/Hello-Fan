package com.test.model;

public class Tuple<A, B> {

    public final A first;
    public final B second;

    public Tuple(A a, B b) {
        this.first = a;
        this.second = b;
    }
}

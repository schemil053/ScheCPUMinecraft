package de.emilschlampp.schecpuminecraft.schemilapi.util;

public class Tuple <A,B> {
    public A a;
    public B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public Tuple<A,B> copy() {
        return new Tuple<>(a, b);
    }

    public void set(Tuple<A, B> tuple) {
        setA(tuple.a);
        setB(tuple.b);
    }
}

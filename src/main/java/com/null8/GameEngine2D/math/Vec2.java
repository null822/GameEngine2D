package com.null8.GameEngine2D.math;

public class Vec2<T extends Number> {

    public T x, y;


    public Vec2(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}

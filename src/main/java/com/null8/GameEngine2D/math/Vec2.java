package com.null8.GameEngine2D.math;

import static com.null8.GameEngine2D.util.TextUtils.beautify;
public class Vec2<T extends Number> {

    public T x, y;


    public Vec2(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + beautify((float)x, 3, 2, '0', false) + ", " + beautify((float)y, 3, 2, '0', false) + ")";
    }

}

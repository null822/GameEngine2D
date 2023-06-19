package com.null8.GameEngine2D.math;

import static com.null8.GameEngine2D.util.TextUtils.beautify;

public class Vec3<T extends Number> {

    public T x, y, z;


    public Vec3(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec2<T> xy, T z) {
        this.x = xy.x;
        this.y = xy.y;
        this.z = z;
    }

    public Vec3(T x, Vec2<T> yz) {
        this.x = x;
        this.y = yz.x;
        this.z = yz.y;
    }


    public String toString() {
        return "(" + beautify((float)x, 3, 2, '0', false) + ", " + beautify((float)y, 3, 2, '0', false) + ", " + beautify((float)z, 3, 2, '0', false) + ")";
    }
}

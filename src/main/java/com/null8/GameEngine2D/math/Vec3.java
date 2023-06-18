package com.null8.GameEngine2D.math;

public class Vec3<T extends Number> {

    public T x, y, z;


    public Vec3(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec2<T> a, T b) {
        this.x = a.x;
        this.y = a.y;
        this.z = b;
    }

    public Vec3(T a, Vec2<T> b) {
        this.x = a;
        this.y = b.x;
        this.z = b.y;
    }


    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}

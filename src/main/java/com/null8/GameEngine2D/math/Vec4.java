package com.null8.GameEngine2D.math;

import static com.null8.GameEngine2D.util.TextUtils.beautify;

public class Vec4<T extends Number> {

    public T x, y, z, w;


    public Vec4(T x, T y, T z, T w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(Vec2<T> xy, T z, T w) {
        this.x = xy.x;
        this.y = xy.y;
        this.z = z;
        this.w = w;
    }

    public Vec4(T x, Vec2<T> yz, T w) {
        this.x = x;
        this.y = yz.x;
        this.z = yz.y;
        this.w = w;
    }

    public Vec4(T x, T y, Vec2<T> zw) {
        this.x = x;
        this.y = y;
        this.z = zw.x;
        this.w = zw.y;
    }

    public Vec4(Vec2<T> xy, Vec2<T> zw) {
        this.x = xy.x;
        this.y = xy.y;
        this.z = zw.x;
        this.w = zw.y;
    }

    public Vec4(Vec3<T> xyz, T w) {
        this.x = xyz.x;
        this.y = xyz.y;
        this.z = xyz.z;
        this.w = w;
    }

    public Vec4(T x, Vec3<T> yzw) {
        this.x = x;
        this.y = yzw.x;
        this.z = yzw.y;
        this.w = yzw.z;
    }


    public String toString() {
        return "(" + beautify((float)x, 3, 2, '0', false) + ", " + beautify((float)y, 3, 2, '0', false) + ", " + beautify((float)z, 3, 2, '0', false) + ", " + beautify((float)w, 3, 2, '0', false) + ")";
    }
}

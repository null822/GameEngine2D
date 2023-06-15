package com.null8.GameEngine2D.util;

public class MathUtils {

    private MathUtils() {
    }

    public static int clamp(int val, int min, int max) {
        if (val < min)
            return min;
        return Math.min(val, max);
    }

    public static float clamp(float val, float min, float max) {
        if (val < min)
            return min;
        return Math.min(val, max);
    }

    public static double clamp(double val, double min, double max) {
        if (val < min)
            return min;
        return Math.min(val, max);
    }
}

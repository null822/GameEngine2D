package com.null8.GameEngine2D.util;

public class TextUtils {
    private TextUtils() {
    }

    public static String beautify(float input) {
        return beautify(input, 3, 2, '0', false);
    }

    public static String beautify(float input, int beforeDP, int afterDP) {
        return beautify(input, beforeDP, afterDP, '0', false);
    }

    public static String beautify(float input, int beforeDP, int afterDP, char bufferChar, boolean moveSign) {

        int dp = (int)Math.pow(10, afterDP);

        float resultFloat = (float) Math.round(input * dp) / dp;

        boolean negative = false;
        if (resultFloat < 0) {
            negative = true;
            resultFloat = -resultFloat;
        }

        StringBuilder result = new StringBuilder(String.valueOf(resultFloat));

        String after = charGen(afterDP - (result.length() - result.indexOf(".") - 1), bufferChar);

        String before = charGen(beforeDP - (result.indexOf(".")), bufferChar);

        String sign = negative ? "-" : String.valueOf(bufferChar);
        before = moveSign ? before + sign : sign + before;

        result = new StringBuilder(before + result + after);


        return result.toString();

    }

    public static String charGen(int count, char character) {
        return String.valueOf(character).repeat(Math.max(0, count));
    }
}

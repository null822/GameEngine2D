package com.null8.GameEngine2D.graphics.text;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Text {

    private Text() {
    }

    public static char[][] charsFromStrings(String[] strings) {
        StringBuilder[] stringBuilders = new StringBuilder[strings.length];

        int i = 0;
        for (String string:strings) {
            stringBuilders[i] = new StringBuilder(string);
            i++;
        }

        int maxWidth = 0;

        for (StringBuilder stringBuilder:stringBuilders) {
            maxWidth = Math.max(maxWidth, stringBuilder.length());
        }

        char[][] result = new char[strings.length][maxWidth];

        int y = 0;
        for (StringBuilder stringBuilder:stringBuilders) {
            for (int x = 0; x < stringBuilder.length(); x++) {
                result[y][x] = stringBuilder.charAt(x);
            }
            y++;
        }

        return result;
    }

    public static ColoredChar[][] makeCCArray(char[][] chars) {

        int maxWidth = 0;

        for (char[] charLine:chars) {
            maxWidth = Math.max(maxWidth, charLine.length);
        }

        ColoredChar[][] result = new ColoredChar[chars.length][maxWidth];

        int indexX = 0;
        int indexY = 0;

        for (char[] charLine:chars) {
            for (char character:charLine) {
                result[indexY][indexX] = new ColoredChar(character, Color.WHITE);

                indexX++;
            }
            indexX = 0;
            indexY++;
        }

        return result;
    }

    public static ColoredChar[][] makeCCArray(char[][] chars, Color[][] colors) {

        ColoredChar[][] result = new ColoredChar[chars.length][];

        int indexX = 0;
        int indexY = 0;

        for (char[] charLine:chars) {
            for (char character:charLine) {

                result[indexY][indexX] = new ColoredChar(character, colors[indexY][indexX]);

                indexX++;
            }
            indexX = 0;
            indexY++;
        }

        return result;
    }

    public static BufferedImage imageFromText(int w, int h, int fontSize, Color bCol, ColoredChar[][] text) {

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        // draw background
        g.setColor(bCol);
        g.fillRect(0, 0, w, h);

        // draw foreground
        g.setFont(new Font("Consolas", Font.PLAIN, fontSize));

        int y = 0;

        for (ColoredChar[] line : text) {
            y++;
            int x = 0;

            for (ColoredChar coloredCharacter : line) {
                x++;

                g.setColor(coloredCharacter.getColor());
                g.drawChars(new char[]{coloredCharacter.getChar()}, 0, 1, x * fontSize, y * fontSize);

            }

        }

        return image;
    }
}

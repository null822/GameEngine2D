package com.null8.GameEngine2D.graphics.text;

import java.awt.*;

public class ColoredChar {

    private Color color;
    private char character;

    public ColoredChar(char character, Color color) {

        this.color = color;
        this.character = character;
    }

    public void setChar(char character) {
        this.character = character;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public char getChar() {
        return this.character;
    }
    public Color getColor() {
        return this.color;
    }
}

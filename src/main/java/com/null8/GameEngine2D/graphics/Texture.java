package com.null8.GameEngine2D.graphics;

import com.null8.GameEngine2D.Main;
import com.null8.GameEngine2D.util.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    private int width, height;
    private int texture;

    public Texture(String path) {
        texture = load(path);
    }

    public Texture(BufferedImage image) {
        texture = load(image);
    }

    private int load(BufferedImage image) {
        int[] pixels;

        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    private int load(String path) {
        int[] pixels = null;
        try {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/textures/" + path)));
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }


    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getTexture() {
        return texture;
    }

}
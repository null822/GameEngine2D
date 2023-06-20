package com.null8.GameEngine2D.graphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class TextureSet {

    private final Texture[] texture;
    private final String name;

    public TextureSet(String name, String path) {
        super();
        texture = new Texture[2];
        texture[0] = new Texture(name, path, false);
        texture[1] = new Texture(name, path, true);
        this.name = name;
    }

    public void bind(int i) {
        glBindTexture(GL_TEXTURE_2D, texture[i].getTexture());
    }
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    public int getTextureInt(int i) {
        return this.texture[i].getTexture();
    }
    public Texture getTexture(int i) {
        return this.texture[i];
    }
    public Texture getTexture(boolean i) {
        return i ? this.texture[0] : this.texture[1];
    }
    public String getName() {
        return this.name;
    }

}

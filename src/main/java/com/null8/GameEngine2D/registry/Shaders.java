package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.graphics.Shader;

public class Shaders {
    public static final Shader TEST = new Shader("shader.vsh", "shader.fsh");
    public static final Shader BACKGROUND = new Shader("background.vsh", "background.fsh");
    public static final Shader TEXTURE = new Shader("texture.vsh", "texture.fsh");

}

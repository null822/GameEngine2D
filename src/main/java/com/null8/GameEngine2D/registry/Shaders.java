package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.graphics.Shader;
import org.lwjgl.system.Pointer;

import java.util.List;

public class Shaders {
    public static final Shader BACKGROUND = init(new Shader("background.vsh", "background.fsh"));
    public static final Shader TEXTURE = init(new Shader("texture.vsh", "texture.fsh"));
    public static final Shader TEXT = init(new Shader("text.vsh", "text.fsh"));

    private static Shader init(Shader shader) {
        shader.setUniform1i("tex", 1);
        return shader;
    }

}


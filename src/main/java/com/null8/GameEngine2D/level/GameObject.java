package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Shaders;

public class GameObject {

    private VertexArray texVert, fade;
    private final Texture tex;
    private final Shader shader;

    private Vec3 pos;

    private int imageWidth;
    private int imageHeight;

    private float renderWidth;
    private float pixelDensity;


    public GameObject(Texture tex, Shader shader, Vec3 pos, int width, int height) {

        this.tex = tex;
        this.shader = shader;
        this.pos = pos;

        setup((float) width, (float) height);
    }

    public void setup(float xSize, float ySize) {

        this.imageWidth = tex.getWidth();
        this.imageHeight = tex.getHeight();

        xSize = xSize * 1.785f;

        float height = 0.5f;

        this.renderWidth = xSize * 2;

        pixelDensity = (float) 80 / (imageWidth * (20 / xSize * 2));


        float[] vertices = new float[] {
                -xSize, -ySize + height, 0.0f,
                -xSize,  ySize + height, 0.0f,
                xSize,  ySize + height, 0.0f,
                xSize, -ySize + height, 0.0f
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        float[] tcs = new float[] {
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };

        texVert = new VertexArray(vertices, indices, tcs);
    }

    public void move(Vec3 newPos) {
        this.pos = newPos;
    }


    public void render(float xScroll) {

        tex.bind();
        Shaders.TEXTURE.enable();
        texVert.bind();

        Shaders.TEXTURE.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vec3(-(xScroll * pixelDensity) + (renderWidth / 2) - 10 + pos.x, pos.y, pos.z)));
        texVert.draw();

        Shaders.TEXTURE.disable();
        tex.unbind();

    }

}

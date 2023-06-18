package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.GameObjects;
import com.null8.GameEngine2D.registry.Shaders;
import com.null8.GameEngine2D.registry.Textures;

public class GameObject {

    private String name;

    private VertexArray texVert;
    private final Texture tex;
    private final Shader shader;

    private Vec3<Float> pos;

    private int imageWidth;
    private int imageHeight;

    private float renderWidth;
    private float pixelDensityX;
    private float pixelDensityY;

    private float xSize;
    private float ySize;

    private float width;
    private float height;


    public GameObject(String name, Texture tex, Shader shader, int width, int height) {

        this.tex = tex;
        this.shader = shader;
        this.name = name;

        this.width = width;
        this.height = height;

        this.pos = new Vec3<>(0f, 0f, 0f);
    }

    public GameObject(GameObject template) {

        this.tex = template.tex;
        this.shader = template.shader;
        this.name = template.name;

        this.width = template.width;
        this.height = template.height;

        this.pos = new Vec3<>(0f, 0f, 0f);
    }

    public void setup(int frameWidth, int frameHeight) {

        this.imageWidth = tex.getWidth();
        this.imageHeight = tex.getHeight();

        double scaleX = (float) imageWidth / 8;
        double scaleY = (float) imageHeight / 8;

        float xSize = (float) ((scaleX * frameHeight * imageWidth) / (imageWidth * frameWidth) * 1.785f);
        float ySize = (float) scaleY;

        float heightOffset = 0.5f;

        this.renderWidth = xSize * 2;

        pixelDensityX = (float) 2 * xSize / imageWidth;
        pixelDensityY = (float) 2 * ySize / imageHeight;

        float[] vertices = new float[] {
                -xSize, -ySize + heightOffset, 0.0f,
                -xSize,  ySize + heightOffset, 0.0f,
                xSize,  ySize + heightOffset, 0.0f,
                xSize, -ySize + heightOffset, 0.0f
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

    public void setPixelDensityX(float pixelDensity) {
        this.pixelDensityX = pixelDensity;
    }
    public void setPixelDensityY(float pixelDensity) {
        this.pixelDensityY = pixelDensity;
    }

    public GameObject move(Vec3<Float> newPos) {
        this.pos = newPos;
        return this;
    }

    public GameObject moveCopy(Vec3<Float> newPos) {
        return new GameObject(this).move(newPos);
    }


    public void render(double xScroll) {

        Vec3<Float> position = new Vec3<>((float) (-(xScroll * pixelDensityX / 2) + (renderWidth / 2) - 10 + (pos.x * pixelDensityX)), (pos.y * pixelDensityY), (pos.z * pixelDensityX));


        tex.bind();
        Shaders.TEXTURE.enable();
        texVert.bind();

        Shaders.TEXTURE.setUniformMat4f("vw_matrix", Matrix4f.translate(position));
        texVert.draw();

        tex.unbind();
        Shaders.TEXTURE.disable();
        texVert.unbind();

        //System.out.println(position);

    }

    public String getName() {
        return name;
    }


}

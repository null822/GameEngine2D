package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Shaders;

import static com.null8.GameEngine2D.util.MathUtils.clamp;

public class Level {

    private VertexArray background, fade;
    private final Texture bgTexture;

    private final GameObject[] gameObjects;

    private float xScroll;

    private int imageWidth;
    private int imageHeight;

    private float renderWidth;
    private float pixelDensity;

    private float time = 0.0f;
    private boolean control = true, reset = false;

    public Level(Texture bgTexture, GameObject[] gameObjects) {

        this.bgTexture = bgTexture;
        this.gameObjects = gameObjects;

        this.xScroll = 0f;
    }

    public void setup(int frameWidth, int frameHeight) {

        this.imageWidth = bgTexture.getWidth();
        this.imageHeight = bgTexture.getHeight();


        float scale = (float) frameHeight / imageHeight * 4;

        float xSize = scale * imageWidth / frameWidth * 1.785f;
        float ySize = scale * imageHeight / frameHeight;

        float height = 0.5f;

        this.renderWidth = xSize * 2;

        pixelDensity = (float) 80 / (imageWidth * (20 / xSize * 2));

        xScroll = 0f;

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

        background = new VertexArray(vertices, indices, tcs);
    }

    public boolean isGameOver() {
        return reset;
    }


    public void render() {

        xScroll = clamp(xScroll, 0f, maxXPos());

        bgTexture.bind();
        Shaders.BACKGROUND.enable();
        background.bind();

        Shaders.BACKGROUND.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vec3(-(xScroll * pixelDensity) + (renderWidth / 2) - 10, 0.0f, 0.0f)));
        background.draw();

        Shaders.BACKGROUND.disable();
        bgTexture.unbind();

        for (GameObject gameObject : gameObjects)
            gameObject.render(xScroll);

    }

    public void setPos(float xScroll) {
        this.xScroll = xScroll;
    }

    public float getPos() {
        return this.xScroll;
    }

    public float maxXPos() {
        return imageWidth - (20 / pixelDensity);
    }


}

package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.Main;
import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vector3f;

public class Level {

    private VertexArray background, fade;
    private Texture bgTexture;

    private float xScroll;

    private int imageWidth;
    private int imageHeight;

    private int frameWidth;
    private int frameHeight;

    private float renderWidth;
    private float pixelDensity;

    private float time = 0.0f;
    private boolean control = true, reset = false;

    public Level(Main main) {

        setup(main);
        this.xScroll = 0f;
    }

    public void setup(Main main) {

        bgTexture = new Texture("background/checkerboard.png");

        this.imageWidth = bgTexture.getWidth();
        this.imageHeight = bgTexture.getHeight();
        this.frameWidth = main.getWidth();
        this.frameHeight = main.getHeight();

        float scale = (float) frameHeight / imageHeight * 4;

        float xSize = scale * imageWidth / frameWidth * 1.785f;
        float ySize = scale * imageHeight / frameHeight;

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

        background = new VertexArray(vertices, indices, tcs);
    }

    public boolean isGameOver() {
        return reset;
    }


    public void render() {
        bgTexture.bind();
        Shader.BACKGROUND.enable();
        background.bind();


        xScroll = xScroll + 0.1f;



        Shader.BACKGROUND.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(-(float)((xScroll * pixelDensity) - (renderWidth / 2) + 10), 0.0f, 0.0f)));
        background.draw();


        Shader.BACKGROUND.disable();
        bgTexture.unbind();

    }

}

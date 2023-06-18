package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Shaders;

import static com.null8.GameEngine2D.util.MathUtils.clamp;

public class Level {

    private VertexArray background, fade;
    private final Texture bgTexture;

    private final GameObject[] gameObjects;
    private final GameObject player;

    private Vec2<Float> pos;

    private int imageWidth;
    private int imageHeight;

    private float renderWidth;
    private float pixelDensity;

    private float frameWidth;

    public Level(Texture bgTexture, GameObject player, GameObject[] gameObjects) {

        this.bgTexture = bgTexture;
        this.gameObjects = gameObjects;
        this.player = player;

        this.pos = new Vec2<>(0.0f, 0.0f);
    }

    public void setup(int frameWidth, int frameHeight) {

        this.frameWidth = frameWidth;

        this.imageWidth = bgTexture.getWidth();
        this.imageHeight = bgTexture.getHeight();

        float scale = 4f;

        float xSize = (scale * frameHeight * imageWidth) / (imageHeight * frameWidth) * 1.785f;
        float ySize = scale;

        float heightOffset = 0.5f;

        this.renderWidth = xSize * 2;

        pixelDensity = (float) 2 * xSize / imageWidth;

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

        background = new VertexArray(vertices, indices, tcs);

        for (GameObject gameObject : gameObjects) {
            gameObject.setup(frameWidth, frameHeight);
        }
        player.setup(frameWidth, frameHeight);


        System.out.println("pd " + pixelDensity);
        System.out.println("rw " + renderWidth);

    }

    public GameObject[] getGameObjects() {
        return gameObjects;
    }


    public void render() {

        Vec3<Float> position = new Vec3<>(-(pos.x * pixelDensity) + (renderWidth / 2) - 10, 0.0f, 0.0f);


        bgTexture.bind();
        Shaders.BACKGROUND.enable();
        background.bind();

        Shaders.BACKGROUND.setUniformMat4f("vw_matrix", Matrix4f.translate(position));
        background.draw();

        bgTexture.unbind();
        Shaders.BACKGROUND.disable();
        background.unbind();


        for (GameObject gameObject : gameObjects) {
            //System.out.println("rendering " + gameObject.getName() + " at:");
            gameObject.render(pos.x);
        }


        player.move(new Vec3<>(pos.x / 2 + 30, pos.y, 2.0f));
        player.render(pos.x);

    }

    public void setPos(Vec2<Float> pos) {
        this.pos = pos;
    }

    public Vec2<Float> getPos() {
        return this.pos;
    }

    public float maxXPos() {
        return  imageWidth;
    }

    public float maxYPos() {
        return 9;
    }

    public float minYPos() {
        return -8;
    }

}

package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Shaders;
import com.null8.GameEngine2D.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Level {

    private VertexArray background;
    private final Texture bgTexture;

    private Matrix4f pr_matrix;

    private final GameObject[] gameObjects;
    private final Player player;
    private final List<GameObject> texts = new ArrayList<>();

    private Vec2<Float> pos;

    private float width;
    private float height;

    private float frameX = 0;


    public Level(Texture bgTexture, Player player, GameObject[] gameObjects) {

        this.bgTexture = bgTexture;
        this.gameObjects = gameObjects;
        this.player = player;

        this.pos = new Vec2<>(0.0f, 0.0f);

    }

    public void setup(float aspectRatio) {

        this.width = 96.0f * aspectRatio * 0.56f;
        this.height = 96.0f;

        float heightOffset = 12f;


        float xSize = (float) bgTexture.getWidth() / 2;
        float ySize = (float) bgTexture.getHeight() / 2;


        float[] vertices = new float[] {
                0,     0 + heightOffset,     1f,
                0,     ySize + heightOffset, 1f,
                xSize, ySize + heightOffset, 1f,
                xSize, 0 + heightOffset,     1f
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

    public GameObject[] getGameObjects() {
        return gameObjects;
    }


    public void render(boolean step) {

        Vec3<Float> position = new Vec3<>(0.0f, 0.0f, 0.0f);

        frameX = MathUtils.clamp(pos.x, width/2 - (player.getWidth()/2), maxXPos() - (width/2) - (player.getWidth()/2))
                - (width/2) + (player.getWidth()/2);

        this.pr_matrix = Matrix4f.orthographic(frameX, width + frameX, 0 * 9.0f / 16.0f, height * 9.0f / 16.0f, 0f, 4.0f);


        bgTexture.bind();
        Shaders.BACKGROUND.enable();
        background.bind();

        Shaders.BACKGROUND.setUniformMat4f("vw_matrix", Matrix4f.translate(position));
        Shaders.BACKGROUND.setUniformMat4f("pr_matrix", pr_matrix);
        background.draw();

        bgTexture.unbind();
        Shaders.BACKGROUND.disable();
        background.unbind();


        for (GameObject gameObject : gameObjects) {
            gameObject.render(pr_matrix);
        }


        player.move(new Vec2<>(pos.x, pos.y));
        player.render(pr_matrix, step);

        List<GameObject> textsCopy = new ArrayList<>(texts);
        for (GameObject text:textsCopy) {
            if (text != null) {
                text.render(pr_matrix);
            }
        }

    }

    public void setText(GameObject text) {
        String name = text.getName();

        boolean containsElement = false;
        for (GameObject textElement:texts) {
            if (Objects.equals(textElement.getName(), name)) {
                containsElement = true;
                break;
            }
        }

        if (!containsElement) {
            addText(text);
            return;
        }

        for (GameObject textElement:texts) {
            if (Objects.equals(textElement.getName(), name)) {
                texts.remove(textElement);
                texts.add(text);
            }
        }
    }

    public void addText(GameObject text) {
        texts.add(text);
    }

    public void removeText(GameObject text) {
        String name = text.getName();
        texts.removeIf(textElement -> Objects.equals(textElement.getName(), name));
    }


    public void setPos(Vec2<Float> pos) {
        this.pos = pos;
    }
    public Vec2<Float> getPos() {
        return this.pos;
    }
    public float maxXPos() {
        return (float) bgTexture.getWidth() / 2;
    }
    public float maxYPos() {
        return (float) bgTexture.getHeight() / 2;
    }
    public Player getPlayer() {
        return this.player;
    }
    public float getFrameX() {
        return this.frameX;
    }

}

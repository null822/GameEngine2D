package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.level.manager.GameObjectManager;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Shaders;
import com.null8.GameEngine2D.util.MathUtils;

public class Level {

    private VertexArray background;
    private final Texture bgTexture;

//    private final GameObject[] gameObjects;
//    private final FakePlayer[] fakePlayers;
//    private int playerIndex = 0;
//
//    private final Player player;
//    private final List<GameObject> texts = new ArrayList<>();

    private final GameObjectManager manager;

    private final float[] margins;

    private Vec2<Float> pos;

    private float width;
    private float height;

    private float xSize;
    private float ySize;

    private float frameX = 0;


    public Level(Texture bgTexture, Player player, GameObject[] gameObjects, FakePlayer[] fakePlayers, float[] margins) {

        this.bgTexture = bgTexture;
        this.manager = new GameObjectManager(gameObjects, fakePlayers, player);
        this.margins = margins;

        this.pos = new Vec2<>(0.0f, 0.0f);

    }

    public void setup(float aspectRatio) {

        this.width  = 320.0f * aspectRatio * 0.56f;
        this.height = 320.0f;

        float heightOffset = 12f;


        xSize = (float) bgTexture.getWidth();
        ySize = (float) bgTexture.getHeight();


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

    public void render(boolean step) {


        float playerWidth = manager.getPlayer().getWidth();

        Vec3<Float> position = new Vec3<>(0.0f, 0.0f, -8.0f);

        frameX = MathUtils.clamp(pos.x, width/2 - (playerWidth/2), xSize - (width/2) - (playerWidth/2))
                - (width/2) + (playerWidth/2);

        Matrix4f pr_matrix = Matrix4f.orthographic(frameX, width + frameX, 0 * 9.0f / 16.0f, height * 9.0f / 16.0f, -8f, 8.0f);


        bgTexture.bind();
        Shaders.BACKGROUND.enable();
        background.bind();

        Shaders.BACKGROUND.setUniformMat4f("vw_matrix", Matrix4f.translate(position));
        Shaders.BACKGROUND.setUniformMat4f("pr_matrix", pr_matrix);
        background.draw();

        bgTexture.unbind();
        Shaders.BACKGROUND.disable();
        background.unbind();

        manager.render(pr_matrix, pos, step);

    }

    public void setText(GameObject text) {
        manager.setText(text);
    }
    public void addText(GameObject text) {
        manager.addText(text);
    }
    public void removeText(GameObject text) {
        manager.removeText(text);
    }


    public void setPos(Vec2<Float> pos) {
        this.pos = pos;
    }
    public Vec2<Float> getPos() {
        return this.pos;
    }
    public float minXPos() {
        return margins[0];
    }
    public float maxXPos() {
        return xSize - margins[2];
    }
    public float minYPos() {
        return margins[3];
    }
    public float maxYPos() {
        return ySize - margins[1];
    }
    public Player getPlayer() {
        return this.manager.getPlayer();
    }
    public GameObject getText(String name) {
        return this.manager.getText(name);
    }
    public FakePlayer getFakePlayer(String name) {
        return this.manager.getFakePlayer(name);
    }
    public GameObject getGameObject(String name) {
        return this.manager.getGameObject(name);
    }
    public float getFrameX() {
        return this.frameX;
    }

    public GameObjectManager getManager() {
        return manager;
    }

}

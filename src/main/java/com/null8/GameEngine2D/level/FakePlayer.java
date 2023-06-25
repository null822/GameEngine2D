package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.TextureSet;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;

import static com.null8.GameEngine2D.Main.pixelsPerStep;

public class FakePlayer extends GameObject {

    private int state = 0;
    private boolean facing = false;

    private final TextureSet stand;
    private final TextureSet walk;

    public FakePlayer(String name, TextureSet stand, TextureSet walk, Shader shader, int width, int height, float zHeight) {
        super(name, stand.getTexture(0), shader, width, height, zHeight);
        this.stand = stand;
        this.walk = walk;

    }

    public FakePlayer(FakePlayer template) {
        super(template);
        this.stand = template.stand;
        this.walk = template.walk;
        this.state = 0;
        this.facing = false;

    }

    public void render(Matrix4f pr_matrix) {

        int modPos = Math.round(pos.x) % pixelsPerStep;
        boolean step = modPos <= pixelsPerStep / 2;

        switch (state) {
            case 0 -> tex = stand.getTexture(facing);
            case 2 -> tex = step ? walk.getTexture(facing) : stand.getTexture(facing);
        }

        tex.bind();
        shader.enable();
        texVert.bind();

        shader.setUniformMat4f("pr_matrix", pr_matrix);
        shader.setUniformMat4f("vw_matrix", Matrix4f.translate(pos));
        texVert.draw();

        tex.unbind();
        shader.disable();
        texVert.unbind();
    }


    public FakePlayer move(Vec3<Float> newPos, boolean add) {
        if (add)
            this.pos = new Vec3<>(newPos.x + this.pos.x, newPos.y + this.pos.y, newPos.z + this.pos.z);
        else
            this.pos = newPos;
        return this;
    }

    public FakePlayer move(Vec2<Float> newPos, boolean add) {
        if (add)
            this.pos = new Vec3<>(newPos.x + this.pos.x, newPos.y + this.pos.y, this.pos.z);
        else
            this.pos = new Vec3<>(newPos, this.pos.z);
        return this;
    }
    public FakePlayer moveCopy(Vec3<Float> newPos, boolean add) {
        return new FakePlayer(this).move(newPos, add);
    }
    public FakePlayer moveCopy(Vec2<Float> newPos, boolean add) {
        return new FakePlayer(this).move(new Vec3<>(newPos, 0f), add);
    }

    public FakePlayer moveCopy(Vec2<Float> newPos) {
        return new FakePlayer(this).move(new Vec3<>(newPos, this.pos.z), false);
    }
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Set the facing direction:
     * false = left
     * true = right
     * @param facing the facing direction
     */
    public void setFacing(boolean facing) {
        this.facing = facing;
    }

}

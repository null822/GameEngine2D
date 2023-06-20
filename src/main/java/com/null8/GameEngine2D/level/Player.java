package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.TextureSet;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;

public class Player extends GameObject {

    private int state = 0;
    private boolean facing = false;

    private final TextureSet stand;
    private final TextureSet crouch;
    private final TextureSet walk;
    private final TextureSet leap;

    public Player(String name, TextureSet stand, TextureSet walk, TextureSet crouch, TextureSet leap, Shader shader, int width, int height, float zHeight) {
        super(name, stand.getTexture(0), shader, width, height, zHeight);
        this.stand = stand;
        this.walk = walk;
        this.crouch = crouch;
        this.leap = leap;

    }

    public Player(Player template) {
        super(template);
        this.stand = template.stand;
        this.walk = template.walk;
        this.crouch = template.crouch;
        this.leap = template.leap;
        this.state = 0;
        this.facing = false;

    }

    public void render(Matrix4f pr_matrix, boolean step) {

        switch (state) {
            case 0 -> tex = stand.getTexture(facing);
            case 1 -> tex = crouch.getTexture(facing);
            case 2 -> tex = step ? walk.getTexture(facing) : stand.getTexture(facing);
            case 3 -> tex = leap.getTexture(facing);
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


    public Player move(Vec3<Float> newPos) {
        this.pos = newPos;
        return this;
    }
    public Player move(Vec2<Float> newPos) {
        this.pos = new Vec3<>(newPos, this.pos.z);
        return this;
    }
    public Player moveCopy(Vec3<Float> newPos) {
        return new Player(this).move(newPos);
    }
    public Player moveCopy(Vec2<Float> newPos) {
        return new Player(this).move(new Vec3<>(newPos, this.pos.z));
    }
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Set the facing direction
     * false = left
     * true = right
     * @param facing - the facing direction
     */
    public void setFacing(boolean facing) {
        this.facing = facing;
    }

}

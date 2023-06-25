package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;

public class GameObject {

    String name;

    VertexArray texVert;
    Texture tex;
    Shader shader;

    Vec3<Float> pos;

    float width;
    float height;

    float heightOffset;


    public GameObject(String name, Texture tex, Shader shader, int width, int height, float zHeight) {

        this.tex = tex;
        this.shader = shader;
        this.name = name;

        this.width = width;
        this.height = height;

        this.pos = new Vec3<>(0f, 0f, zHeight);

        this.heightOffset = 12f;


        setup();
    }

    public GameObject(GameObject template) {

        this.tex = template.tex;
        this.shader = template.shader;
        this.name = template.name;

        this.width = template.width;
        this.height = template.height;

        this.pos = new Vec3<>(0f, 0f, 0f);

        this.heightOffset = 12f;

        setup();
    }

    public void setup() {

        float xSize = width;
        float ySize = height;


        float[] vertices = new float[] {
                0,     0 + heightOffset,     pos.z,
                0,     ySize + heightOffset, pos.z,
                xSize, ySize + heightOffset, pos.z,
                xSize, 0 + heightOffset,     pos.z
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


    public void render(Matrix4f pr_matrix) {

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

    public String getName() {
        return this.name;
    }
    public float getWidth() {
        return this.width;
    }
    public float getHeight() {
        return this.height;
    }
    public float getZHeight() {
        return this.pos.z;
    }
    public void setZHeight(float zHeight) {
        this.pos.z = zHeight;
    }
    public Vec3<Float> getPos() {
        return this.pos;
    }


    public GameObject move(Vec3<Float> newPos, boolean add) {
        if (add)
            this.pos = new Vec3<>(newPos.x + this.pos.x, newPos.y + this.pos.y, newPos.z + this.pos.z);
        else
            this.pos = newPos;
        return this;
    }

    public GameObject move(Vec2<Float> newPos) {
        this.pos = new Vec3<>(newPos, this.pos.z);
        return this;
    }
    public GameObject move(Vec3<Float> newPos) {
        this.pos = newPos;
        return this;
    }

    public GameObject move(Vec2<Float> newPos, boolean add) {
        if (add)
            this.pos = new Vec3<>(newPos.x + this.pos.x, newPos.y + this.pos.y, this.pos.z);
        else
            this.pos = new Vec3<>(newPos, this.pos.z);
        return this;
    }
    public GameObject moveCopy(Vec3<Float> newPos, boolean add) {
        return new GameObject(this).move(newPos, add);
    }
    public GameObject moveCopy(Vec2<Float> newPos, boolean add) {
        return new GameObject(this).move(new Vec3<>(newPos, 0f), add);
    }

    public GameObject moveCopy(Vec2<Float> newPos) {
        return new GameObject(this).move(new Vec3<>(newPos, this.pos.z), false);
    }

}

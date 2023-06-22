package com.null8.GameEngine2D.level.manager;

import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Player;
import com.null8.GameEngine2D.math.Vec3;

import java.lang.reflect.Type;

public class GameObjectRepresenter {
    private final String name;
    private Vec3<Float> pos;
    private final Type type;
    private final int index;

    public GameObjectRepresenter(GameObject template, int index) {
        this.name = template.getName();
        this.pos = template.getPos();
        this.type = GameObject.class;
        this.index = index;
    }

    public GameObjectRepresenter(FakePlayer template, int index) {
        this.name = template.getName();
        this.pos = template.getPos();
        this.type = FakePlayer.class;
        this.index = index;
    }

    public GameObjectRepresenter(Player template, int index) {
        this.name = template.getName();
        this.pos = template.getPos();
        this.type = Player.class;
        this.index = index;
    }

    public String getName() {
        return this.name;
    }
    public Vec3<Float> getPos() {
        return this.pos;
    }
    public float getZHeight() {
        return this.pos.z;
    }
    public int locate() {
        return this.index;
    }
    public Type getType() {
        return this.type;
    }

}

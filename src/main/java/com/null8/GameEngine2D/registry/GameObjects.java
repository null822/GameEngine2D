package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.math.Vec3;

public class GameObjects {
    public static final GameObject TEST_OBJECT = new GameObject(Textures.SLOPE_SMALL, Shaders.TEXTURE, new Vec3(2f, 2f, 2f), 10, 10);
}

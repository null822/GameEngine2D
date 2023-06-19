package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;

public class Levels {
    public static final Level TEST_LEVEL = new Level(Textures.BACKGROUND,
            GameObjects.PLAYER.moveCopy(new Vec2<>(0f, 0f)), new GameObject[]{

            GameObjects.TEST_SMALL.moveCopy(new Vec2<>( 0f, 0f)),
            GameObjects.TEST_SMALL.moveCopy(new Vec2<>(12f, 0f)),
            GameObjects.TEST_SMALL.moveCopy(new Vec3<>(24f, 0f, 4f)),
            GameObjects.TEST_SMALL.moveCopy(new Vec3<>(36f, 0f, 4f)),
            GameObjects.TEST_TALL .moveCopy(new Vec2<>(48f, 0f)),
            GameObjects.TEST_TALL .moveCopy(new Vec2<>(60f, 0f)),
            GameObjects.TEST_TALL .moveCopy(new Vec3<>(72f, 0f, 4f)),
            GameObjects.TEST_TALL .moveCopy(new Vec3<>(84f, 0f, 4f)),
    });

}

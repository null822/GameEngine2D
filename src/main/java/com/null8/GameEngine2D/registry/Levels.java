package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Vec2;

import static com.null8.GameEngine2D.registry.GameObjects.*;

public class Levels {
    public static final Level A3_S4 = new Level(Textures.A3_S4_BACKGROUND,
            Players.MACBETH.moveCopy(new Vec2<>(12f, 8f)), new GameObject[]{

            CHAIR.moveCopy(new Vec2<>(64f + 28, 8f)),
            CHAIR.moveCopy(new Vec2<>(64f + 28 + 51, 8f)),
            CHAIR.moveCopy(new Vec2<>(64f + 28 + 102, 8f)),
            CHAIR.moveCopy(new Vec2<>(64f + 28 + 153, 8f)),

            TABLE.moveCopy(new Vec2<>(64f, 8f)),

            BANQUO.moveCopy(new Vec2<>(64f, 60f)),


    }, new FakePlayer[]{
            ROSS.moveCopy(new Vec2<>(72f, 8f)),
    }, new float[] {12, 0, 12, 8});


}

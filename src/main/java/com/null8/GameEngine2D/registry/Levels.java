package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Vec2;

import static com.null8.GameEngine2D.registry.GameObjects.*;

public class Levels {

    private static float floor = 8f;

    private static float chair1X = 64f + 35;
    private static float chair2X = 64f + 35 + 51;
    private static float chair3X = 64f + 35 + 102;
    private static float chair4X = 64f + 35 + 153;

    public static final Level A3_S4 = new Level(Textures.A3_S4_BACKGROUND,
            Players.MACBETH.moveCopy(new Vec2<>(12f, 8f)), new GameObject[]{

            PAUSED.moveCopy(new Vec2<>(-100f, -100f)),

            CHAIR.moveCopy(new Vec2<>(chair1X, floor)),
            CHAIR.moveCopy(new Vec2<>(chair2X, floor)),
            CHAIR.moveCopy(new Vec2<>(chair3X, floor)),
            CHAIR.moveCopy(new Vec2<>(chair4X, floor)),

            TABLE.moveCopy(new Vec2<>(64f, floor)),

            BANQUO.moveCopy(new Vec2<>(chair2X, floor + 180f)),

            ATTENDANT1.moveCopy(new Vec2<>(chair1X, floor)),
            ATTENDANT2.moveCopy(new Vec2<>(chair3X, floor)),
            LADY_MACBETH.moveCopy(new Vec2<>(chair4X, floor)),


    }, new FakePlayer[]{
            ROSS.moveCopy(new Vec2<>(500f, floor)),
            LENNOX.moveCopy(new Vec2<>(450f, floor)),
    }, new float[] {12, 0, 12, 8});


}

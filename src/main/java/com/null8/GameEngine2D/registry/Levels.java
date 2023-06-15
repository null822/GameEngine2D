package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.Main;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;

public class Levels {
    public static final Level TEST_LEVEL = new Level(Textures.SLOPE, new GameObject[]{
        GameObjects.TEST_OBJECT
    });
}

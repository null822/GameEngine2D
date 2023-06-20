package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.GameObject;

public class GameObjects {

    public static final GameObject TEST = new GameObject("test_object", Textures.TEST, Shaders.TEXTURE, 8, 8, 2);
    public static final GameObject TEST_SMALL = new GameObject("test_object", Textures.TEST_SMALL, Shaders.TEXTURE, 4, 4, 2);
    public static final GameObject TEST_TALL = new GameObject("test_object", Textures.TEST_TALL, Shaders.TEXTURE, 4, 8, 2);
}

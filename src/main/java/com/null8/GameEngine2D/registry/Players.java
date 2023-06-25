package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.Player;

import static com.null8.GameEngine2D.registry.Textures.*;

public class Players {
    public static final Player MACBETH = new Player("macbeth",
            MACBETH_STAND, MACBETH_WALK, MACBETH_CROUCH, MACBETH_LEAP,
            Shaders.TEXTURE, 32, 96, 4);

}

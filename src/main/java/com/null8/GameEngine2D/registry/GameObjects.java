package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;

public class GameObjects {

    public static final GameObject CHAIR = new GameObject("chair", Textures.CHAIR, Shaders.TEXTURE, 40, 80, -4);
    public static final GameObject TABLE = new GameObject("table", Textures.TABLE, Shaders.TEXTURE, 256, 64, 0);

    public static final GameObject BANQUO = new GameObject("banquo", Textures.BANQUO, Shaders.TEXTURE, 29, 40, 3);
    public static final FakePlayer ROSS = new FakePlayer("ross", Textures.ROSS_STAND, Textures.ROSS_WALK, Shaders.TEXTURE, 32, 96, 1);

}

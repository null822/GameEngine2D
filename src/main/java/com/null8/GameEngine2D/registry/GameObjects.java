package com.null8.GameEngine2D.registry;

import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;

public class GameObjects {

    public static final GameObject PAUSED = new GameObject("paused", Textures.PAUSED, Shaders.TEXTURE, 45, 37, 5);


    public static final GameObject CHAIR = new GameObject("chair", Textures.CHAIR, Shaders.TEXTURE, 29, 64, -4);
    public static final GameObject TABLE = new GameObject("table", Textures.TABLE, Shaders.TEXTURE, 256, 64, 0);

    public static final GameObject ATTENDANT1 = new GameObject("attendant1", Textures.ATTENDANT1, Shaders.TEXTURE, 29, 80, -3);
    public static final GameObject ATTENDANT2 = new GameObject("attendant2", Textures.ATTENDANT2, Shaders.TEXTURE, 29, 80, -3);
    public static final GameObject LADY_MACBETH = new GameObject("lady_macbeth", Textures.LADY_MACBETH, Shaders.TEXTURE, 29, 80, -3);

    public static final GameObject BANQUO = new GameObject("banquo", Textures.BANQUO, Shaders.TEXTURE, 29, 40, 3);

    public static final FakePlayer MURDERER2 = new FakePlayer("murderer_2", Textures.MURDERER2_STAND, Textures.MURDERER2_WALK, Shaders.TEXTURE, 32, 96, 1);
    public static final FakePlayer MURDERER1 = new FakePlayer("murderer_1", Textures.MURDERER1_STAND, Textures.MURDERER1_WALK, Shaders.TEXTURE, 32, 96, 2);

}

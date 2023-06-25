package com.null8.GameEngine2D;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.registry.Levels;
import com.null8.GameEngine2D.registry.Shaders;
import com.null8.GameEngine2D.util.MathUtils;
import com.null8.GameEngine2D.util.TextUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.null8.GameEngine2D.graphics.text.Text.*;
import static com.null8.GameEngine2D.util.MathUtils.clamp;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Main implements Runnable {

    private static int width = 800;
    private static int height = 500;

    private static long window;

    private static volatile Level level;

    private static boolean isResized;
    private static int fps;
    private static int tps;
    private static long lastFrameTime;
    private static float delta;
    private static final double ns = 1000000000.0 / 60.0;
    private static GLFWWindowSizeCallback sizeCallback;

    private static volatile boolean isInitialized = false;
    private static boolean isRunning = true;

    // text rendering
    private static final List<BufferedImage> textQueue = new ArrayList<>();
    private static final List<Vec2<Integer>> textQueueMeta = new ArrayList<>();
    private static final List<GameObject> textQueueOut = new ArrayList<>();

    // tick animation
    private static int currentTick = -1;
    private static Vec2<Float> murderer_2Vel = new Vec2<>(0f, 0f);
    private static Vec2<Float> murderer_1Vel = new Vec2<>(0f, 0f);

    private static Vec2<Float> banquoVel = new Vec2<>(0f, 0f);

    // Movement and position

    private static final boolean[] heldMovementKeys = new boolean[4];

    private static final Vec2<Float> pos = new Vec2<>(0.0f, 0.0f);
    private static final Vec2<Float> vel = new Vec2<>(0.0f, 0.0f);

    private static final float velMax = 1f;
    private static final float velDamping = 0.25f;
    private static final float gravity = 0.25f;

    private static final float jumpForce = 3.0f;
    private static final float walkForce = 0.1f;

    public static final int pixelsPerStep = 32;

    private static int state = 0;
    private static boolean facing = true;
    private static boolean step = false;
    private static short crouch = 60;


    public void start() {
        Thread thread = new Thread(this, "Render");
        thread.start();

        ticks();
    }

    private static void init() {
        if (!glfwInit()) {
            System.err.println("Could not initialize GLFW!");
            return;
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        window = glfwCreateWindow(width, height, "GameEngine2D", NULL, NULL);
        if (window == NULL) {
            System.err.println("Could not create GLFW window!");
            return;
        }

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidmode != null;
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        createCallbacks();

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        System.out.println("Using OpenGL " + glGetString(GL_VERSION));

        level = Levels.A3_S4;

        setupElements();

    }

    private static void createCallbacks() {
        sizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                isRunning = !isRunning;
            }

            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) heldMovementKeys[0] = true;
            if (key == GLFW_KEY_W && action == GLFW_PRESS) heldMovementKeys[0] = true;
            if (key == GLFW_KEY_A && action == GLFW_PRESS) heldMovementKeys[1] = true;
            if (key == GLFW_KEY_S && action == GLFW_PRESS) heldMovementKeys[2] = true;
            if (key == GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS) heldMovementKeys[2] = true;
            if (key == GLFW_KEY_D && action == GLFW_PRESS) heldMovementKeys[3] = true;

            if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) heldMovementKeys[0] = false;
            if (key == GLFW_KEY_W && action == GLFW_RELEASE) heldMovementKeys[0] = false;
            if (key == GLFW_KEY_A && action == GLFW_RELEASE) heldMovementKeys[1] = false;
            if (key == GLFW_KEY_S && action == GLFW_RELEASE) heldMovementKeys[2] = false;
            if (key == GLFW_KEY_LEFT_SHIFT && action == GLFW_RELEASE) heldMovementKeys[2] = false;
            if (key == GLFW_KEY_D && action == GLFW_RELEASE) heldMovementKeys[3] = false;

        });

        glfwSetWindowSizeCallback(window, sizeCallback);
    }

    public static void setupElements() {
        level.setup((float) width / height);
    }

    public void run() {
        init();

        lastFrameTime = System.nanoTime();
        delta = 0.0f;
        long timer = System.currentTimeMillis();
        tps = 0;
        fps = 0;


        isInitialized = true;
        System.out.println("Initialized!");

        while (!glfwWindowShouldClose(window)) {
            if (isResized) {
                GL11.glViewport(0, 0, width, height);
                setupElements();
                isResized = false;
            }

            fps++;



            render();

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                //System.out.println(tps + " tps, " + fps + " fps");
                tps = 0;
                fps = 0;
            }
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }


    private static void ticks() {

        while (!isInitialized) {
            try {
                Thread.sleep(50);
            } catch(Exception ignored) {}
        }

        boolean prev = true;

        while (!glfwWindowShouldClose(window)) {

            if (isRunning) {
                long now = System.nanoTime();
                delta += (now - lastFrameTime) / ns;
                lastFrameTime = now;
                if (delta >= 1.0) {
                    tps++;
                    delta--;

                    currentTick++;
                    tick();
                }
            }

            if (prev != isRunning) {
                System.out.println(isRunning);
                if (isRunning) {
                    level.getGameObject("paused").move(new Vec2<>(-100.0f, -100.0f));
                    prev = isRunning;
                    lastFrameTime = System.nanoTime();
                    delta = 0.0f;
                } else {
                    level.getGameObject("paused").move(new Vec2<>(128.0f + level.getFrameX(), level.maxYPos()));
                    prev = isRunning;
                }
            }
        }
    }


    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int error = glGetError();
        if (error != GL_NO_ERROR)
            System.out.println(error);

        if (!textQueue.isEmpty()) {

            List<BufferedImage> textQueueCopy = new ArrayList<>(textQueue);
            List<Vec2<Integer>> textQueueMetaCopy = new ArrayList<>(textQueueMeta);

            int i = 0;
            for (BufferedImage image:textQueueCopy) {
                Vec2<Integer> size = textQueueMetaCopy.get(i);
                Texture textTexture = new Texture("text", image);
                GameObject textElement = new GameObject("text", textTexture, Shaders.TEXT, size.x, size.y, 4);
                textQueueOut.add(textElement);

                i++;
            }

            textQueueMeta.clear();
            textQueue.clear();

            textQueueMetaCopy.clear();
            textQueueCopy.clear();

        }

        level.render(step);

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private static void tick() {

        float minY = level.minYPos();
        float maxY = level.maxYPos();
        float minX = level.minXPos();
        float maxX = level.maxXPos();

        float currentWalkForce = walkForce;
        float currentVelMax = velMax;

        if (heldMovementKeys[1] || heldMovementKeys[3])
            facing = heldMovementKeys[3];

        // movements
        if (pos.y == minY) {

            if (state == 3) {
                state = 0;
            }

            // crouching
            if (heldMovementKeys[2]) {
                crouch = 1;
                currentWalkForce /= 4;
                currentVelMax /= 4;
                vel.y -= walkForce;
                state = 1;
            }

            // walking / sneaking
            if ((heldMovementKeys[1] || heldMovementKeys[3]) && crouch > 30) state = 2;

            if (!(heldMovementKeys[1] && heldMovementKeys[3])) {
                if (heldMovementKeys[1]) vel.x -= currentWalkForce;
                if (heldMovementKeys[3]) vel.x += currentWalkForce;
            } else {
                vel.x /= (velDamping + 1);
            }


            // leaping
            if (heldMovementKeys[0] && (heldMovementKeys[1] || heldMovementKeys[3])) {
                if (crouch <= 30) {
                    vel.y = currentWalkForce * 28;
                    vel.x = heldMovementKeys[1] ? vel.x - currentWalkForce * 8 : vel.x + currentWalkForce * 8;
                } else {
                    vel.y = currentWalkForce * 16;
                    vel.x = heldMovementKeys[1] ? vel.x - currentWalkForce * 6 : vel.x + currentWalkForce * 6;
                }
                state = 3;
            }

            // jumping
            if (heldMovementKeys[0] && !(heldMovementKeys[1] || heldMovementKeys[3])) {
                vel.y = state == 1 ? jumpForce * 1.5f : jumpForce;
                state = 0;
            }
        }

        // handle ticks since crouch
        if (crouch < 60)
            crouch++;


        // add velocity to position
        pos.x += vel.x;
        pos.y += vel.y;

        // decrement player velocity limit it
        vel.x = pos.y <= minY ? MathUtils.clamp(!heldMovementKeys[1] && !heldMovementKeys[3] ? vel.x / (velDamping + 1) : vel.x, -currentVelMax, currentVelMax) : vel.x;
        vel.y -= gravity;

        // set velocity to 0 if it is close enough
        if (Math.abs(vel.x) <= 0.001) vel.x = 0f;
        if (Math.abs(vel.y) <= 0.001) vel.y = 0f;

        // set respective velocity to 0 when touching any map edge
        if (pos.x <= minX && !heldMovementKeys[3]) vel.x = 0.0f;
        if (pos.x >= maxX - level.getPlayer().getWidth() && !heldMovementKeys[1]) vel.x = 0.0f;

        if (pos.y <= minY && !heldMovementKeys[0]) vel.y = 0.0f;
        if (pos.y >= maxY - level.getPlayer().getHeight() + 1 && vel.y > 0f) vel.y = 0.0f;

        // keep player within world bounds
        pos.x = clamp(pos.x, minX, maxX - level.getPlayer().getWidth());
        pos.y = clamp(pos.y, minY, maxY - level.getPlayer().getHeight());


        // update stats
        level.setPos(pos);
        level.getPlayer().setState(state);
        level.getPlayer().setFacing(facing);

        int modPos = Math.round(pos.x) % pixelsPerStep;
        step = modPos <= pixelsPerStep / 2;



        // all other code to run every tick (such as actual game updates etc.)

        FakePlayer murderer_2 = level.getFakePlayer("murderer_2");
        FakePlayer murderer_1 = level.getFakePlayer("murderer_1");

        GameObject banquo = level.getGameObject("banquo");

        boolean indefWait = false;

        if (currentTick == 0) {
            murderer_2.setState(2);
            murderer_2.setFacing(false);

            murderer_1.setState(2);
            murderer_1.setFacing(false);

        } else if (currentTick >= 60 && currentTick < 70) {
            murderer_1Vel.x -= 0.1f;
            murderer_2Vel.x -= 0.1f;

        } else if (currentTick >= 450 && currentTick < 455) {
            murderer_1Vel.x += 0.2f;
            murderer_2Vel.x += 0.2f;

        } else if (currentTick == 500) {
            dialogue("Macbeth", new String[] {
                    "There's blood on your face."
            });

        } else if (currentTick == 650) {
            dialogue("Murderer 1", new String[] {
                    "It's Banquo's then."
            });
        } else if (currentTick == 800) {
            dialogue("Macbeth", new String[] {
                    "Is he dead?"
            });
        } else if (currentTick == 950) {
            dialogue("Murderer 1", new String[] {
                    "His throat is cut,",
                    "I did that for him."
            });
        } else if (currentTick == 1150) {
            dialogue("Macbeth", new String[] {
                    "Did you do the same for Fleance?"
            });
        } else if (currentTick == 1300) {
            dialogue("Murderer 2", new String[] {
                    "My loyal sir,",
                    "Fleance escaped."
            });
        } else if (currentTick == 1450) {
            dialogue("Macbeth", new String[] {
                    "It was so close to perfect.",
                    "But Banquo's dead?"
            });
        } else if (currentTick == 1650) {
            dialogue("Murderer 2", new String[] {
                    "Ay, my good lord, he is dead."
            });
        } else if (currentTick == 1800) {
            dialogue("Macbeth", new String[] {
                    "Thanks for that."
            });
        } else if (currentTick >= 1900 && currentTick < 1910) {

            murderer_1.setFacing(true);
            murderer_2.setFacing(true);

            murderer_1Vel.x += 0.1f;
            murderer_2Vel.x += 0.1f;

        } else if (currentTick >= 2010 && currentTick < 2020) {
            murderer_1Vel.x -= 0.1f;
            murderer_2Vel.x -= 0.1f;
        } else if (currentTick == 2025) {
            murderer_1.setFacing(false);
            murderer_2.setFacing(false);

        } else if (currentTick >= 2400 && currentTick < 2410) {
            banquoVel.y = banquoVel.y - 0.1f;
        } else if (currentTick == 2410) {
            indefWait = true;

            if (banquo.getPos().y <= 38) {
                banquoVel.y = 0f;
                banquo.move(new Vec2<>(banquo.getPos().x, 38f));
                indefWait = false;
            }
        } else if (currentTick == 2420) {
            dialogue("Lady Macbeth", new String[]{
                    "Come, sit down."
            });
        } else if (currentTick == 2550) {
            dialogue("Macbeth", new String[]{
                    "The table's full."
            });
        } else if (currentTick == 2750) {
            dialogue("Lady Macbeth", new String[]{
                    "Here's a place reserved"
            });
        } else if (currentTick == 2900) {
            dialogue("Macbeth", new String[]{
                    "Where?"
            });
        } else if (currentTick == 3050) {
            dialogue("Lady Macbeth", new String[]{
                    "Here!"
            });
        } else if (currentTick == 3200) {
            dialogue("Macbeth", new String[]{
                    "Which of you have done this?"
            });
        } else if (currentTick == 3350) {
            dialogue("Lords", new String[]{
                    "What, my good lord?"
            });
        } else if (currentTick == 3500) {
            dialogue("Macbeth", new String[]{
                    "You cannot say I did it.",
                    "The gore is terrible."
            });
        } else if (currentTick == 3700) {
            dialogue("Murderer 2", new String[]{
                    "Macbeth is not well."
            });
        } else if (currentTick == 3850) {
            dialogue("Lady Macbeth", new String[]{
                    "Sit, Macbeth is often like this.",
                    "He will be well again in a moment."
            });
        } else if (currentTick == 4100) {
            dialogue("Macbeth", new String[]{
                    "He's still there!",
            });
        } else if (currentTick == 4250) {
            dialogue("Lady Macbeth", new String[]{
                    "All is good, Macbeth.",
            });
        } else if (currentTick == 4400) {
            dialogue("Macbeth", new String[]{
                    "Do you not see him?",
            });
            banquoVel.y = -1f;
        } else if (currentTick == 4500) {
            banquoVel.y = 0f;
            banquo.move(new Vec2<>(banquo.getPos().x, 180f));
        } else if (currentTick == 4400) {
            dialogue("Lady Macbeth", new String[]{
                    "There is nothing here!",
            });
        } else if (currentTick == 4550) {
            dialogue("Macbeth", new String[]{
                    "He was there a second ago!",
            });
        }


        if (indefWait) {
            currentTick--;
        }


        murderer_2.move(murderer_2Vel, true);
        murderer_1.move(murderer_1Vel, true);

        banquo.move(banquoVel, true);


        // add dialogues to level
        List<GameObject> textQueueOutCopy = new ArrayList<>(textQueueOut);
        for(GameObject textElement:textQueueOutCopy) {
            textElement.move(new Vec2<>(level.getFrameX(), maxY));
            level.setText(textElement);
        }

        textQueueOut.clear();
        textQueueOutCopy.clear();

        // move existing dialogue
        GameObject text = level.getManager().getText("text");

        if (text != null)
            text.move(new Vec2<>(level.getFrameX(), maxY - 48));

    }

    private static void dialogue(String name, String[] lines) {

        String[] strings = new String[lines.length + 2];

        strings[0] = name.toUpperCase();
        strings[1] = TextUtils.charGen(name.length(), '='); // underline (very necessary)

        int i = 2;
        for(String line:lines) {
            strings[i] = line;
            i++;
        }

        char[][] dialogue = charsFromStrings(strings);

        textQueue.add(imageFromText(1536, 256, 12, new Color(0, 0, 0, 0), makeCCArray(dialogue)));
        textQueueMeta.add(new Vec2<>(512, 85));
    }

    public static void main(String[] args) {
        new Main().start();
    }


    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getTps() {
        return tps;
    }
    public int getFps() {
        return fps;
    }

}

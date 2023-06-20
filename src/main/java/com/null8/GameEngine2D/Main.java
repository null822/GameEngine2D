package com.null8.GameEngine2D;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.registry.Levels;
import com.null8.GameEngine2D.registry.Shaders;
import com.null8.GameEngine2D.util.MathUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.null8.GameEngine2D.util.MathUtils.clamp;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Main implements Runnable {

    private int width = 800;
    private int height = 500;

    private long window;

    private volatile Level level;

    private static boolean isResized;
    private static int fps;
    private static int tps;
    private static long lastFrameTime;
    private static float delta;
    private static final double ns = 1000000000.0 / 60.0;
    private static GLFWWindowSizeCallback sizeCallback;

    private volatile boolean isInitialized = false;

    private final List<BufferedImage> textQueue = new ArrayList<>();
    private final List<Vec2<Integer>> textQueueMeta = new ArrayList<>();
    private final List<GameObject> textQueueOut = new ArrayList<>();

    // Movement and position

    private final boolean[] heldMovementKeys = new boolean[4];

    private final Vec2<Float> pos = new Vec2<>(0.0f, 0.0f);
    private final Vec2<Float> vel = new Vec2<>(0.0f, 0.0f);

    private final float velMax = 0.25f;
    private final float velInc = 0.04f;

    private final float velDamping = 0.25f;
    private final float gravity = 0.1f;

    private int state = 0;
    private boolean facing = false;
    private boolean step = false;
    private short crouch = 60;


    public void start() {
        Thread thread = new Thread(this, "Render");
        thread.start();

        ticks();
    }

    private void init() {
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

        level = Levels.TEST_LEVEL;

        setupElements();

    }

    private void createCallbacks() {
        sizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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

    public void setupElements() {
        level.setup((float) this.width / this.height);
    }

    public void run() {
        init();

        lastFrameTime = System.nanoTime();
        delta = 0.0f;
        long timer = System.currentTimeMillis();
        tps = 0;
        fps = 0;


        this.isInitialized = true;
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


    private void ticks() {

        while (!this.isInitialized) {
            try {
                Thread.sleep(50);
            } catch(Exception ignored) {}
        }

        while (!glfwWindowShouldClose(window)) {
            long now = System.nanoTime();
            delta += (now - lastFrameTime) / ns;
            lastFrameTime = now;
            if (delta >= 1.0) {
                tps++;
                delta--;

                tick();
            }
        }
    }


    private void render() {
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
                GameObject textElement = new GameObject("debug", textTexture, Shaders.TEXT, size.x, size.y, 2);
                textQueueOut.add(textElement);
                textQueue.remove(image);

                i++;
            }

        }

        level.render(step);

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void tick() {

        float currentVel = velInc;
        float currentVelMax = velMax;

        facing = heldMovementKeys[3];

        // movements
        if (pos.y == 0f) {

            if (state == 3) {
                state = 0;
            }

            // crouching
            if (heldMovementKeys[2]) {
                crouch = 1;
                currentVel /= 4;
                currentVelMax /= 4;
                vel.y -= velInc;
                state = 1;
            }

            // walking / sneaking
            if ((heldMovementKeys[1] || heldMovementKeys[3]) && crouch > 30) state = 2;

            if (!(heldMovementKeys[1] && heldMovementKeys[3])) {
                if (heldMovementKeys[1]) vel.x -= currentVel;
                if (heldMovementKeys[3]) vel.x += currentVel;
            } else {
                vel.x /= (velDamping + 1);
            }


            // leaping
            if (heldMovementKeys[0] && (heldMovementKeys[1] || heldMovementKeys[3])) {
                if (crouch <= 30) {
                    vel.y = currentVel * 28;
                    vel.x = heldMovementKeys[1] ? vel.x - currentVel * 8 : vel.x + currentVel * 8;
                } else {
                    vel.y = currentVel * 16;
                    vel.x = heldMovementKeys[1] ? vel.x - currentVel * 6 : vel.x + currentVel * 6;
                }
                state = 3;

            }

            // jumping
            if (heldMovementKeys[0] && !(heldMovementKeys[1] || heldMovementKeys[3])) {
                vel.y = state == 1 ? 1.25f : 0.75f;
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
        vel.x = pos.y <= 0f ? MathUtils.clamp(!heldMovementKeys[1] && !heldMovementKeys[3] ? vel.x / (velDamping + 1) : vel.x, -currentVelMax, currentVelMax) : vel.x;
        vel.y -= gravity;

        // set velocity to 0 if it is close enough
        if (Math.abs(vel.x) <= 0.001) vel.x = 0f;
        if (Math.abs(vel.y) <= 0.001) vel.y = 0f;

        // set respective velocity to 0 when touching any map edge
        if (pos.x <= 0.0f && !heldMovementKeys[3]) vel.x = 0.0f;
        if (pos.x >= level.maxXPos() - level.getPlayer().getWidth() && !heldMovementKeys[1]) vel.x = 0.0f;

        if (pos.y <= 0f && !heldMovementKeys[0]) vel.y = 0.0f;
        if (pos.y >= level.maxYPos() - level.getPlayer().getHeight() + 1 && vel.y > 0f) vel.y = 0.0f;

        // keep player within world bounds
        pos.x = clamp(pos.x, 0f, level.maxXPos() - level.getPlayer().getWidth());
        pos.y = clamp(pos.y, 0f, level.maxYPos() - level.getPlayer().getHeight());


        // update stats
        level.setPos(pos);
        level.getPlayer().setState(state);
        level.getPlayer().setFacing(facing);

        int modPos = Math.round(pos.x) % 8;
        step = modPos == 0 || modPos == 1 || modPos == 2 || modPos == 3;

        // display debug stats

//        char[][] text = charsFromStrings( new String[] {
//                "pos: " + pos + ", vel: " + vel,
//                "state: " + state + ", facing: " + facing
//        });
//
//        textQueue.add(imageFromText(896, 128, 16, new Color(0, 0, 0, 0), makeCCArray(text)));
//        textQueueMeta.add(new Vec2<>(64, 10));


        List<GameObject> textQueueOutCopy = new ArrayList<>(textQueueOut);
        for(GameObject textElement:textQueueOutCopy) {
            textElement.move(new Vec2<>(level.getFrameX(), level.maxYPos()));
            level.setText(textElement);
            textQueueOut.remove(textElement);
        }


        // all other code to run every tick (such as actual game updates etc.)

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

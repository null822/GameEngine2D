package com.null8.GameEngine2D;

import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Levels;
import com.null8.GameEngine2D.registry.Shaders;
import com.null8.GameEngine2D.util.MathUtils;
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

    private static final List<BufferedImage> textQueue = new ArrayList<>();
    private static final List<Vec2<Integer>> textQueueMeta = new ArrayList<>();
    private static final List<GameObject> textQueueOut = new ArrayList<>();

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
    private static boolean facing = false;
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
                GameObject textElement = new GameObject("debug", textTexture, Shaders.TEXT, size.x, size.y, 2);
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

        // display debug stats

        char[][] text = charsFromStrings( new String[] {
                "pos: " + pos + ", vel: " + vel,
                "state: " + state + ", facing: " + (facing ? "right" : "left"),
                "WASD: " + (heldMovementKeys[0] ? "1" : "0") + (heldMovementKeys[1] ? "1" : "0") + (heldMovementKeys[2] ? "1" : "0") + (heldMovementKeys[3] ? "1" : "0"),
                //"tq: " + textQueue.size() + " tqO: " + textQueueOut.size(), " tqM: " + textQueueMeta.size()
        });

        textQueue.add(imageFromText(896, 128, 16, new Color(0, 0, 0, 0), makeCCArray(text)));
        textQueueMeta.add(new Vec2<>(224, 32));


        List<GameObject> textQueueOutCopy = new ArrayList<>(textQueueOut);
        for(GameObject textElement:textQueueOutCopy) {
            textElement.move(new Vec2<>(level.getFrameX(), maxY));
            level.setText(textElement);
        }

        textQueueOut.clear();
        textQueueOutCopy.clear();


        // all other code to run every tick (such as actual game updates etc.)

        Vec3<Float> rossPos = level.getFakePlayer("ross").getPos();


        FakePlayer ross = level.getFakePlayer("ross");

        if (ross != null) {
            ross.move(new Vec2<>(rossPos.x, rossPos.y + 0.1f));
        } else {
            System.out.println("No Ross :c");
        }

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

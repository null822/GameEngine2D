package com.null8.GameEngine2D;

import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;
import com.null8.GameEngine2D.math.Vec3;
import com.null8.GameEngine2D.registry.Levels;
import com.null8.GameEngine2D.util.MathUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;

import static com.null8.GameEngine2D.registry.Shaders.BACKGROUND;
import static com.null8.GameEngine2D.registry.Shaders.TEXTURE;
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

    private Thread thread;
    private boolean running = false;

    private long window;

    private volatile Level level;

    private static boolean isResized;
    //private static boolean isFullscreen = false;
    private static int fps;
    private static int tps;
    private static long lastFrameTime;
    private static float delta;
    private static final double ns = 1000000000.0 / 60.0;
    //private static int[] windowPosX = new int[1], windowPosY = new int[1];
    private static GLFWWindowSizeCallback sizeCallback;

    private volatile boolean isInitialized = false;

    private volatile boolean[] heldMovementKeys = new boolean[4];

    private Vec2<Float> pos = new Vec2<>(0.0f, 0.0f);
    private Vec2<Float> vel = new Vec2<>(0.0f, 0.0f);

    private final float velMax = 2f;
    private final float velInc = 0.08f;

    private final float velDamping = 0.25f;
    private final float gravity = 0.1f;


    public void start() {
        running = true;
        thread = new Thread(this, "Render");
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

        Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);

        BACKGROUND.setUniformMat4f("pr_matrix", pr_matrix);
        BACKGROUND.setUniform1i("tex", 1);
        TEXTURE.setUniformMat4f("pr_matrix", pr_matrix);
        TEXTURE.setUniform1i("tex", 1);

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
            if (key == GLFW_KEY_D && action == GLFW_PRESS) heldMovementKeys[3] = true;

            if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) heldMovementKeys[0] = false;
            if (key == GLFW_KEY_W && action == GLFW_RELEASE) heldMovementKeys[0] = false;
            if (key == GLFW_KEY_A && action == GLFW_RELEASE) heldMovementKeys[1] = false;
            if (key == GLFW_KEY_S && action == GLFW_RELEASE) heldMovementKeys[2] = false;
            if (key == GLFW_KEY_D && action == GLFW_RELEASE) heldMovementKeys[3] = false;

        });

        //glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        //glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
        //glfwSetScrollCallback(window, input.getMouseScrollCallback());
        glfwSetWindowSizeCallback(window, sizeCallback);
    }

    public void setupElements() {
        level.setup(this.width, this.height);
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
            if (glfwWindowShouldClose(window))
                running = false;
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

        level.render();


        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void tick() {

        // add/subtract from velocity depending on held keys
        //if (heldMovementKeys[0]) vel.y += velInc;
        if (pos.y == level.minYPos()) {
            if (heldMovementKeys[1]) vel.x -= velInc;
            if (heldMovementKeys[3]) vel.x += velInc;
        }

        if (heldMovementKeys[2]) vel.y -= velInc;


        if (pos.y == level.minYPos() && heldMovementKeys[0]) {
            vel.y = 1.5f;
        }

        // set respective velocity to 0 when touching any map edge
        if (pos.x <= 0.0f && !heldMovementKeys[3]) vel.x = 0.0f;
        if (pos.x >= level.maxXPos() && !heldMovementKeys[1]) vel.x = 0.0f;

        if (pos.y <= level.minYPos() && !heldMovementKeys[0]) vel.y = 0.0f;
        if (pos.y >= level.maxYPos() && vel.y > 0f) vel.y = 0.0f;

        // add velocity to position
        pos.x += vel.x;
        pos.y += vel.y;

        // decrement player x velocity to simulate friction and limit max velocity
        vel.x = MathUtils.clamp(!heldMovementKeys[1] && !heldMovementKeys[3] ? vel.x / (velDamping + 1) : vel.x, -velMax, velMax);
        vel.y -= gravity;

        // set velocity to 0 if it is close enough
        if (Math.abs(vel.x) < 0.001) vel.x = 0f;

        // keep player within world bounds
        pos.x = clamp(pos.x, 0f, level.maxXPos());
        pos.y = clamp(pos.y, level.minYPos(), level.maxYPos());


        System.out.println("pos: " + pos + ", vel: " + vel + ", maxX: " + level.maxXPos() + ", maxY: " + level.maxYPos() + ", minY: " + level.minYPos());


        // update pos in level
        level.setPos(pos);



        // all other code to run every tick

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

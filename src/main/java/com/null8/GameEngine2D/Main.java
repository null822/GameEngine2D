package com.null8.GameEngine2D;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.level.Level;
import com.null8.GameEngine2D.math.Matrix4f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static com.null8.GameEngine2D.graphics.Shader.BACKGROUND;
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

    private Level level;

    private static boolean isResized;
    private static boolean isFullscreen = false;
    private static int fps;
    private static int tps;
    private static long lastFrameTime;
    private static float delta;
    private static final double ns = 1000000000.0 / 60.0;
    private static int[] windowPosX = new int[1], windowPosY = new int[1];
    private static GLFWWindowSizeCallback sizeCallback;



    public void start() {
        running = true;
        thread = new Thread(this, "Render");
        thread.start();
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

        Shader.loadAll();

        Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
        BACKGROUND.setUniformMat4f("pr_matrix", pr_matrix);
        BACKGROUND.setUniform1i("tex", 1);

        level = new Level(this);

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
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        //glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        //glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
        //glfwSetScrollCallback(window, input.getMouseScrollCallback());
        glfwSetWindowSizeCallback(window, sizeCallback);
    }

    public void run() {
        init();

        lastFrameTime = System.nanoTime();
        delta = 0.0f;
        long timer = System.currentTimeMillis();
        tps = 0;
        fps = 0;


        while (!glfwWindowShouldClose(window)) {
            if (isResized) {
                GL11.glViewport(0, 0, width, height);
                level.setup(this);
                isResized = false;
            }

            fps++;



            long now = System.nanoTime();
            delta += (now - lastFrameTime) / ns;
            lastFrameTime = now;
            if (delta >= 1.0) {
                tps++;
                delta--;
            }

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

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int error = glGetError();
        if (error != GL_NO_ERROR)
            System.out.println(error);

        level.render();


        glfwSwapBuffers(window);
        glfwPollEvents();
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

    public long getWindow() {
        return window;
    }

}

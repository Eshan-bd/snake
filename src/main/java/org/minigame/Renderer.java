package org.minigame;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.minigame.utils.Vector2D;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glVertex2i;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {

    private long window;
    private boolean glfwInitialized;
    private Game game;
    private GameMap gameMap;
    private Snake snake;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();

            snake = new Snake(1);
            gameMap = new GameMap(new Vector2D<>(GameConfig.GRID_COLUMNS, GameConfig.GRID_ROWS));
            game = new Game(snake, gameMap);

            loop();
        } finally {
            cleanUp();
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwInitialized = true;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(
                GameConfig.WINDOW_WIDTH,
                GameConfig.WINDOW_HEIGHT,
                GameConfig.TITLE,
                NULL,
                NULL
        );
        if (window == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (handle, key, scanCode, action, modifiers) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(handle, true);
                return;
            }

            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                handleKeyInput(key);
            }
        });

        centerWindow();
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    private void centerWindow() {
        long monitor = glfwGetPrimaryMonitor();
        if (monitor == NULL) {
            return;
        }

        GLFWVidMode videoMode = glfwGetVideoMode(monitor);
        if (videoMode == null) {
            return;
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(window, width, height);
            glfwSetWindowPos(
                    window,
                    (videoMode.width() - width.get(0)) / 2,
                    (videoMode.height() - height.get(0)) / 2
            );
        }
    }

    private void loop() {
        GL.createCapabilities();
        configureProjection();
        configureViewport();
        glfwSetFramebufferSizeCallback(window, (handle, width, height) -> glViewport(0, 0, width, height));

        glClearColor(0.08f, 0.08f, 0.08f, 1.0f);
        double nextUpdate = glfwGetTime() + GameConfig.TICK_DELAY_MS / 1000.0;

        while (!glfwWindowShouldClose(window)) {
            double now = glfwGetTime();
            if (now >= nextUpdate) {
                game.update();
                nextUpdate = now + GameConfig.TICK_DELAY_MS / 1000.0;
            }

            glClear(GL_COLOR_BUFFER_BIT);
            drawGrid();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleKeyInput(int key) {
        if (snake == null) {
            return;
        }

        Vector2D<Integer> nextDirection = directionForKey(key);
        if (nextDirection == null || isOppositeDirection(nextDirection, snake.getDirection())) {
            return;
        }

        snake.setDirection(nextDirection);
    }

    private Vector2D<Integer> directionForKey(int key) {
        return switch (key) {
            case GLFW_KEY_UP, GLFW_KEY_W -> new Vector2D<>(0, -1);
            case GLFW_KEY_DOWN, GLFW_KEY_S -> new Vector2D<>(0, 1);
            case GLFW_KEY_LEFT, GLFW_KEY_A -> new Vector2D<>(-1, 0);
            case GLFW_KEY_RIGHT, GLFW_KEY_D -> new Vector2D<>(1, 0);
            default -> null;
        };
    }

    private boolean isOppositeDirection(Vector2D<Integer> nextDirection, Vector2D<Integer> currentDirection) {
        return nextDirection.x + currentDirection.x == 0 && nextDirection.y + currentDirection.y == 0;
    }

    private void configureProjection() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    private void configureViewport() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(window, width, height);
            glViewport(0, 0, width.get(0), height.get(0));
        }
    }

    public void drawGrid() {
        int[][] grid = gameMap.getMap();

        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                if (!setColor(grid[row][column])) {
                    continue;
                }

                drawCell(column * GameConfig.CELL_SIZE, row * GameConfig.CELL_SIZE);
            }
        }
    }

    private boolean setColor(int state) {
        switch (state) {
            case -1 -> glColor3f(0.2f, 0.8f, 0.2f);
            case 1 -> glColor3f(0.35f, 0.35f, 0.35f);
            case 2 -> glColor3f(0.9f, 0.2f, 0.2f);
            case 3 -> glColor3f(1.0f, 0.75f, 0.1f);
            default -> {
                return false;
            }
        }
        return true;
    }

    private void drawCell(int x, int y) {
        int size = GameConfig.CELL_SIZE;

        glBegin(GL_QUADS);
        glVertex2i(x, y);
        glVertex2i(x + size, y);
        glVertex2i(x + size, y + size);
        glVertex2i(x, y + size);
        glEnd();
    }

    private void cleanUp() {
        if (window != NULL) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            window = NULL;
        }
        if (glfwInitialized) {
            glfwTerminate();
            glfwInitialized = false;
        }

        GLFWErrorCallback errorCallback = glfwSetErrorCallback(null);
        if (errorCallback != null) {
            errorCallback.free();
        }
    }

    public static void main(String[] args) {
        new Renderer().run();
    }
}

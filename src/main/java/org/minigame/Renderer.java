package org.minigame;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
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
    private final SceneManager sceneManager;

    public Renderer(SceneManager sceneManager) {
        this.sceneManager = Objects.requireNonNull(sceneManager);
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();
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
                sceneManager.handleKeyInputs(key);
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
                sceneManager.update();
                nextUpdate = now + GameConfig.TICK_DELAY_MS / 1000.0;
            }

            glClear(GL_COLOR_BUFFER_BIT);
            drawScene();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleKeyInput(int key) {


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

    private void drawScene() {
        switch (sceneManager.getState()) {
            case START -> drawStartScreen();
            case RUNNING -> drawGrid();
            case GAME_OVER -> drawGameOverScreen();
        }
    }

    private void drawStartScreen() {
        glColor3f(0.2f, 0.8f, 0.2f);
        drawTextCentered("SPACE TO START", GameConfig.WINDOW_HEIGHT / 2, 3);
    }

    private void drawGameOverScreen() {
        glColor3f(0.9f, 0.2f, 0.2f);
        drawTextCentered("GAME OVER", GameConfig.WINDOW_HEIGHT / 2, 4);
    }

    private void drawGrid() {
        int[][] grid = sceneManager.getGameMap().getMap();

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

    private void drawTextCentered(String text, int centerY, int scale) {
        int textWidth = textWidth(text, scale);
        int textHeight = 7 * scale;
        int x = (GameConfig.WINDOW_WIDTH - textWidth) / 2;
        int y = centerY - textHeight / 2;

        drawText(text, x, y, scale);
    }

    private int textWidth(String text, int scale) {
        return text.length() * 5 * scale + Math.max(0, text.length() - 1) * scale;
    }

    private void drawText(String text, int x, int y, int scale) {
        int cursorX = x;
        for (char letter : text.toUpperCase().toCharArray()) {
            drawGlyph(letter, cursorX, y, scale);
            cursorX += 6 * scale;
        }
    }

    private void drawGlyph(char letter, int x, int y, int scale) {
        String[] glyph = glyph(letter);

        for (int row = 0; row < glyph.length; row++) {
            for (int column = 0; column < glyph[row].length(); column++) {
                if (glyph[row].charAt(column) == '1') {
                    drawRect(x + column * scale, y + row * scale, scale, scale);
                }
            }
        }
    }

    private String[] glyph(char letter) {
        return switch (letter) {
            case 'A' -> new String[]{"01110", "10001", "10001", "11111", "10001", "10001", "10001"};
            case 'C' -> new String[]{"01111", "10000", "10000", "10000", "10000", "10000", "01111"};
            case 'E' -> new String[]{"11111", "10000", "10000", "11110", "10000", "10000", "11111"};
            case 'G' -> new String[]{"01111", "10000", "10000", "10111", "10001", "10001", "01111"};
            case 'M' -> new String[]{"10001", "11011", "10101", "10101", "10001", "10001", "10001"};
            case 'O' -> new String[]{"01110", "10001", "10001", "10001", "10001", "10001", "01110"};
            case 'P' -> new String[]{"11110", "10001", "10001", "11110", "10000", "10000", "10000"};
            case 'R' -> new String[]{"11110", "10001", "10001", "11110", "10100", "10010", "10001"};
            case 'S' -> new String[]{"01111", "10000", "10000", "01110", "00001", "00001", "11110"};
            case 'T' -> new String[]{"11111", "00100", "00100", "00100", "00100", "00100", "00100"};
            case 'V' -> new String[]{"10001", "10001", "10001", "10001", "10001", "01010", "00100"};
            default -> new String[]{"00000", "00000", "00000", "00000", "00000", "00000", "00000"};
        };
    }

    private void drawRect(int x, int y, int width, int height) {
        glBegin(GL_QUADS);
        glVertex2i(x, y);
        glVertex2i(x + width, y);
        glVertex2i(x + width, y + height);
        glVertex2i(x, y + height);
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
}

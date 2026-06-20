package org.minigame;

public final class GameConfig {

    private GameConfig() {
        // Prevent creating instances
    }

    public static final int WINDOW_WIDTH = 320;
    public static final int WINDOW_HEIGHT = 240;

    public static final int CELL_SIZE = 16;
    public static final int GRID_COLUMNS = WINDOW_WIDTH / CELL_SIZE;
    public static final int GRID_ROWS = WINDOW_HEIGHT / CELL_SIZE;

    public static final int TICK_DELAY_MS = 150;
    public static final int INITIAL_SNAKE_LENGTH = 3;

    public static final String TITLE = "Snake";
}
package org.minigame;

import org.minigame.utils.Vector2D;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class SceneManager {

    private final Game game;
    private final GameMap gameMap;
    private final Snake snake;
    private GameState state;

    public SceneManager(Game game, GameMap gameMap, Snake snake) {
        this.game = Objects.requireNonNull(game);
        this.gameMap = Objects.requireNonNull(gameMap);
        this.snake = Objects.requireNonNull(snake);
        this.state = GameState.START;
    }

    public GameState getState() {
        return state;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getScore() {
        return game.getScore();
    }

    public void startGame() {
        if (state == GameState.START) {
            state = GameState.RUNNING;
        }
    }

    public void update() {
        if (state != GameState.RUNNING) {
            return;
        }

        if (!game.update()) {
            state = GameState.GAME_OVER;
        }
    }

    public void handleKeyInputs(int key) {

        if (key == GLFW_KEY_SPACE) {
            startGame();
            return;
        }


        if (state != GameState.RUNNING)
            return;

        Vector2D<Integer> nextDirection = directionForKey(key);

        if (nextDirection != null)
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
}

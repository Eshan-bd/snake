package org.minigame;

import org.minigame.utils.Vector2D;

public class Main {
    public static void main(String[] args) {
        playSnake();
    }

    public static void playSnake() {
        var snake = new Snake(1);
        var gameMap = new GameMap(new Vector2D<>(GameConfig.GRID_COLUMNS, GameConfig.GRID_ROWS));
        var game = new Game(snake, gameMap);

        new Renderer(game, gameMap, snake).run();
    }
}

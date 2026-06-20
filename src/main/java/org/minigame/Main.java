package org.minigame;

import org.minigame.utils.Vector2D;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        playSnake();
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
    }

    public static void playSnake() {
        int windowWidth = 640;
        int windowHeight = 480;

        int cellSize = 20;

        int columns = windowWidth / cellSize; // 32
        int rows = windowHeight / cellSize;   // 24

        var snake = new Snake(1);
        var gameMap = new GameMap(new Vector2D<Integer>(GameConfig.GRID_COLUMNS, GameConfig.GRID_ROWS));

        var game = new Game(snake, gameMap);

        while (true) {
            game.update();
            gameMap.printMap();

            try {
                Thread.sleep(1000); // 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


    }
}
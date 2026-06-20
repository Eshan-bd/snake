package org.minigame;

public class Game {

    private final Snake snake;
    private final GameMap gameMap;
    private int cycles;

    private int score;

    public Game(Snake snake, GameMap gameMap) {
        this.snake = snake;
        this.gameMap = gameMap;
        cycles = 0;

        drawSnake();
    }

    private void drawSnake() {
        for (var v : snake.getBody())
            gameMap.setGrid(v, -1);
    }

    public boolean update() {
        var bound = gameMap.getBound();

        var head = snake.getHead().add(snake.getDirection());

        if (head.x < 0 || head.x >= bound.x || head.y < 0 || head.y >= bound.y) {
            return false;
        }

        GridState grid = gameMap.getGridState(head);

        boolean grow = false;
        switch (grid) {
            case SNAKE:
                return false;

            case OBSTACLE:
                return false;

            case Food:
                grow = true;
                score += 1;
                break;

            case BigFood:
                score += 10;
                break;
        }

        if (!grow) {
            gameMap.setGrid(snake.popTail(), 0);
        }

        gameMap.setGrid(head, -1);

        snake.setHead(head);

        gameMap.updateFood(cycles);

        cycles++;
        if (cycles == Integer.MAX_VALUE)
            return false;

        return true;
    }

    public void reset() {
        gameMap.reset();
        snake.resetPosition();
        drawSnake();
        cycles = 0;
    }

    public int getScore() {
        return score;
    }
}

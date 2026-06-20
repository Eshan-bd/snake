package org.minigame;

public class Game {

    private final Snake snake;
    private final GameMap gameMap;

    private GameState gameState;
    private int score;

    public Game(Snake snake, GameMap gameMap) {
        this.snake = snake;
        this.gameMap = gameMap;

        for (var v: snake.getBody())
            gameMap.setGrid(v, -1);

        gameState = GameState.RUNNING;
    }

    public void update() {
        if (gameState.equals(GameState.OVER))
            return;

        var bound = gameMap.getBound();

        var head = snake.getHead().add(snake.getDirection());

        if (head.x < 0 || head.x >= bound.x || head.y < 0 || head.y >= bound.y) {
            GameOverScenario();
            return;
        }

        int grid = gameMap.getGrid(head);

        if (grid < 0) {
            GameOverScenario();
            return;
        }


        boolean grow = false;
        switch(grid) {
            case 1:
                GameOverScenario();
                return;

            case 2:
                grow = true;
                score += 1;
                break;

            case 3:
                score += 10;
                break;
        }

        if (!grow) {
            gameMap.setGrid(snake.popTail(), 0);
        }

        gameMap.setGrid(head, -1);

        snake.setHead(head);
    }

    private void GameOverScenario() {
        System.out.println("Game over");
    }
}

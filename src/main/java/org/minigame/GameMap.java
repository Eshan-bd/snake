package org.minigame;

import org.minigame.utils.Vector2D;

import java.io.IOException;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameMap {

    private static final Path DEFAULT_MAP = Path.of("maps", "map_00.txt");

    private final Vector2D<Integer> bound;
    private int[][] grids;
    private Vector2D<Integer> food;
    private Vector2D<Integer> bigFood;

    public GameMap(Vector2D<Integer> bound) {
        this.bound = bound;

        initialiseMap();
        food = generateRandomPosition();
    }

    public Vector2D<Integer> getBound() {
        return bound;
    }

    private void initialiseMap() {
        int rows = bound.y;
        int columns = bound.x;

        grids = new int[rows][columns];

        List<String> mapRows;
        try {
            mapRows = Files.readAllLines(DEFAULT_MAP);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load map: " + DEFAULT_MAP, e);
        }

        if (mapRows.size() != rows) {
            throw new IllegalStateException("Map " + DEFAULT_MAP + " must have " + rows + " rows");
        }

        for (int row = 0; row < rows; row++) {
            String[] cells = mapRows.get(row).trim().split("\\s+");
            if (cells.length != columns) {
                throw new IllegalStateException("Map " + DEFAULT_MAP + " row " + row + " must have " + columns + " columns");
            }

            for (int col = 0; col < columns; col++) {
                grids[row][col] = Integer.parseInt(cells[col]);
            }
        }
    }

    public GridState getGridState(Vector2D<Integer> v) {
        int value = grids[v.y][v.x];
        return Arrays.stream(GridState.values())
                .filter(state -> state.digit() == value)
                .findFirst()
                .orElse(GridState.EMPTY);
    }

    public void setGrid(Vector2D<Integer> v, int val) {
        grids[v.y][v.x] = val;
    }

    public int[][] getMap() {
        return this.grids;
    }

    public void printMap() {

        for (var row: grids) {
            for (int grid: row) {

                System.out.print(grid + " ");
            }
            System.out.println();
        }
    }

    public Vector2D<Integer> generateRandomPosition() {

        Vector2D<Integer> position;
        do {
            int x = (int) (Math.random() * bound.x);
            int y = (int) (Math.random() * bound.y);
            position = new Vector2D<>(x, y);
        } while (grids[position.y][position.x] != 0);

        return position;
    }

    public void updateFood(long cycles) {
        if (getGridState(food) != GridState.Food) {
            food = generateRandomPosition();
            setGrid(food, 2);
        }
        if (cycles % 5 == 0) {
            bigFood = generateRandomPosition();
            setGrid(bigFood, 3);
        } else if (bigFood != null && getGridState(bigFood) == GridState.BigFood && cycles % 5 > 2) {
            setGrid(bigFood, 0);
            bigFood = null;
        }
    }

    public void reset() {
        for (int r = 0; r < bound.y; r++) {
            for (int c = 0; c < bound.x; c++) {
                int val = grids[r][c];
                if (val != GridState.EMPTY.digit() && val != GridState.OBSTACLE.digit()) {
                    grids[r][c] = GridState.EMPTY.digit();
                }
            }
        }
        food = generateRandomPosition();
    }
}

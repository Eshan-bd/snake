package org.minigame;

import org.minigame.utils.Vector2D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameMap {

    private static final Path DEFAULT_MAP = Path.of("maps", "map_00.txt");

    private final Vector2D<Integer> bound;
    private int[][] grids;

    public GameMap(Vector2D<Integer> bound) {
        this.bound = bound;

        initialiseMap();
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

    public int getGrid(Vector2D<Integer> head) {
        return grids[head.y][head.x];
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
}

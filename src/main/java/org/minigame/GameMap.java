package org.minigame;

import org.minigame.utils.Vector2D;

public class GameMap {

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

        for (int row=0; row<rows; row++) {
            for (int col=0; col<columns; col++) {

                grids[row][col] = 0;
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

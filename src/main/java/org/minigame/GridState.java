package org.minigame;

public enum GridState {
    EMPTY (0),
    OBSTACLE (1),
    SNAKE (-1),
    Food (2),
    BigFood (3);

    private final int digit;

    GridState(int digit) {
        this.digit = digit;
    }

    public int digit() {
        return digit;
    }

}

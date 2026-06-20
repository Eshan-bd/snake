package org.minigame;

public enum Food {
    REGULAR(2),
    SUPER(3);

    private final int score;

    Food(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}

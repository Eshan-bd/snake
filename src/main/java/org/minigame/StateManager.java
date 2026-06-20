package org.minigame;

public class StateManager {

    private final GameState state;

    private StateManager () {
        this.state = GameState.RUNNING;
    }

    public GameState getState() {
        return state;
    }
}

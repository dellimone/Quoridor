package it.units.quoridor.engine;

import it.units.quoridor.domain.GameState;

public class GameEngine {

    private final ActionValidator validator;
    private GameState state;


    public GameEngine(GameState initialState, ActionValidator validator) {
        this.state = initialState;
        this.validator = validator;
    }

    public GameState getGameState() {
        return state;
    }
}

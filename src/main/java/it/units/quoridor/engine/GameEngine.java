package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

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

    public MoveResult movePawn(PlayerId player, Direction direction) {
        boolean valid = validator.canMovePawn(state, player, direction);
        return valid ? MoveResult.OK : MoveResult.INVALID; // if valid, move OK else INVALID
    }
}

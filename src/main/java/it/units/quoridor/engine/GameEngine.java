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

        if (!valid) {
            return MoveResult.INVALID;
        }

        // need to change state if move was valid
        state = state.withNextTurn();
        return MoveResult.OK;
    }
}

package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

public class GameEngine {

    private final ActionValidator validator;
    private GameState state;
    private boolean gameOver = false;
    private PlayerId winner = null;


    public GameEngine(GameState initialState, ActionValidator validator) {
        this.state = initialState;
        this.validator = validator;
    }

    public GameState getGameState() {
        return state;
    }
    
    
    public void endGame(PlayerId winner) {
        this.gameOver = true;
        this.winner = winner;
    }


    public boolean isGameOver() {
        return gameOver;
    }


    public PlayerId getWinner() {
        return winner;
    }


    public MoveResult movePawn(PlayerId player, Direction direction) {

        // if the game has ended, every move is marked invalid
        if (gameOver) {
            return MoveResult.INVALID;
        }

        // if the "not current"-player tries to make a move, we mark it directly as invalid
        if (!player.equals(state.currentPlayerId())) {
            return MoveResult.INVALID;
        }

        boolean valid = validator.canMovePawn(state, player, direction);

        if (!valid) {
            return MoveResult.INVALID;
        }

        // need to change state if move was valid
        state = state.withNextTurn();
        return MoveResult.OK;
    }
}

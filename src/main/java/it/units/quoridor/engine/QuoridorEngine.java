package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

public class QuoridorEngine implements GameEngine {

    private final ActionValidator validator;
    private final WinChecker winChecker;
    private GameState state;

    private boolean gameOver = false;
    private PlayerId winner = null;


    public QuoridorEngine(GameState initialState, ActionValidator validator, WinChecker winChecker) {
        this.state = initialState;
        this.validator = validator;
        this.winChecker = winChecker;
    }

    @Override
    public GameState getGameState() {
        return state;
    }
    
    
    public void endGame(PlayerId winner) {
        this.gameOver = true;
        this.winner = winner;
    }


    @Override
    public boolean isGameOver() {
        return gameOver;
    }


    @Override
    public PlayerId getWinner() {
        return winner;
    }


    @Override
    public MoveResult movePawn(PlayerId player, Direction direction) {

        // if the game has ended, every move is marked invalid
        if (gameOver) {
            return MoveResult.INVALID;
        }

        // if the "not current"-player tries to make a move, we mark it directly as invalid
        if (!player.equals(state.currentPlayerId())) {
            return MoveResult.INVALID;
        }

        // check move validity
        boolean valid = validator.canMovePawn(state, player, direction);

        if (!valid) {
            return MoveResult.INVALID;
        }

        // need to change state if move was valid
        state = state.withNextTurn();

        // check if the next state is a win
        if (winChecker.isWin(state, player)) {
            gameOver = true;
            winner = player;
            return MoveResult.WIN;
        }

        return MoveResult.OK;
    }
}

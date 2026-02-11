package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

public class QuoridorEngine implements GameEngine {

    private final PawnMoveValidator pawnValidator;
    private final WallPlacementValidator wallValidator;
    private final WinChecker winChecker;
    private GameState state;

    private boolean gameOver = false;
    private PlayerId winner = null;


    public QuoridorEngine(GameState initialState, PawnMoveValidator pawnValidator, WallPlacementValidator wallValidator, WinChecker winChecker) {
        this.state = initialState;
        this.pawnValidator = pawnValidator;
        this.wallValidator = wallValidator;
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
        boolean valid = pawnValidator.canMovePawn(state, player, direction);

        if (!valid) {
            return MoveResult.INVALID;
        }

        // we need to update the board with the new position
        Position nextPosition = state.board().playerPosition(player).move(direction);
        Board newBoard = state.board().withPlayerAt(player, nextPosition);

        // need to change state if move was valid
        state = new GameState(newBoard, state.players(), state.currentPlayerIndex()).withNextTurn();

        // check if the next state is a win
        if (winChecker.isWin(state, player)) {
            gameOver = true;
            winner = player;
            return MoveResult.WIN;
        }

        return MoveResult.OK;
    }

    @Override
    public MoveResult placeWall(PlayerId player, Wall wall) {

        if (gameOver) {
            return MoveResult.INVALID;
        }

        if (player != state.currentPlayerId()) {
            return MoveResult.INVALID;
        }

        // calls the wall validator to check
        boolean valid = wallValidator.canPlaceWall(state, player, wall);

        if (!valid) {
            return MoveResult.INVALID;
        }

        Board newBoard = state.board().addWall(wall);
        state.currentPlayer().useWall(); // consume a wall from current player

        state = new GameState(newBoard, state.players(), state.currentPlayerIndex()).withNextTurn();

        return MoveResult.OK;
    }
}

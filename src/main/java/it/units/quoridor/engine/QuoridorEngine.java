package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

import java.util.ArrayDeque;
import java.util.Deque;


public class QuoridorEngine implements GameEngine {

    // immutable and private snapshot (for the undo)
    private record EngineSnapshot(
            GameState state,
            boolean gameOver,
            PlayerId winner
    ) {}

    private final PawnMoveValidator pawnValidator;
    private final WallPlacementValidator wallValidator;
    private final WinChecker winChecker;

    private final Deque<EngineSnapshot> history = new ArrayDeque<>(); //snapshot history
    private final GameState initialState;
    private GameState state;

    private boolean gameOver = false;
    private PlayerId winner = null;


    public QuoridorEngine(GameState initialState, PawnMoveValidator pawnValidator, WallPlacementValidator wallValidator, WinChecker winChecker) {
        this.initialState = initialState;
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
    public void reset(){
        this.state = initialState;
        this.gameOver = false;
        this.winner = null;
        this.history.clear();
    }


    private void saveSnapshot() {
        history.push(new EngineSnapshot(state, gameOver, winner));
    }

    private void restoreStateFromSnapshot(EngineSnapshot snapshot) {
        this.state = snapshot.state;
        this.gameOver = snapshot.gameOver;
        this.winner = snapshot.winner;
    }

    @Override
    public boolean undo() {

        if (!history.isEmpty()) {
            EngineSnapshot lastSnapshot = history.pop(); // obtain the last snapshot
            restoreStateFromSnapshot(lastSnapshot);

            return true;
        }

        return false;
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

        // we save a snapshot in the history before moving on
        saveSnapshot();

        // we need to update the board with the new position
        Position nextPosition = state.board().playerPosition(player).move(direction);
        Board newBoard = state.board().withPlayerAt(player, nextPosition);

        // need to change state if move was valid
        state = state.withBoard(newBoard)
                .withNextTurn();


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

        // if a player has no walls remaining, the move is invalid
        if (state.currentPlayer().wallsRemaining() == 0) {
            return MoveResult.INVALID;
        }

        // calls the wall validator to check
        boolean valid = wallValidator.canPlaceWall(state, player, wall);

        if (!valid) {
            return MoveResult.INVALID;
        }

        // we save a snapshot in the history before moving on
        saveSnapshot();

        Player updatedPlayer = state.currentPlayer().useWall(); // update player after wall used
        Board newBoard = state.board().addWall(wall);

        state = state.withBoard(newBoard)
                .withUpdatedPlayer(updatedPlayer)
                .withNextTurn();

        return MoveResult.OK;
    }
}

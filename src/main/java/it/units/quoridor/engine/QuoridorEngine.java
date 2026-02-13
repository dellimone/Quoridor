package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.rules.setup.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class QuoridorEngine implements GameEngine {

    private final PawnMoveValidator pawnValidator;
    private final WallPlacementValidator wallValidator;
    private final WinChecker winChecker;

    private final Deque<GameState> history = new ArrayDeque<>();
    private final GameState initialState;
    private GameState state;

    // we make this package private (for the tests already implemented, for now)
    QuoridorEngine(
            GameState initialState,
            PawnMoveValidator pawnValidator,
            WallPlacementValidator wallValidator,
            WinChecker winChecker) {
        this.initialState = initialState;
        this.state = initialState;
        this.pawnValidator = pawnValidator;
        this.wallValidator = wallValidator;
        this.winChecker = winChecker;
    }

    // we will leverage a "factory" that will provide our engine with the needed setup
    public static QuoridorEngine newGame(
            GameRules rules,
            PlayerCount playerCount,
            List<PlayerSpec> specification,

            PawnMoveValidator pawnValidator,
            WallPlacementValidator wallValidator,
            WinChecker winChecker
    ) {
        GameState initialState = InitialStateFactory.create(rules, playerCount, specification);

        return new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);
    }


    @Override
    public GameState getGameState() {
        return state;
    }


    @Override
    public boolean isGameOver() {
        return state.isGameOver();
    }


    @Override
    public PlayerId getWinner() {
        return state.winner();
    }


    @Override
    public void reset(){
        this.state = initialState;
        this.history.clear();
    }


    private void saveSnapshot() {
        history.push(state);
    }

    @Override
    public boolean undo() {
        if (!history.isEmpty()) {
            state = history.pop();
            return true;
        }
        return false;
    }


    @Override
    public MoveResult movePawn(PlayerId playerId, Direction direction) {

        // if the game has ended, every move is marked invalid
        if (state.isGameOver()) {
            return MoveResult.failure("Game is over");
        }

        // if the "not current"-player tries to make a move, we mark it directly as invalid
        if (!playerId.equals(state.currentPlayerId())) {
            return MoveResult.failure("Not your turn");
        }

        // check move validity
        boolean isValidMove = pawnValidator.canMovePawn(state, playerId, direction);

        if (!isValidMove) {
            return MoveResult.failure("Invalid pawn move");
        }

        // we save a snapshot in the history before moving on
        saveSnapshot();

        // we need to update the board with the new position
        Position nextPosition = state.board().playerPosition(playerId).move(direction);
        Board newBoard = state.board().withPlayerAt(playerId, nextPosition);

        // need to change state if move was valid
        state = state.withBoard(newBoard)
                .withNextTurn();


        // check if the next state is a win
        if (winChecker.isWin(state, playerId)) {
            state = state.withGameFinished(playerId);
        }

        return MoveResult.success();
    }


    @Override
    public MoveResult placeWall(PlayerId player, Wall wall) {

        if (state.isGameOver()) {
            return MoveResult.failure("Game is over");
        }

        if (player != state.currentPlayerId()) {
            return MoveResult.failure("Not your turn");
        }

        // if a player has no walls remaining, the move is invalid
        if (state.currentPlayer().wallsRemaining() == 0) {
            return MoveResult.failure("No walls remaining");
        }

        // calls the wall validator to check
        boolean valid = wallValidator.canPlaceWall(state, player, wall);

        if (!valid) {
            return MoveResult.failure("Impossible to place wall here");
        }

        // we save a snapshot in the history before moving on
        saveSnapshot();

        Player updatedPlayer = state.currentPlayer().useWall(); // update player after wall used
        Board newBoard = state.board().addWall(wall);

        state = state.withBoard(newBoard)
                .withUpdatedPlayer(updatedPlayer)
                .withNextTurn();

        return MoveResult.success();
    }
}

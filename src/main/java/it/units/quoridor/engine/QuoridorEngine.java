package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.rules.setup.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class QuoridorEngine implements GameEngine {

    private GameRules rules;  // Temporarily non-final until old constructor is removed
    private final PawnMoveValidator pawnValidator;
    private final WallPlacementValidator wallValidator;
    private final WinChecker winChecker;

    private final Deque<GameState> history = new ArrayDeque<>();
    private GameState initialState;  // Temporarily non-final until old constructor is removed
    private GameState state;

    // New constructor - takes GameRules instead of initial state
    public QuoridorEngine(
            GameRules rules,
            PawnMoveValidator pawnValidator,
            WallPlacementValidator wallValidator,
            WinChecker winChecker) {
        this.rules = rules;
        this.pawnValidator = pawnValidator;
        this.wallValidator = wallValidator;
        this.winChecker = winChecker;
        this.initialState = null; // Will be removed in future refactoring
    }

    public void newGame() {
        // Create fixed player specs for 2-player game
        List<PlayerSpec> specs = List.of(
            new PlayerSpec(PlayerId.PLAYER_1, "Player 1"),
            new PlayerSpec(PlayerId.PLAYER_2, "Player 2")
        );

        // Derive PlayerCount from specs size
        PlayerCount playerCount = PlayerCount.TWO_PLAYERS;

        // Create initial state using factory
        this.state = InitialStateFactory.create(rules, playerCount, specs);

        // Clear history for new game
        this.history.clear();
    }

    // OLD constructor - will be removed after tests are updated
    QuoridorEngine(
            GameState initialState,
            PawnMoveValidator pawnValidator,
            WallPlacementValidator wallValidator,
            WinChecker winChecker) {
        this.rules = null;  // Not used in old constructor
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
        // TODO: for resetting the state we should use newGame()
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

    // TODO: movePawn and PlaceWall share the same check at the beginning, not DRY
    @Override
    public MoveResult movePawn(PlayerId playerId, Direction direction) {

        // if game is ended the move is invalid
        if (state.isGameOver()) {
            return MoveResult.failure("Game is over");
        }

        // if not player turn the move is invalid
        if (!playerId.equals(state.currentPlayerId())) {
            return MoveResult.failure("Not your turn");
        }

        // check player movement validity
        boolean isValidMove = pawnValidator.canMovePawn(state, playerId, direction);

        if (!isValidMove) {
            return MoveResult.failure("Invalid pawn move");
        }

        // we save a snapshot in the history before moving on
        saveSnapshot();

        // we need to update the board with the new position
        // TODO: feature envy
        Position nextPosition = state.board().playerPosition(playerId).move(direction);
        // TODO: feature envy
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
        // TODO: feature envy
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
        // TODO: feature envy
        Player updatedPlayer = state.currentPlayer().useWall(); // update player after wall used
        Board newBoard = state.board().addWall(wall);

        state = state.withBoard(newBoard)
                .withUpdatedPlayer(updatedPlayer)
                .withNextTurn();

        return MoveResult.success();
    }
}

package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.rules.setup.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class QuoridorEngine implements GameEngine {

    private final GameRules rules;
    private final PawnMoveValidator pawnValidator;
    private final WallPlacementValidator wallValidator;
    private final WinChecker winChecker;

    private final Deque<GameState> history = new ArrayDeque<>();
    private GameState state;

    public QuoridorEngine(
            GameRules rules,
            PawnMoveValidator pawnValidator,
            WallPlacementValidator wallValidator,
            WinChecker winChecker) {
        this.rules = rules;
        this.pawnValidator = pawnValidator;
        this.wallValidator = wallValidator;
        this.winChecker = winChecker;
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
        newGame();
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

    private MoveResult validateTurnPreconditions(PlayerId playerId) {
        if (state.isGameOver()) {
            return MoveResult.failure("Game is over");
        }

        if (!playerId.equals(state.currentPlayerId())) {
            return MoveResult.failure("Not your turn");
        }

        return null; // Preconditions valid
    }

    @Override
    public MoveResult movePawn(PlayerId playerId, Direction direction) {
        MoveResult preconditionCheck = validateTurnPreconditions(playerId);
        if (preconditionCheck != null) {
            return preconditionCheck;
        }

        // check player movement validity
        boolean isValidMove = pawnValidator.canMovePawn(state, playerId, direction);

        if (!isValidMove) {
            return MoveResult.failure("Invalid pawn move");
        }

        saveSnapshot();
        state = state.withPawnMoved(playerId, direction);

        if (winChecker.isWin(state, playerId)) {
            state = state.withGameFinished(playerId);
        }

        return MoveResult.success();
    }


    @Override
    public MoveResult placeWall(PlayerId player, Wall wall) {
        MoveResult preconditionCheck = validateTurnPreconditions(player);
        if (preconditionCheck != null) {
            return preconditionCheck;
        }

        if (state.currentPlayer().wallsRemaining() == 0) {
            return MoveResult.failure("No walls remaining");
        }

        if (!wallValidator.canPlaceWall(state, player, wall)) {
            return MoveResult.failure("Impossible to place wall here");
        }

        saveSnapshot();
        state = state.withWallPlaced(player, wall);

        return MoveResult.success();
    }
}

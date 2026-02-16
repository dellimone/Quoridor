package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.rules.setup.*;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import it.units.quoridor.engine.moves.PawnMoveGenerator;

import java.util.*;


public class QuoridorEngine implements GameEngine {

    private final GameRules rules;
    private final PawnMoveValidator pawnValidator;
    private final WallPlacementValidator wallValidator;
    private final WinChecker winChecker;
    private final PawnMoveGenerator pawnMoveGenerator;

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

        this.pawnMoveGenerator = new PawnMoveGenerator(pawnValidator);

        newGame();  // Initialize engine to ready state
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

    // for tests -> package-private:
    QuoridorEngine(
            GameRules rules,
            GameState initialState,
            PawnMoveValidator pawnValidator,
            WallPlacementValidator wallValidator,
            WinChecker winChecker
    ) {
        this.rules = rules;
        this.pawnValidator = pawnValidator;
        this.wallValidator = wallValidator;
        this.winChecker = winChecker;
        this.pawnMoveGenerator = new PawnMoveGenerator(pawnValidator);

        this.state = initialState;
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

    // for the controller for the highlights
    @Override
    public Set<Position> legalPawnDestinationsForPlayer(PlayerId player) {
        if (state.isGameOver()) return Set.of();
        return pawnMoveGenerator.legalDestinations(state, player);
    }

    @Override
    public MoveResult movePawn(PlayerId playerId, Position target) {
        MoveResult pre = validateTurnPreconditions(playerId);
        if (pre != null) return pre;

        if (!pawnMoveGenerator.isLegalDestination(state, playerId, target)) {
            return MoveResult.failure("Invalid pawn move");
        }

        saveSnapshot();
        state = state.withPawnMovedTo(playerId, target);

        if (winChecker.isWin(state, playerId)) {
            state = state.withGameFinished(playerId);
            return MoveResult.win();
        }

        return MoveResult.success();
    }


    public MoveResult movePawn(PlayerId playerId, Direction direction) {
        MoveResult pre = validateTurnPreconditions(playerId);
        if (pre != null) return pre;

        Optional<Position> dest = pawnMoveGenerator.resolveDestination(state, playerId, direction);
        if (dest.isEmpty()) return MoveResult.failure("Invalid pawn move");

        // Apply directly, do NOT call core (avoids revalidation)
        saveSnapshot();
        state = state.withPawnMovedTo(playerId, dest.get());

        if (winChecker.isWin(state, playerId)) {
            state = state.withGameFinished(playerId);
            return MoveResult.win();
        }
        return MoveResult.success();
    }


    @Override
    public MoveResult placeWall(PlayerId player, Wall wall) {
        MoveResult preconditionCheck = validateTurnPreconditions(player);
        if (preconditionCheck != null) {
            return preconditionCheck;
        }

        if (state.currentPlayerWallsRemaining() == 0) {
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

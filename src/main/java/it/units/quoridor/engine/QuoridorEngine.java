package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.rules.setup.*;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import it.units.quoridor.engine.moves.PawnMoveGenerator;

import java.util.*;


/** Standard Quoridor engine for 2-player games. Delegates all validation to the Logic layer. */
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

        pawnMoveGenerator = new PawnMoveGenerator(pawnValidator);

        newGame();  // Initialize engine to ready state
    }

    public void newGame() {
        List<PlayerSpec> specs = List.of(
            new PlayerSpec(PlayerId.PLAYER_1, "Player 1"),
            new PlayerSpec(PlayerId.PLAYER_2, "Player 2")
        );

        PlayerCount playerCount = PlayerCount.TWO_PLAYERS;
        state = InitialStateFactory.create(rules, playerCount, specs);
        history.clear();
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
        pawnMoveGenerator = new PawnMoveGenerator(pawnValidator);

        state = initialState;
        history.clear();
    }



    @Override
    public GameState gameState() {
        return state;
    }


    @Override
    public boolean isGameOver() {
        return state.isGameOver();
    }


    @Override
    public PlayerId winner() {
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

    private Optional<MoveResult> validateTurnPreconditions(PlayerId playerId) {
        if (state.isGameOver()) {
            return Optional.of(MoveResult.failure("Game is over"));
        }

        if (!playerId.equals(state.currentPlayerId())) {
            return Optional.of(MoveResult.failure("Not your turn"));
        }

        return Optional.empty();
    }

    @Override
    public Set<Position> legalPawnDestinationsForPlayer(PlayerId player) {
        if (state.isGameOver()) return Set.of();
        return pawnMoveGenerator.legalDestinations(state, player);
    }

    @Override
    public MoveResult movePawn(PlayerId playerId, Position target) {
        Optional<MoveResult> pre = validateTurnPreconditions(playerId);
        if (pre.isPresent()) return pre.get();

        if (!pawnMoveGenerator.isLegalDestination(state, playerId, target)) {
            return MoveResult.failure("Invalid pawn move");
        }

        saveSnapshot();
        state = state.withPawnMovedTo(playerId, target);

        if (winChecker.isWin(state, playerId)) {
            state = state.withGameFinished(playerId);
            return MoveResult.win();
        }

        state = state.withNextTurn();
        return MoveResult.success();
    }



    @Override
    public MoveResult placeWall(PlayerId player, Wall wall) {
        Optional<MoveResult> preconditionCheck = validateTurnPreconditions(player);
        if (preconditionCheck.isPresent()) {
            return preconditionCheck.get();
        }

        if (state.currentPlayerWallsRemaining() == 0) {
            return MoveResult.failure("No walls remaining");
        }

        if (!wallValidator.canPlaceWall(state, player, wall)) {
            return MoveResult.failure("Impossible to place wall here");
        }

        saveSnapshot();
        state = state.withWallPlaced(player, wall).withNextTurn();

        return MoveResult.success();
    }
}

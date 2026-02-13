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
        MoveResult preconditionCheck = validateTurnPreconditions(player);
        if (preconditionCheck != null) {
            return preconditionCheck;
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

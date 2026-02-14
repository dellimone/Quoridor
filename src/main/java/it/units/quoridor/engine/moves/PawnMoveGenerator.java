package it.units.quoridor.engine.moves;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import java.util.*;

// we use a "destination" resolver for:
// - the engine, to update the GameState correctly in case of "basic" pawn movements but also jumps
// - the UI, so that we could highlight in the future "legal" pawn movements

public class PawnMoveGenerator {

    // we ask the validator for legality of the moves
    private final PawnMoveValidator pawnValidator;

    public PawnMoveGenerator(PawnMoveValidator pawnValidator) {
        this.pawnValidator = pawnValidator;
    }

    // if the player moves in said direction, where to they end up
    public Optional<Position> resolveDestination(GameState state, PlayerId playerId, Direction direction) {

        // first: rule legality (includes jump possibility)
        if (!pawnValidator.canMovePawn(state, playerId, direction)) {
            return Optional.empty();
        } // if validator says no, STOP

        Board board = state.board();
        Position from = state.getPlayerPosition(playerId);

        // compute the adjacent square
        Optional<Position> maybeAdj = from.tryMove(direction);
        if (maybeAdj.isEmpty()) return Optional.empty(); // defensive

        Position adj = maybeAdj.get();

        // normal step - move one square forward
        if (board.occupantAt(adj).isEmpty()) {
            return Optional.of(adj);
        }

        // jump
        return adj.tryMove(direction);
    }

    public boolean isLegalDestination(GameState state, PlayerId playerId, Position target) {
        Position from = state.getPlayerPosition(playerId);

        for (Direction dir : Direction.values()) {
            Optional<Position> dest = resolveDestination(state, playerId, dir);
            if (dest.isPresent() && dest.get().equals(target)) {
                return true;
            }
        }
        return false;
    }

    // for UI
    public Set<Position> legalDestinations(GameState state, PlayerId playerId) {
        Set<Position> destinations = new HashSet<>();
        for (Direction dir : Direction.values()) {
            resolveDestination(state, playerId, dir).ifPresent(destinations::add);
        }
        return destinations;
    }


}

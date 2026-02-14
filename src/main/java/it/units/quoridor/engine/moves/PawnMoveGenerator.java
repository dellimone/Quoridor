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
        if (!pawnValidator.canMovePawn(state, playerId, direction)) {
            return Optional.empty();
        }

        Board board = state.board();
        Position from = state.getPlayerPosition(playerId);

        Optional<Position> maybeAdj = from.tryMove(direction);
        if (maybeAdj.isEmpty()) return Optional.empty();
        Position adj = maybeAdj.get();

        if (board.occupantAt(adj).isEmpty()) {
            return Optional.of(adj);
        }

        // jump destination must exist and be actually reachable (no wall behind)
        Optional<Position> maybeBehind = adj.tryMove(direction);
        if (maybeBehind.isEmpty()) return Optional.empty();
        Position behind = maybeBehind.get();

        if (board.isEdgeBlocked(adj, direction) || board.isEdgeBlocked(behind, direction.opposite())) {
            return Optional.empty();
        }
        if (board.occupantAt(behind).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(behind);
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

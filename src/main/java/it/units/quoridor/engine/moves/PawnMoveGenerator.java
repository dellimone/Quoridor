package it.units.quoridor.engine.moves;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import java.util.*;

public class PawnMoveGenerator {

    private final PawnMoveValidator pawnValidator;

    public PawnMoveGenerator(PawnMoveValidator pawnValidator) {
        this.pawnValidator = pawnValidator;
    }

    public boolean isLegalDestination(GameState state, PlayerId playerId, Position target) {
        return pawnValidator.canMovePawn(state, playerId, target);
    }

    public Set<Position> legalDestinations(GameState state, PlayerId playerId) {
        Set<Position> destinations = new HashSet<>();
        Position from = state.getPlayerPosition(playerId);

        for (Position candidate : candidatePositions(from)) {
            if (pawnValidator.canMovePawn(state, playerId, candidate)) {
                destinations.add(candidate);
            }
        }

        return destinations;
    }

    // Enumerate all positions a pawn could theoretically reach in one move:
    // - 4 adjacent cells (step)
    // - 4 cells two steps away on one axis (straight jump)
    // - 4 diagonal cells (diagonal jump)
    private Set<Position> candidatePositions(Position from) {
        Set<Position> candidates = new HashSet<>();

        for (Direction dir : Direction.values()) {
            // adjacent step
            from.tryMove(dir).ifPresent(adj -> {
                candidates.add(adj);
                // straight jump (two steps in same direction)
                adj.tryMove(dir).ifPresent(candidates::add);
            });
        }

        // diagonal candidates: one step in each pair of perpendicular directions
        for (Direction vertical : List.of(Direction.NORTH, Direction.SOUTH)) {
            for (Direction horizontal : List.of(Direction.EAST, Direction.WEST)) {
                from.tryMove(vertical)
                        .flatMap(p -> p.tryMove(horizontal))
                        .ifPresent(candidates::add);
            }
        }

        return candidates;
    }
}

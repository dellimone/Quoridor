package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;

/** Validates wall placements: overlap, intersection, and path-blocking checks. */
@FunctionalInterface
public interface WallPlacementValidator {
    /** Returns true if the wall can be legally placed in the current state. */
    boolean canPlaceWall(GameState state, PlayerId player, Wall wall);
}

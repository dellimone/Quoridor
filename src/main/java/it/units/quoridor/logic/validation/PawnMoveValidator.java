package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;

/**
 * Validates pawn moves without applying them (stateless).
 * Two overloads: by direction (for single-step checks) and by target position
 * (for the full range of steps, straight jumps, and diagonal jumps).
 */
public interface PawnMoveValidator {
    /** Can the player move one step in the given direction? */
    boolean canMovePawn(GameState state, PlayerId player, Direction direction);

    /** Can the player reach the target position in one legal move? */
    boolean canMovePawn(GameState state, PlayerId player, Position target);
}

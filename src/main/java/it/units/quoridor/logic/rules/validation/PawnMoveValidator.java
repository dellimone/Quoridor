package it.units.quoridor.logic.rules.validation;

import it.units.quoridor.domain.*;

@FunctionalInterface

// does NOT apply the move, only checks whether it is valid
public interface PawnMoveValidator {
    boolean canMovePawn(GameState state, PlayerId player, Direction direction);
}

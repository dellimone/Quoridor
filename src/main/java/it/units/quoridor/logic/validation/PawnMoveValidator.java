package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;

// does NOT apply the move, only checks whether it is valid
public interface PawnMoveValidator {
    boolean canMovePawn(GameState state, PlayerId player, Direction direction);
    boolean canMovePawn(GameState state, PlayerId player, Position target);
}

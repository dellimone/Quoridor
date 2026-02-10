package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

@FunctionalInterface

// does NOT apply the move, only checks whether it is valid
public interface ActionValidator {
    boolean canMovePawn(GameState state, PlayerId player, Direction direction);
}

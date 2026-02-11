package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

@FunctionalInterface

public interface WallPlacementValidator {
    boolean canPlaceWall(GameState state, PlayerId player, Wall wall);
}

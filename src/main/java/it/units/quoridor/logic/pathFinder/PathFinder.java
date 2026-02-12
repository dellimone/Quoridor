package it.units.quoridor.logic.pathFinder;

import it.units.quoridor.domain.Board;
import it.units.quoridor.domain.Position;

public interface PathFinder {
    boolean pathExists(Board board, Position positionA, Position positionB);
}

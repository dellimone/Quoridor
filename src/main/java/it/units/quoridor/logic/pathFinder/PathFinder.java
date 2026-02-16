package it.units.quoridor.logic.pathFinder;

import it.units.quoridor.domain.Board;
import it.units.quoridor.domain.Position;

/** Checks reachability between two positions on the board, respecting walls. */
public interface PathFinder {
    /** Returns true if a path exists from positionA to positionB without crossing walls. */
    boolean pathExists(Board board, Position positionA, Position positionB);
}

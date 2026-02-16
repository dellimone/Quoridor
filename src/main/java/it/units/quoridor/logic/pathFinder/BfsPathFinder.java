package it.units.quoridor.logic.pathFinder;

import it.units.quoridor.domain.Board;
import it.units.quoridor.domain.Direction;
import it.units.quoridor.domain.Position;

import java.util.*;

/** BFS-based reachability check. Used by wall validator to ensure no player is fully blocked. */
public class BfsPathFinder implements PathFinder {
    @Override
    public boolean pathExists(Board board, Position positionA, Position positionB) {
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();

        queue.offer(positionA);
        visited.add(positionA);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.equals(positionB)) {
                return true;
            }

            for (Direction dir : Direction.values()) {
                if (board.isEdgeBlocked(current, dir)) {
                    continue;
                }

                Optional<Position> maybeNext = current.tryMove(dir);
                if (maybeNext.isEmpty()) {
                    continue;
                }

                Position next = maybeNext.get();
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(next);
                }
            }
        }
        return false;
    }
}

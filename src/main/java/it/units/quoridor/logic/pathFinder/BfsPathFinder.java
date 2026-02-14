package it.units.quoridor.logic.pathFinder;

import it.units.quoridor.domain.Board;
import it.units.quoridor.domain.Direction;
import it.units.quoridor.domain.Position;

import java.util.*;

public class BfsPathFinder implements PathFinder {
    @Override
    public boolean pathExists(Board board, Position positionA, Position positionB) {
        // Queue for BFS traversal
        Queue<Position> queue = new LinkedList<>();

        // Track visited positions to avoid cycles
        Set<Position> visited = new HashSet<>();

        // Start from the initial position
        queue.offer(positionA);
        visited.add(positionA);

        // BFS loop
        while (!queue.isEmpty()) {
            Position current = queue.poll();

            // Check if we've reached the goal
            if (current.equals(positionB)) {
                return true;
            }

            // Explore all four directions
            for (Direction dir : Direction.values()) {

                // If wall blocks movement, skip
                if (board.isEdgeBlocked(current, dir)) {
                    continue;
                }

                // instead of throwing an exception, we use the implemented tryMove()
                Optional<Position> maybeNext = current.tryMove(dir);
                if (maybeNext.isEmpty()) {
                    continue; // off board
                }

                Position next = maybeNext.get();

                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(next);
                }
            }
        }
        // Exhausted all possibilities - no path found
        return false;
    }
}

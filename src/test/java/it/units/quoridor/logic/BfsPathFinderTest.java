package it.units.quoridor.logic;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import org.junit.jupiter.api.Test;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class BfsPathFinderTest {

    @Test
    void pathExistsOnEmptyBoardFromOnePositionToAnotherPosition() {
        // Arrange
        Board board = new Board();  // Empty board, no walls
        Position start = new Position(2,2);  // Bottom center
        Position end = new Position(6,6);

        BfsPathFinder pathFinder = new BfsPathFinder();

        // Act
        boolean exists = pathFinder.pathExists(board, start, end);

        // Assert
        assertTrue(exists);
    }

    @Test
    void pathExistsWhenStartEqualsEnd() {
        // Arrange
        Board board = new Board();
        Position start = new Position(4, 4);
        Position end = new Position(4, 4);  // Same as start

        BfsPathFinder pathFinder = new BfsPathFinder();

        // Act
        boolean exists = pathFinder.pathExists(board, start, end);

        // Assert
        assertTrue(exists);
    }

    @Test
    void pathExistsWithSingleNonBlockingWall() {
        // Arrange - Wall exists but doesn't block the path
        Board board = new Board()
                .addWall(hWall(0, 0));

        Position start = new Position(2, 2);
        Position end = new Position(6, 6);

        BfsPathFinder pathFinder = new BfsPathFinder();

        // Act
        boolean exists = pathFinder.pathExists(board, start, end);

        // Assert
        assertTrue(exists);  // Path still exists, just needs detour
    }

    @Test
    void noPathWhenCompletelyBlockedByWalls() {
        // Arrange - Create walls that completely surround the start position
        Board board = new Board()
                .addWall(hWall(3, 3))   // Blocks SOUTH
                .addWall(hWall(4, 3))   // Blocks NORTH
                .addWall(vWall(4, 3))   // Blocks WEST
                .addWall(vWall(4, 4));  // Blocks EAST

        Position start = new Position(4, 4);  // Trapped in the center
        Position end = new Position(0, 0);
        BfsPathFinder pathFinder = new BfsPathFinder();

        // Act
        boolean exists = pathFinder.pathExists(board, start, end);

        // Assert
        assertFalse(exists);
    }
}
package it.units.quoridor.logic;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import org.junit.jupiter.api.Test;

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
                .addWall(new Wall(new WallPosition(0, 0), WallOrientation.HORIZONTAL));

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
                .addWall(new Wall(new WallPosition(3, 3), WallOrientation.HORIZONTAL))  // Blocks SOUTH
                .addWall(new Wall(new WallPosition(4, 3), WallOrientation.HORIZONTAL))  // Blocks NORTH
                .addWall(new Wall(new WallPosition(4, 3), WallOrientation.VERTICAL))    // Blocks WEST
                .addWall(new Wall(new WallPosition(4, 4), WallOrientation.VERTICAL));   // Blocks EAST

        Position start = new Position(4, 4);  // Trapped in the center
        Position end = new Position(0, 0);
        BfsPathFinder pathFinder = new BfsPathFinder();

        // Act
        boolean exists = pathFinder.pathExists(board, start, end);

        // Assert
        assertFalse(exists);
    }
}
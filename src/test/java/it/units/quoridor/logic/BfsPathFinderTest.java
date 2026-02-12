package it.units.quoridor.logic;

import it.units.quoridor.domain.Board;
import it.units.quoridor.domain.Position;
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
}
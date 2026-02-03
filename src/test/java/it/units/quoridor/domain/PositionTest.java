package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Position - a cell on the 9x9 Quoridor board.
 */
class PositionTest {

    // ============================================================
    // Position Tests
    // ============================================================

    @Test
    void positionWithValidCoordinatesCreatesPosition() {

        Position position = new Position(4, 4);

        assertEquals(4, position.row());
        assertEquals(4, position.col());
    }

    @Test
    void positionWithNegativeRowThrowsException() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Position(-1, 4);
        });
    }

    @Test
    void positionWithRowGreaterThan8ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(9, 4);
        });
    }

    @Test
    void positionWithNegativeColThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(4, -1);
        });
    }

    @Test
    void positionWithColGreaterThan8ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(4, 9);
        });
    }

    // ============================================================
    // Movement Tests
    // ============================================================

    @Test
    void moveNorthIncreasesRow() {
        Position start = new Position(4, 4);
        Position result = start.move(Direction.NORTH);

        assertEquals(5, result.row());
        assertEquals(4, result.col());
    }

    @Test
    void moveSouthDecreasesRow() {
        Position start = new Position(4, 4);
        Position result = start.move(Direction.SOUTH);

        assertEquals(3, result.row());
        assertEquals(4, result.col());
    }

    @Test
    void moveEastIncreasesCol() {
        Position start = new Position(4, 4);
        Position result = start.move(Direction.EAST);

        assertEquals(4, result.row());
        assertEquals(5, result.col());
    }

    @Test
    void moveWestDecreasesCol() {
        Position start = new Position(4, 4);
        Position result = start.move(Direction.WEST);

        assertEquals(4, result.row());
        assertEquals(3, result.col());
    }

    // ============================================================
    // Boundary Tests - Moving Off Board
    // ============================================================

    @Test
    void moveNorthfromTopRowThrowsException() {
        Position topEdge = new Position(8, 4);

        assertThrows(IllegalArgumentException.class, () -> {
            topEdge.move(Direction.NORTH);
        });
    }

    @Test
    void moveSouthFromBottomRowThrowsException() {
        Position bottomEdge = new Position(0, 4);

        assertThrows(IllegalArgumentException.class, () -> {
            bottomEdge.move(Direction.SOUTH);
        });
    }

    @Test
    void moveEastFromRightmostColThrowsException() {
        Position rightEdge = new Position(4, 8);

        assertThrows(IllegalArgumentException.class, () -> {
            rightEdge.move(Direction.EAST);
        });
    }

    @Test
    void moveWestFromLeftmostColThrowsException() {
        Position leftEdge = new Position(4, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            leftEdge.move(Direction.WEST);
        });
    }

}

package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WallPositionTest {

    @Test
    void wallPositionWithValidCoordinatesCreatesPosition() {

        WallPosition wallPosition = new WallPosition(4, 4);

        assertEquals(4, wallPosition.row());
        assertEquals(4, wallPosition.col());
    }

    @Test
    void wallPositionWithNegativeRowThrowsException() {

        assertThrows(IllegalArgumentException.class, () -> new WallPosition(-1, 4));
    }

    @Test
    void wallPositionWithRowGreaterThan7ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new WallPosition(8, 4));
    }

    @Test
    void wallPositionWithNegativeColThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new WallPosition(4, -1));
    }

    @Test
    void wallPositionWithColGreaterThan7ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new WallPosition(4, 8));
    }

}
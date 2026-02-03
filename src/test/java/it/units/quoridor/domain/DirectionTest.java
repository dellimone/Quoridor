package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void northHasCorrectDelta() {
        assertEquals(-1, Direction.NORTH.rowDelta());
        assertEquals(0, Direction.NORTH.colDelta());
    }
    @Test
    void southHasCorrectDelta() {
        assertEquals(1, Direction.SOUTH.rowDelta());
        assertEquals(0, Direction.SOUTH.colDelta());
    }

    @Test
    void eastHasCorrectDelta() {
        assertEquals(0, Direction.EAST.rowDelta());
        assertEquals(1, Direction.EAST.colDelta());
    }

    @Test
    void westHasCorrectDelta() {
        assertEquals(0, Direction.WEST.rowDelta());
        assertEquals(-1, Direction.WEST.colDelta());
    }

}
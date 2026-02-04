package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockedEdgeTest {

    @Test
    void createBlockedEdgeWithPositionAndDirection() {
        Position position = new Position(3, 4);
        BlockedEdge blockedEdge = new BlockedEdge(position, Direction.NORTH);

        assertEquals(position, blockedEdge.position());
        assertEquals(Direction.NORTH, blockedEdge.direction());
    }

    @Test
    void blockedEdgesWithSamePositionAndDirectionAreEqual() {
        Position position = new Position(3, 4);
        BlockedEdge blockedEdgeA = new BlockedEdge(position, Direction.NORTH);
        BlockedEdge blockedEdgeB = new BlockedEdge(position, Direction.NORTH);
        assertEquals(blockedEdgeA, blockedEdgeB);
    }
    @Test
    void blockedEdgesWithDifferentPositionOrDirectionAreNotEqual() {
        assertNotEquals(
                new BlockedEdge(new Position(3,4),Direction.NORTH),
                new BlockedEdge(new Position(3,5),Direction.NORTH));

        assertNotEquals(
                new BlockedEdge(new Position(3,4),Direction.NORTH),
                new BlockedEdge(new Position(3,4),Direction.SOUTH));
    }

}
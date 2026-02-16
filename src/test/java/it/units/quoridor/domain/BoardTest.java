package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void createEmptyBoard() {
        Board emptyBoard = new Board();

        assertTrue(emptyBoard.walls().isEmpty());
        assertTrue(emptyBoard.playerPositions().isEmpty());
    }

    @Test
    void  addWallReturnsNewBoardWithWall() {
        Board board = new Board();
        Wall wall = hWall(1, 2);

        Board boardWithWall = board.addWall(wall);

        assertTrue(board.walls().isEmpty());
        assertTrue(boardWithWall.walls().contains(wall));
    }

    @Test
    void getWallsReturnsBoardWalls() {
        Board board = new Board();
        Wall wall1 = hWall(2, 2);
        Wall wall2 = vWall(4, 4);

        Board boardWithWalls = board.addWall(wall1).addWall(wall2);

        assertEquals(2, boardWithWalls.walls().size());
        assertTrue(boardWithWalls.walls().contains(wall1));
        assertTrue(boardWithWalls.walls().contains(wall2));
        assertTrue(board.walls().isEmpty()); // Original unchanged
    }

    @Test
    void withPlayerAtReturnsNewBoardWithPlayerPosition() {
        Board board = new Board();
        Position position = new Position(4, 4);

        Board boardWithPlayer = board.withPlayerAt(PlayerId.PLAYER_1, position);

        assertNull(board.playerPosition(PlayerId.PLAYER_1)); // Original has no players
        assertEquals(position, boardWithPlayer.playerPosition(PlayerId.PLAYER_1));
    }

    @Test
    void movingPlayerUpdatesPositionOnNewBoard() {
        Board emptyBoard = new Board();
        Position startPosition = new Position(4, 4);
        Position endPosition = new Position(5, 4);

        Board boardWithPlayer = emptyBoard.withPlayerAt(PlayerId.PLAYER_1, startPosition);
        Board boardAfterMove = boardWithPlayer.withPlayerAt(PlayerId.PLAYER_1, endPosition);

        assertEquals(startPosition, boardWithPlayer.playerPosition(PlayerId.PLAYER_1)); // Original position unchanged
        assertEquals(endPosition, boardAfterMove.playerPosition(PlayerId.PLAYER_1));     // New position updated
    }

    @Test
    void getAllBlockedEdgesReturnsEdgesFromAllWalls() {
        Board board = new Board();
        Wall wall1 = hWall(2, 3);
        Wall wall2 = vWall(5, 5);

        Board boardWithWalls = board.addWall(wall1).addWall(wall2);

        Set<BlockedEdge> blockedEdges = boardWithWalls.getAllBlockedEdges();

        // Horizontal wall blocks 4 edges, vertical wall blocks 4 edges
        assertEquals(8, blockedEdges.size());
        assertTrue(blockedEdges.containsAll(wall1.getBlockedEdges()));
        assertTrue(blockedEdges.containsAll(wall2.getBlockedEdges()));
    }

    @Test
    void isEdgeBlockedReturnsTrueForBlockedEdge() {
        Board board = new Board();
        Wall wall = hWall(3, 4);
        Board boardWithWall = board.addWall(wall);

        // Horizontal wall at (3,4) blocks position (3,4) moving NORTH
        assertTrue(boardWithWall.isEdgeBlocked(new Position(3, 4), Direction.NORTH));
        // And also blocks position (4,4) moving SOUTH
        assertTrue(boardWithWall.isEdgeBlocked(new Position(4, 4), Direction.SOUTH));
    }

    @Test
    void isEdgeBlockedReturnsFalseForUnblockedEdge() {
        Board board = new Board();
        Wall wall = hWall(3, 4);
        Board boardWithWall = board.addWall(wall);

        // This edge is not blocked by the horizontal wall
        assertFalse(boardWithWall.isEdgeBlocked(new Position(3, 4), Direction.EAST));
        // Empty board has no blocked edges
        assertFalse(board.isEdgeBlocked(new Position(3, 4), Direction.NORTH));
    }
}
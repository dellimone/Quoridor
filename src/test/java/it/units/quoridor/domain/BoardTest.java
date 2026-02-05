package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void createEmptyBoard() {
        Board board = new Board();

        assertTrue(board.walls().isEmpty());
        assertTrue(board.playerPositions().isEmpty());
    }

    @Test
    void  addWallReturnsNewBoardWithWall() {
        Board board = new Board();
        Wall wall = new Wall(
                new WallPosition(1,2),
                WallOrientation.HORIZONTAL);

        Board newBoard = board.addWall(wall);

        assertTrue(board.walls().isEmpty());
        assertTrue(newBoard.walls().contains(wall));
    }

    @Test
    void getWallsReturnsBoardWalls() {
        Board board = new Board();
        Wall wall1 = new Wall(new WallPosition(2, 2), WallOrientation.HORIZONTAL);
        Wall wall2 = new Wall(new WallPosition(4, 4), WallOrientation.VERTICAL);

        Board newBoard = board.addWall(wall1).addWall(wall2);

        assertEquals(2, newBoard.walls().size());
        assertTrue(newBoard.walls().contains(wall1));
        assertTrue(newBoard.walls().contains(wall2));
        assertTrue(board.walls().isEmpty()); // Original unchanged
    }

    @Test
    void withPlayerAtReturnsNewBoardWithPlayerPosition() {
        Board board = new Board();
        Position position = new Position(4, 4);

        Board newBoard = board.withPlayerAt(PlayerId.PLAYER_1, position);

        assertNull(board.playerPosition(PlayerId.PLAYER_1)); // Original has no players
        assertEquals(position, newBoard.playerPosition(PlayerId.PLAYER_1));
    }

    @Test
    void movingPlayerUpdatesPositionOnNewBoard() {
        Board board = new Board();
        Position startPosition = new Position(4, 4);
        Position endPosition = new Position(5, 4);

        Board boardWithPlayer = board.withPlayerAt(PlayerId.PLAYER_1, startPosition);
        Board boardAfterMove = boardWithPlayer.withPlayerAt(PlayerId.PLAYER_1, endPosition);

        assertEquals(startPosition, boardWithPlayer.playerPosition(PlayerId.PLAYER_1)); // Original position unchanged
        assertEquals(endPosition, boardAfterMove.playerPosition(PlayerId.PLAYER_1));     // New position updated
    }
}
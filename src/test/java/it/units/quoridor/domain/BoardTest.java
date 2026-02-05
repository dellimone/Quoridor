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
}
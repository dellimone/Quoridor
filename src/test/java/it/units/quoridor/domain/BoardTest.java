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
}
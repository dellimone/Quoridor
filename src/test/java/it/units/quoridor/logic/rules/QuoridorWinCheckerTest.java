package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class QuoridorWinCheckerTest {

    private final QuoridorGameRules rules = new QuoridorGameRules();
    private final WinChecker winChecker = new QuoridorWinChecker(rules);

    @Test
    void playerAtGoalRowHasWon() {
        // P1 goal is row 8 per rules; place P1 at row 8
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(8, 4));

        GameState state = new GameState(board, List.of(P1, P2));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_1));
    }

    @Test
    void playerNotAtGoalRowHasNotWon() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(5, 4));

        GameState state = new GameState(board, List.of(P1, P2));

        assertFalse(winChecker.isWin(state, PlayerId.PLAYER_1));
    }

    @Test
    void player1WinsAtRow8() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(8, 3));

        GameState state = new GameState(board, List.of(P1, P2));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_1));
    }

    @Test
    void player2WinsAtRow0() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 6));

        GameState state = new GameState(board, List.of(P1, P2));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_2));
    }

    @Test
    void player3WinsAtCol8() {
        Player p3 = new Player(PlayerId.PLAYER_3, "P3", 5);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_3, new Position(4, 8));

        GameState state = new GameState(board, List.of(p3));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_3));
    }

    @Test
    void player3NotAtGoalColumnHasNotWon() {
        Player p3 = new Player(PlayerId.PLAYER_3, "P3", 5);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_3, new Position(4, 5));

        GameState state = new GameState(board, List.of(p3));

        assertFalse(winChecker.isWin(state, PlayerId.PLAYER_3));
    }

    @Test
    void player4WinsAtCol0() {
        Player p4 = new Player(PlayerId.PLAYER_4, "P4", 5);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_4, new Position(7, 0));

        GameState state = new GameState(board, List.of(p4));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_4));
    }
}

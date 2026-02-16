package it.units.quoridor.logic;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.WinChecker;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import it.units.quoridor.logic.rules.QuoridorWinChecker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuoridorWinCheckerTest {

    private final QuoridorGameRules rules = new QuoridorGameRules();
    private final WinChecker winChecker = new QuoridorWinChecker(rules);

    @Test
    void playerAtGoalRowHasWon() {
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10);

        // P1 goal is row 8 per rules; place P1 at row 8
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(8, 4));

        GameState state = new GameState(board, List.of(player1, player2));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_1));
    }

    @Test
    void playerNotAtGoalRowHasNotWon() {
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(5, 4));

        GameState state = new GameState(board, List.of(player1, player2));

        assertFalse(winChecker.isWin(state, PlayerId.PLAYER_1));
    }

    @Test
    void player1WinsAtRow8() {
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(8, 3));

        GameState state = new GameState(board, List.of(player1, player2));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_1));
    }

    @Test
    void player2WinsAtRow0() {
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 6));

        GameState state = new GameState(board, List.of(player1, player2));

        assertTrue(winChecker.isWin(state, PlayerId.PLAYER_2));
    }
}

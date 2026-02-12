package it.units.quoridor.logic;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.WinChecker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuoridorWinCheckerTest {

    @Test
    void playerAtGoalRowHasWon() {
        // Arrange
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4)); // Player 1 at goal row 0

        GameState state = new GameState(board, List.of(player1, player2));

        WinChecker winChecker = new QuoridorWinChecker();

        // Act
        boolean hasWon = winChecker.isWin(state, PlayerId.PLAYER_1);

        // Assert
        assertTrue(hasWon);
    }
}
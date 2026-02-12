package it.units.quoridor.logic;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.PlayerCount;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {

    @Test
    void getStartPositionForPlayer1In2PlayerGame() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        Position startPosition = rules.getStartPosition(PlayerId.PLAYER_1);

        // Assert
        assertEquals(new Position(0, 4), startPosition);
    }

    @Test
    void getStartPositionForPlayer2In2PlayerGame() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        Position startPosition = rules.getStartPosition(PlayerId.PLAYER_2);

        // Assert
        assertEquals(new Position(8, 4), startPosition);
    }

    @Test
    void getStartPositionForPlayer3In4PlayerGame() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        Position startPosition = rules.getStartPosition(PlayerId.PLAYER_3);

        // Assert
        assertEquals(new Position(4, 0), startPosition);
    }

    @Test
    void getStartPositionForPlayer4In4PlayerGame() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        Position startPosition = rules.getStartPosition(PlayerId.PLAYER_4);

        // Assert
        assertEquals(new Position(4, 8), startPosition);
    }

    @Test
    void getGoalRowForPlayer1() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        int goalRow = rules.getGoalRow(PlayerId.PLAYER_1);

        // Assert
        assertEquals(8, goalRow);  // Player 1 starts at row 0, must reach row 8
    }

    @Test
    void getGoalRowForPlayer2() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        int goalRow = rules.getGoalRow(PlayerId.PLAYER_2);

        // Assert
        assertEquals(0, goalRow);  // Player 2 starts at row 8, must reach row 0
    }

    @Test
    void getInitialWallCountFor2PlayerGame() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        int wallCount = rules.getInitialWallCount(PlayerCount.TWO_PLAYERS);

        // Assert
        assertEquals(10, wallCount);  // 2-player: 10 walls each
    }

    @Test
    void getInitialWallCountFor4PlayerGame() {
        // Arrange
        GameRules rules = new QuoridorGameRules();

        // Act
        int wallCount = rules.getInitialWallCount(PlayerCount.FOUR_PLAYERS);

        // Assert
        assertEquals(5, wallCount);  // 4-player: 5 walls each
    }
}
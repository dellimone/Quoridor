package it.units.quoridor.logic;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
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
}
package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    void goalPositionsForPlayer1IsEntireRow8() {
        GameRules rules = new QuoridorGameRules();

        Set<Position> goals = rules.getGoalPositions(PlayerId.PLAYER_1);

        Set<Position> expectedRow8 = IntStream.rangeClosed(0, 8)
                .mapToObj(col -> new Position(8, col))
                .collect(Collectors.toSet());
        assertEquals(expectedRow8, goals);
    }

    @Test
    void goalPositionsForPlayer2IsEntireRow0() {
        GameRules rules = new QuoridorGameRules();

        Set<Position> goals = rules.getGoalPositions(PlayerId.PLAYER_2);

        Set<Position> expectedRow0 = IntStream.rangeClosed(0, 8)
                .mapToObj(col -> new Position(0, col))
                .collect(Collectors.toSet());
        assertEquals(expectedRow0, goals);
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
package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

import java.util.HashSet;
import java.util.Set;

/** Standard Quoridor rules: P1 starts bottom, P2 starts top, 10 walls each. */
public class QuoridorGameRules implements GameRules {

    @Override
    public Position getStartPosition(PlayerId playerId) {
        return switch (playerId) {
            case PLAYER_1 -> new Position(0, 4);
            case PLAYER_2 -> new Position(8, 4);
            case PLAYER_3 -> new Position(4, 0);
            case PLAYER_4 -> new Position(4, 8);
        };
    }

    @Override
    public Set<Position> getGoalPositions(PlayerId playerId) {
        return switch (playerId) {
            case PLAYER_1 -> buildRow(8);
            case PLAYER_2 -> buildRow(0);
            case PLAYER_3 -> buildColumn(8);
            case PLAYER_4 -> buildColumn(0);
        };
    }

    private Set<Position> buildRow(int row) {
        Set<Position> positions = new HashSet<>();
        for (int col = Position.MIN_COORDINATE; col <= Position.MAX_COORDINATE; col++) {
            positions.add(new Position(row, col));
        }
        return Set.copyOf(positions);
    }

    private Set<Position> buildColumn(int col) {
        Set<Position> positions = new HashSet<>();
        for (int row = Position.MIN_COORDINATE; row <= Position.MAX_COORDINATE; row++) {
            positions.add(new Position(row, col));
        }
        return Set.copyOf(positions);
    }

    @Override
    public int getInitialWallCount(PlayerCount playerCount) {
        return switch (playerCount) {
            case TWO_PLAYERS -> 10;
            case FOUR_PLAYERS -> 5;
        };
    }

}

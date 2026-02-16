package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

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
    public int getGoalRow(PlayerId playerId) {
        return switch (playerId) {
            case PLAYER_1 -> 8;
            case PLAYER_2 -> 0;
            case PLAYER_3, PLAYER_4 -> throw new UnsupportedOperationException(
                "4-player goals not yet implemented (requires column goals)"
            );
        };
    }

    @Override
    public int getInitialWallCount(PlayerCount playerCount) {
        return switch (playerCount) {
            case TWO_PLAYERS -> 10;
            case FOUR_PLAYERS -> 5;
        };
    }

}

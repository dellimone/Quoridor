package it.units.quoridor.logic;

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
        if  (playerId==PlayerId.PLAYER_1) {
            return 8;
        }
        if (playerId==PlayerId.PLAYER_2) {
            return 0;
        }
        throw new IllegalArgumentException("Unknown player: " + playerId);
    }

    @Override
    public int getInitialWallCount(PlayerCount playerCount) {
        if (playerCount == PlayerCount.TWO_PLAYERS){
            return 10;
        }
        if  (playerCount == PlayerCount.FOUR_PLAYERS){
            return 5;
        }
        throw  new IllegalArgumentException("Unknown PlayerCount: " + playerCount);
    }
}

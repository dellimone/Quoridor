package it.units.quoridor.logic;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

public class QuoridorGameRules implements GameRules {

    public Position getStartPosition(PlayerId playerId) {

        if (playerId==PlayerId.PLAYER_1) {
            return  new Position(0, 4);
        }
        if (playerId==PlayerId.PLAYER_2) {
            return new Position(8, 4);
        }
        if (playerId==PlayerId.PLAYER_3) {
            return new Position(4, 0);
        }
        if (playerId==PlayerId.PLAYER_4) {
            return new Position(4, 8);
        }
        throw new IllegalArgumentException("Unknown player: " + playerId);
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
}

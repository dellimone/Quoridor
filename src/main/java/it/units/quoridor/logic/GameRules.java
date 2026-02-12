package it.units.quoridor.logic;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

public interface GameRules {
    Position getStartPosition(PlayerId playerId);

    int getGoalRow(PlayerId playerId);
}

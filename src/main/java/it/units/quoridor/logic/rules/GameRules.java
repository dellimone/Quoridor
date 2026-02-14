package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

public interface GameRules {
    Position getStartPosition(PlayerId playerId);

    int getGoalRow(PlayerId playerId);

    int getInitialWallCount(PlayerCount playerCount);

    PlayerId getNextPlayer(PlayerId currentPlayer, PlayerCount playerCount);
}

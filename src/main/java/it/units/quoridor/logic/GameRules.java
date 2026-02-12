package it.units.quoridor.logic;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

public interface GameRules {
    public Position getStartPosition(PlayerId playerId);
}

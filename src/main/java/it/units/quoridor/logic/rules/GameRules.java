package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

/**
 * Configurable rules of Quoridor: start positions, goal rows, wall counts.
 * Implementations define the specific values for different game variants.
 */
public interface GameRules {
    /** Starting position for the given player. */
    Position getStartPosition(PlayerId playerId);

    /** Row a player must reach to win. */
    int getGoalRow(PlayerId playerId);

    /** Number of walls each player starts with. */
    int getInitialWallCount(PlayerCount playerCount);
}

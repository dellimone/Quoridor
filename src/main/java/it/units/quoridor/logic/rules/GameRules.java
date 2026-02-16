package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

import java.util.Set;

/**
 * Configurable rules of Quoridor: start positions, goal positions, wall counts.
 * Implementations define the specific values for different game variants.
 */
public interface GameRules {
    /** Starting position for the given player. */
    Position getStartPosition(PlayerId playerId);

    /** Set of positions a player must reach to win (a full row or column edge). */
    Set<Position> getGoalPositions(PlayerId playerId);

    /** Number of walls each player starts with. */
    int getInitialWallCount(PlayerCount playerCount);
}

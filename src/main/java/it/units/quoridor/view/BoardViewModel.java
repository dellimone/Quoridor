package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import it.units.quoridor.domain.Wall;

import java.util.Map;
import java.util.Set;

/**
 * View model representing the game board state for rendering.
 *
 * Contains only the data needed to draw the board:
 * - Where each player's pawn is located
 * - Which walls have been placed
 */
public record BoardViewModel(
        Map<PlayerId, Position> playerPositions,
        Set<Wall> walls
) {
}
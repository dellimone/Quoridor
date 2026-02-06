package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;

/**
 * View model representing a player's state for display in the UI.
 *
 * Contains only the data needed to show player info:
 * - Player identity (id and name)
 * - Resources remaining (wall count)
 * - Turn indicator (is this player's turn?)
 */
public record PlayerViewModel(
        PlayerId id,
        String name,
        int wallsRemaining,
        boolean isCurrentPlayer
) {
}
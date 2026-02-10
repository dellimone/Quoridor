package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

import java.util.List;
import java.util.Set;

/**
 * Interface for the game view (Humble Dialog pattern).
 *
 * The view is "dumb" - it only knows how to:
 * 1. Render data given to it (via ViewModels)
 * 2. Capture user input and forward to listener
 *
 * It doesn't game logic or decision-making.
 */
public interface GameView {

    // === Rendering ===

    /**
     * Render the board with current player positions and walls.
     */
    void renderBoard(BoardViewModel board);

    /**
     * Highlight cells as valid move destinations.
     */
    void highlightValidMoves(Set<Position> positions);

    /**
     * Clear all cell highlights.
     */
    void clearHighlights();

    /**
     * Update the player information panel.
     */
    void updatePlayerInfo(List<PlayerViewModel> players);

    /**
     * Set which player is the current player (visual indicator).
     */
    void setCurrentPlayer(PlayerId player);

    // === Messages ===

    /**
     * Show an informational message to the user.
     */
    void showMessage(String message);

    /**
     * Show an error message to the user.
     */
    void showError(String error);

    /**
     * Show game over dialog with winner.
     */
    void showGameOver(PlayerId winner);

    // === State ===

    /**
     * Enable or disable the undo button.
     */
    void setUndoEnabled(boolean enabled);

    // === Event Registration ===

    /**
     * Register a listener to receive user input events.
     */
    void setListener(ViewListener listener);
}
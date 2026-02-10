package it.units.quoridor.view;

import it.units.quoridor.domain.WallOrientation;

/**
 * Listener interface for user input events from the view.
 *
 * The Controller implements this interface to receive events
 * and translate them into game actions.
 *
 * The View just forwards raw input (coordinates, clicks) without
 * interpreting what they mean.
 */
public interface ViewListener {

    /**
     * Called when user starts a new game.
     *
     * @param playerCount number of players (2 or 4)
     */
    void onNewGame(int playerCount);

    /**
     * Called when user clicks on a board cell.
     *
     * Could mean:
     * - Selecting their own pawn
     * - Moving to a destination
     * - Clicking empty space
     *
     * The Controller decides what this means.
     *
     * @param row the row clicked (0-8)
     * @param col the column clicked (0-8)
     */
    void onCellClicked(int row, int col);

    /**
     * Called when user places a wall.
     *
     * @param row wall intersection row (0-7)
     * @param col wall intersection column (0-7)
     * @param orientation HORIZONTAL or VERTICAL
     */
    void onWallPlacement(int row, int col, WallOrientation orientation);

    /**
     * Called when user clicks the undo button.
     */
    void onUndo();

    /**
     * Called when user quits the game.
     */
    void onQuit();
}
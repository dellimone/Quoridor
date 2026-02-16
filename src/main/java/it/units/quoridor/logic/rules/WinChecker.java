package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.GameState;
import it.units.quoridor.domain.PlayerId;

/** Checks whether a player has won after a move. */
public interface WinChecker {
    /**
     * @param stateAfterMove the game state after the move has been applied
     * @param playerWhoMoved the player who just moved
     */
    boolean isWin(GameState stateAfterMove, PlayerId playerWhoMoved);
}

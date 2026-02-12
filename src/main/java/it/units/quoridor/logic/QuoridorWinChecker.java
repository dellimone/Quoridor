package it.units.quoridor.logic;

import it.units.quoridor.domain.GameState;
import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import it.units.quoridor.engine.WinChecker;

public class QuoridorWinChecker implements WinChecker {

    @Override
    public boolean isWin(GameState state, PlayerId playerId) {
        // Get player's current position from the board
        Position currentPosition = state.getPlayerPosition(playerId);

        // Get player's goal row from their player data
        int goalRow = state.getPlayer(playerId).goalRow();

        // Player wins when their current row equals their goal row
        return currentPosition.row() == goalRow;
    }
}

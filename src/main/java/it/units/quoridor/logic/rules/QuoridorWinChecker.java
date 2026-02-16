package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.GameState;
import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;


/** Wins when a player's pawn reaches their goal row. */
public class QuoridorWinChecker implements WinChecker {

    private final GameRules rules;

    public QuoridorWinChecker(GameRules rules) {
        this.rules = rules;
    }

    @Override
    public boolean isWin(GameState state, PlayerId playerId) {
        Position currentPosition = state.playerPosition(playerId);
        int goalRow = rules.getGoalRow(playerId);

        return currentPosition.row() == goalRow;
    }
}

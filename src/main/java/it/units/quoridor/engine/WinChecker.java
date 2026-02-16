package it.units.quoridor.engine;

import it.units.quoridor.domain.GameState;
import it.units.quoridor.domain.PlayerId;

public interface WinChecker {
    boolean isWin(GameState stateAfterMove, PlayerId playerWhoMoved);
}

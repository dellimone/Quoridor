package it.units.quoridor.engine;

import it.units.quoridor.logic.rules.GameRules;

public record MoveResult(
        boolean isValid,
        String message
) {
    // Static factory method for a standard success
    public static MoveResult success() {
        return new MoveResult(true, "Move executed");
    }

    // Static factory method for a custom failure message
    public static MoveResult failure(String reason) {
        return new MoveResult(false, reason);
    }

    public static MoveResult isWin() {
        return new MoveResult(true, "Win");
    }
}
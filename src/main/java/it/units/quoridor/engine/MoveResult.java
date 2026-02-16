package it.units.quoridor.engine;

/** Result of a move attempt — success, failure with reason, or win. */
public record MoveResult(
        boolean isValid,
        boolean isWin,
        String message
) {
    public static MoveResult success() {
        return new MoveResult(true, false, "Move executed");
    }

    public static MoveResult failure(String reason) {
        return new MoveResult(false, false, reason);
    }

    public static MoveResult win() {
        return new MoveResult(true, true, "Win");
    }
}
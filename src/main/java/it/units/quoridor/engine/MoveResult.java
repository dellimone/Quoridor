package it.units.quoridor.engine;

public record MoveResult(
        boolean isValid,
        boolean isWin,
        String message
) {
    // Static factory method for a standard success
    public static MoveResult success() {
        return new MoveResult(true,false, "Move executed");
    }

    // Static factory method for a custom failure message
    public static MoveResult failure(String reason) {
        return new MoveResult(false, false, reason);
    }

    public static MoveResult win() {
        return new MoveResult(true, true, "Win");
    }
}
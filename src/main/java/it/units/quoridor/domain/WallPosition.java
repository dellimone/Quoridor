package it.units.quoridor.domain;

public record WallPosition(int row, int col) {
    public static final int MIN_COORDINATE = 0;
    public static final int MAX_COORDINATE = 7;

    public WallPosition {
        if (row < MIN_COORDINATE || row > MAX_COORDINATE) {
            throw new IllegalArgumentException("row must be between 0 and 7");
        }
        if (col < MIN_COORDINATE || col > MAX_COORDINATE) {
            throw new IllegalArgumentException("col must be between 0 and 7");
        }
    }


}

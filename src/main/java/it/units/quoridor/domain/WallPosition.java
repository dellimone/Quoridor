package it.units.quoridor.domain;

/** Position on the 8x8 wall intersection grid (0-7, 0-7). Each intersection anchors a 2-cell wall. */
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

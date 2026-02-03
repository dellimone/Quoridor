package it.units.quoridor.domain;

/**
 * Represent a valid position on the 9x9 Quoridor board.
 *
 * Uses Cartesian coordinate system:
 * - Origin (0,0) at bottom-left corner
 * - row 0 = bottom, row 8 = top
 * - col 0 = left, col 8 = right
 */

public record Position(int row, int col) {

    public static final int MIN_COORDINATE = 0;
    public static final int MAX_COORDINATE = 8;

    public Position {
        if (row < MIN_COORDINATE || row > MAX_COORDINATE) {
            throw new IllegalArgumentException("row must be between 0 and 8");
        }
        if (col < MIN_COORDINATE || col > MAX_COORDINATE) {
            throw new IllegalArgumentException("col must be between 0 and 8");
        }
    }

    public Position move(Direction direction) {
        return new Position(row + direction.rowDelta(), col + direction.colDelta());
    }
}

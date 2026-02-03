package it.units.quoridor.domain;

// Cartesian coordinate system: origin at bottom-left
// row 0 = BOTTOM, row 8 = TOP
// Moving NORTH (up) increases row
//
//         col0   col1   ...        col8
//         ┌─────┬─────┬─────┬─────┬─────┐
// row8    │     │     │     │     │     │  ← TOP
//         ├─────┼─────┼─────┼─────┼─────┤
//         │ ... │ ... │ ... │ ... │ ... │
//         ├─────┼─────┼─────┼─────┼─────┤
// row2    │     │     │     │     │     │
//         ├─────┼─────┼─────┼─────┼─────┤
// row1    │     │     │     │     │     │
//         ├─────┼─────┼─────┼─────┼─────┤
// row0    │     │     │     │     │     │  ← BOTTOM
//         └─────┴─────┴─────┴─────┴─────┘


public enum Direction {
    NORTH(1, 0),   // Moving up increases row
    SOUTH(-1, 0),  // Moving down decreases row
    EAST(0, 1),    // Moving right increases col
    WEST(0, -1);   // Moving left decreases col

    private final int rowDelta;
    private final int colDelta;
    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    public int rowDelta() {
        return rowDelta;
    }
    public int colDelta() {
        return colDelta;
    }
}

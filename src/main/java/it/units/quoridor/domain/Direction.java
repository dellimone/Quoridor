package it.units.quoridor.domain;

//         col0   col1   ...        col8
//         ┌─────┬─────┬─────┬─────┬─────┐
// row0    │     │     │     │     │     │
//         ├─────┼─────┼─────┼─────┼─────┤
// row1    │     │     │     │     │     │
//         ├─────┼─────┼─────┼─────┼─────┤
// row2    │     │     │     │     │     │
//         ├─────┼─────┼─────┼─────┼─────┤
//         │ ... │ ... │ ... │ ... │ ... │
//         ├─────┼─────┼─────┼─────┼─────┤
// row8    │     │     │     │     │     │
//         └─────┴─────┴─────┴─────┴─────┘


public enum Direction {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1);

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

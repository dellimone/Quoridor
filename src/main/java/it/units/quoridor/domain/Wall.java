package it.units.quoridor.domain;

import java.util.HashSet;
import java.util.Set;

public record Wall(WallPosition position, WallOrientation orientation) {

    public Set<BlockedEdge> getBlockedEdges() {

        int row = position.row();
        int col = position.col();

        Set<BlockedEdge> blockedEdges = new HashSet<>();

        //    * : WallPosition intersection
        //    ┌─────────────┬─────────────┐
        //    │  row+1,col  │ row+1,col+1 │
        //    ├─────────────*─────────────┤
        //    │   row,col   │  row,col+1  │
        //    └─────────────┴─────────────┘
        if (orientation == WallOrientation.HORIZONTAL) {
            blockedEdges.add(new BlockedEdge(new Position(row, col), Direction.NORTH));
            blockedEdges.add(new BlockedEdge(new Position(row, col + 1), Direction.NORTH));
            blockedEdges.add(new BlockedEdge(new Position(row + 1, col), Direction.SOUTH));
            blockedEdges.add(new BlockedEdge(new Position(row + 1, col + 1), Direction.SOUTH));
        }

        if (orientation == WallOrientation.VERTICAL) {
            blockedEdges.add(new BlockedEdge(new Position(row, col), Direction.EAST));
            blockedEdges.add(new BlockedEdge(new Position(row + 1, col), Direction.EAST));
            blockedEdges.add(new BlockedEdge(new Position(row, col + 1), Direction.WEST));
            blockedEdges.add(new BlockedEdge(new Position(row + 1, col + 1), Direction.WEST));
        }
        return blockedEdges;
    }


}

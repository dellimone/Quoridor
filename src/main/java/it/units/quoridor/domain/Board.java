package it.units.quoridor.domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record Board(
        Set<Wall> walls,
        Map<PlayerId, Position> playerPositions
) {
    public Board() {
        this(Set.of(), Map.of());
    }

    public Board addWall(Wall wall) {
        Set<Wall> newWalls = new HashSet<>(walls);
        newWalls.add(wall);
        return new Board(newWalls, playerPositions);
    }
}

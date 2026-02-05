package it.units.quoridor.domain;

import java.util.Map;
import java.util.Set;

public record Board(
        Set<Wall> walls,
        Map<PlayerId, Position> playerPositions
) {
    public Board() {
        this(Set.of(), Map.of());
    }
}

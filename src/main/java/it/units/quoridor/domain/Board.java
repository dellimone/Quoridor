package it.units.quoridor.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

/** Immutable game board: 9x9 cell grid tracking player positions and placed walls. */
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

    public Board withPlayerAt(PlayerId playerId, Position position) {
        Map<PlayerId, Position> newPlayerPositions = new HashMap<>(playerPositions);
        newPlayerPositions.put(playerId, position);

        return new Board(walls, newPlayerPositions);
    }

    public Optional<PlayerId> occupantAt(Position position) {
        return playerPositions().entrySet().stream()
                .filter(e -> e.getValue().equals(position))
                .map(Map.Entry::getKey)
                .findFirst();
    }


    public Position playerPosition(PlayerId playerId) {
        return playerPositions.get(playerId);
    }

    public Set<BlockedEdge> allBlockedEdges() {
        Set<BlockedEdge> blockedEdges = new HashSet<>();
        for (Wall wall : walls) {
            blockedEdges.addAll(wall.blockedEdges());
        }
        return blockedEdges;
    }

    public boolean isEdgeBlocked(Position position, Direction direction) {
        return allBlockedEdges().contains(new BlockedEdge(position, direction));
    }
}

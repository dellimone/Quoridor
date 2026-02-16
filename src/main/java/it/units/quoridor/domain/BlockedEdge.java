package it.units.quoridor.domain;

/** An edge between two adjacent cells blocked by a wall. Each wall creates 4 blocked edges. */
public record BlockedEdge(Position position, Direction direction) {
}

package it.units.quoridor.domain;

import java.util.List;

public record GameState(
        Board board,
        List<Player> players,
        PlayerId currentPlayerId
) {
    // Compact constructor
    public GameState {
        players = List.copyOf(players);
    }

    // Convenience constructor - starts with first player
    public GameState(Board board, List<Player> players) {
        this(board, players, players.getFirst().id());
    }

    public Player currentPlayer() {
        return players.stream()
                .filter(p ->p.id()
                .equals(currentPlayerId))
                .findFirst()
                .orElseThrow();
    }
}

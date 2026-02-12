package it.units.quoridor.domain;

import java.util.List;

public record GameState(
        Board board,
        List<Player> players,
        int currentPlayerIndex
) {
    // Compact constructor - defensive copying
    public GameState {
        players = List.copyOf(players);
    }

    // Convenience constructor - starts with first player (index 0)
    public GameState(Board board, List<Player> players) {
        this(board, players, 0);
    }

    public PlayerId currentPlayerId() {
        return players.get(currentPlayerIndex).id();
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getPlayer(PlayerId playerId) {
        return players.stream()
                .filter(p -> p.id().equals(playerId))
                .findFirst()
                .orElseThrow();
    }

    public Position getPlayerPosition(PlayerId playerId) {
        return board.playerPosition(playerId);
    }

    public GameState withNextTurn() {
        int nextIndex = (currentPlayerIndex + 1) % players.size();
        return new GameState(board, players, nextIndex);
    }

    // we return a new GameState with updated turn (useful for valid pawn movements and pawn placements)
    public GameState withBoard(Board newBoard) {
        return new GameState(newBoard, players, currentPlayerIndex);
    }

    // creates a new player list where the player with the same id is replaced -> to avoid mutating
    public GameState withUpdatedPlayer(Player updatedPlayer) {
        List<Player> newPlayers = players.stream()
                .map(player -> player.id().equals(updatedPlayer.id()) ? updatedPlayer : player)
                .toList();

        return new GameState(board, newPlayers, currentPlayerIndex);
    }
}

package it.units.quoridor.domain;

import java.util.List;

/** Immutable snapshot of the entire game. All {@code with*} methods return new instances. */
public record GameState(
        Board board,
        List<Player> players,
        int currentPlayerIndex,
        GameStatus status,
        PlayerId winner) {

    public GameState {
        players = List.copyOf(players);
    }

    public GameState(Board board, List<Player> players, int currentPlayerIndex) {
        this(board, players, currentPlayerIndex, GameStatus.IN_PROGRESS, null);
    }

    public GameState(Board board, List<Player> players) {
        this(board, players, 0, GameStatus.IN_PROGRESS, null);
    }

    public PlayerId currentPlayerId() {
        return players.get(currentPlayerIndex).id();
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player player(PlayerId playerId) {
        return players.stream()
                .filter(p -> p.id().equals(playerId))
                .findFirst()
                .orElseThrow();
    }

    public Position playerPosition(PlayerId playerId) {
        return board.playerPosition(playerId);
    }

    public int currentPlayerWallsRemaining() {
        return currentPlayer().wallsRemaining();
    }

    public GameState withNextTurn() {
        int nextIndex = (currentPlayerIndex + 1) % players.size();
        return new GameState(board, players, nextIndex, status, winner);
    }

    public GameState withBoard(Board newBoard) {
        return new GameState(newBoard, players, currentPlayerIndex, status, winner);
    }

    public GameState withUpdatedPlayer(Player updatedPlayer) {
        List<Player> newPlayers = players.stream()
                .map(player -> player.id().equals(updatedPlayer.id()) ? updatedPlayer : player)
                .toList();

        return new GameState(board, newPlayers, currentPlayerIndex, status, winner);
    }

    public boolean isGameOver(){
        return status.equals(GameStatus.FINISHED);
    }

    public GameState withGameFinished(PlayerId winner) {
        return new GameState(board, players, currentPlayerIndex, GameStatus.FINISHED, winner);
    }

    public GameState withGameInProgress() {
        return new GameState(board, players, currentPlayerIndex,
                GameStatus.IN_PROGRESS, null);
    }

    public GameState withPawnMovedTo(PlayerId playerId, Position destination) {
        Board newBoard = board.withPlayerAt(playerId, destination);
        return withBoard(newBoard);
    }

    public GameState withWallPlaced(PlayerId playerId, Wall wall) {
        Board newBoard = board.addWall(wall);
        Player updatedPlayer = player(playerId).useWall();

        return withBoard(newBoard)
                   .withUpdatedPlayer(updatedPlayer);
    }
}

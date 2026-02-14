package it.units.quoridor.domain;

import java.util.List;

public record GameState(
        Board board,
        List<Player> players,
        int currentPlayerIndex,
        GameStatus status,
        PlayerId winner) {
    // Compact constructor
    public GameState {
        players = List.copyOf(players);
    }

    public GameState(Board board, List<Player> players, int currentPlayerIndex) {
        this(board, players, currentPlayerIndex, GameStatus.IN_PROGRESS, null);
    }

    // Convenience constructor - starts with first player (index 0)
    public GameState(Board board, List<Player> players) {
        this(board, players, 0, GameStatus.IN_PROGRESS, null);
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

    public int currentPlayerWallsRemaining() {
        return currentPlayer().wallsRemaining();
    }

    public GameState withNextTurn() {
        int nextIndex = (currentPlayerIndex + 1) % players.size();
        return new GameState(board, players, nextIndex, status, winner);
    }

    // we return a new GameState with updated turn (useful for valid pawn movements and pawn placements)
    public GameState withBoard(Board newBoard) {
        return new GameState(newBoard, players, currentPlayerIndex, status, winner);
    }

    // creates a new player list where the player with the same id is replaced -> to avoid mutating
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

    public GameState withPawnMoved(PlayerId playerId, Direction direction) {
        Position currentPos = board.playerPosition(playerId);
        Position newPos = currentPos.move(direction);
        Board newBoard = board.withPlayerAt(playerId, newPos);

        return this.withBoard(newBoard).withNextTurn();
    }

    public GameState withWallPlaced(PlayerId playerId, Wall wall) {
        Board newBoard = board.addWall(wall);
        Player updatedPlayer = getPlayer(playerId).useWall();

        return this.withBoard(newBoard)
                   .withUpdatedPlayer(updatedPlayer)
                   .withNextTurn();
    }
}

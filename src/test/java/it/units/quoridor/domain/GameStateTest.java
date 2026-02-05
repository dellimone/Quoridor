package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void createGameStateWithBoardAndPlayers() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);

        GameState gameState = new GameState(board, players);

        assertNotNull(gameState);
        assertEquals(board, gameState.board());
        assertEquals(2, gameState.players().size());
        assertTrue(gameState.players().contains(player1));
        assertTrue(gameState.players().contains(player2));
        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId()); // Starts with first player
    }

    @Test
    void getCurrentPlayerReturnsPlayerWhoseTurnItIs() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);

        GameState gameState = new GameState(board, players);

        Player currentPlayer = gameState.currentPlayer();

        assertEquals(player1, currentPlayer);
        assertEquals("Alice", currentPlayer.name());
    }
}
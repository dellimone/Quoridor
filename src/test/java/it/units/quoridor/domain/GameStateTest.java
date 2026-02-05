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

    @Test
    void getPlayerByIdReturnsCorrectPlayer() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);

        GameState gameState = new GameState(board, players);

        Player retrievedPlayer = gameState.getPlayer(PlayerId.PLAYER_2);

        assertEquals(player2, retrievedPlayer);
        assertEquals("Bob", retrievedPlayer.name());
    }

    @Test
    void withNextTurnReturnsNewStateWithNextPlayer() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        GameState nextState = gameState.withNextTurn();

        // Original state unchanged (immutability)
        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
        // New state has next player
        assertEquals(PlayerId.PLAYER_2, nextState.currentPlayerId());
        assertEquals(player2, nextState.currentPlayer());
    }

    @Test
    void twoPlayerGameCyclesCorrectly() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState state = new GameState(board, players);

        // P1 → P2 → P1 → P2
        assertEquals(PlayerId.PLAYER_1, state.currentPlayerId());

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_2, state.currentPlayerId());

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_1, state.currentPlayerId()); // Cycles back!

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_2, state.currentPlayerId());
    }

    @Test
    void fourPlayerGameCyclesCorrectly() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 5, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 5, 8);
        Player player3 = new Player(PlayerId.PLAYER_3, "Charlie", 5, 4);
        Player player4 = new Player(PlayerId.PLAYER_4, "Diana", 5, 4);
        List<Player> players = List.of(player1, player2, player3, player4);
        GameState state = new GameState(board, players);

        // P1 → P2 → P3 → P4 → P1
        assertEquals(PlayerId.PLAYER_1, state.currentPlayerId());

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_2, state.currentPlayerId());

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_3, state.currentPlayerId());

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_4, state.currentPlayerId());

        state = state.withNextTurn();
        assertEquals(PlayerId.PLAYER_1, state.currentPlayerId()); // Cycles back!
    }
}
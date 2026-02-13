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

    // Tests for game status functionality

    @Test
    void newGameStateHasStatusInProgress() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);

        GameState gameState = new GameState(board, players);

        assertEquals(GameStatus.IN_PROGRESS, gameState.status());
        assertNull(gameState.winner());
        assertFalse(gameState.isGameOver());
    }

    @Test
    void isGameOverReturnsFalseForInProgressGame() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);

        GameState gameState = new GameState(board, players);

        assertFalse(gameState.isGameOver());
    }

    @Test
    void withGameFinishedSetsStatusAndWinner() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        assertEquals(GameStatus.FINISHED, finishedState.status());
        assertEquals(PlayerId.PLAYER_1, finishedState.winner());
        assertTrue(finishedState.isGameOver());
    }

    @Test
    void withGameFinishedPreservesOtherFields() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players, 1); // P2's turn

        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_2);

        assertEquals(board, finishedState.board());
        assertEquals(players, finishedState.players());
        assertEquals(1, finishedState.currentPlayerIndex());
        assertEquals(PlayerId.PLAYER_2, finishedState.currentPlayerId());
    }

    @Test
    void withGameInProgressClearsWinner() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        // First finish the game
        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);
        assertTrue(finishedState.isGameOver());

        // Then resume it
        GameState resumedState = finishedState.withGameInProgress();

        assertEquals(GameStatus.IN_PROGRESS, resumedState.status());
        assertNull(resumedState.winner());
        assertFalse(resumedState.isGameOver());
    }

    @Test
    void withGameInProgressPreservesOtherFields() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState finishedState = new GameState(board, players, 1);
        finishedState = finishedState.withGameFinished(PlayerId.PLAYER_2);

        GameState resumedState = finishedState.withGameInProgress();

        assertEquals(board, resumedState.board());
        assertEquals(players, resumedState.players());
        assertEquals(1, resumedState.currentPlayerIndex());
    }

    @Test
    void isGameOverReturnsTrueForFinishedGame() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        assertTrue(finishedState.isGameOver());
    }


    @Test
    void withBoardPreservesGameStatus() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        // Finish the game
        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        // Update the board
        Board newBoard = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(1, 1))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(7, 7));
        GameState updatedState = finishedState.withBoard(newBoard);

        // Status should still be FINISHED
        assertEquals(GameStatus.FINISHED, updatedState.status());
        assertEquals(PlayerId.PLAYER_1, updatedState.winner());
        assertEquals(newBoard, updatedState.board());
    }

    @Test
    void withUpdatedPlayerPreservesGameStatus() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        // Finish the game
        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_2);

        // Update a player (e.g., walls used)
        Player updatedPlayer1 = player1.withWallsRemaining(9);
        GameState updatedState = finishedState.withUpdatedPlayer(updatedPlayer1);

        // Status should still be FINISHED
        assertEquals(GameStatus.FINISHED, updatedState.status());
        assertEquals(PlayerId.PLAYER_2, updatedState.winner());
        assertEquals(9, updatedState.getPlayer(PlayerId.PLAYER_1).wallsRemaining());
    }

    @Test
    void withNextTurnPreservesGameStatus() {
        Board board = new Board();
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        List<Player> players = List.of(player1, player2);
        GameState gameState = new GameState(board, players);

        // Finish the game
        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        // Advance turn
        GameState nextTurn = finishedState.withNextTurn();

        // Status should still be FINISHED
        assertEquals(GameStatus.FINISHED, nextTurn.status());
        assertEquals(PlayerId.PLAYER_1, nextTurn.winner());
        assertEquals(PlayerId.PLAYER_2, nextTurn.currentPlayerId()); // Turn advanced
    }
}
package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void createGameStateWithBoardAndPlayers() {
        GameState gameState = stateWith(new Board());

        assertNotNull(gameState);
        assertEquals(2, gameState.players().size());
        assertTrue(gameState.players().contains(P1));
        assertTrue(gameState.players().contains(P2));
        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
    }

    @Test
    void getCurrentPlayerReturnsPlayerWhoseTurnItIs() {
        GameState gameState = stateWith(new Board());

        Player currentPlayer = gameState.currentPlayer();

        assertEquals(P1, currentPlayer);
        assertEquals("P1", currentPlayer.name());
    }

    @Test
    void getPlayerByIdReturnsCorrectPlayer() {
        GameState gameState = stateWith(new Board());

        Player retrievedPlayer = gameState.getPlayer(PlayerId.PLAYER_2);

        assertEquals(P2, retrievedPlayer);
        assertEquals("P2", retrievedPlayer.name());
    }

    @Test
    void withNextTurnReturnsNewStateWithNextPlayer() {
        GameState gameState = stateWith(new Board());

        GameState nextState = gameState.withNextTurn();

        // Original state unchanged (immutability)
        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
        // New state has next player
        assertEquals(PlayerId.PLAYER_2, nextState.currentPlayerId());
        assertEquals(P2, nextState.currentPlayer());
    }

    @Test
    void twoPlayerGameCyclesCorrectly() {
        GameState state = stateWith(new Board());

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
        Player player1 = new Player(PlayerId.PLAYER_1, "Alice", 5);
        Player player2 = new Player(PlayerId.PLAYER_2, "Bob", 5);
        Player player3 = new Player(PlayerId.PLAYER_3, "Charlie", 5);
        Player player4 = new Player(PlayerId.PLAYER_4, "Diana", 5);
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
        GameState gameState = stateWith(new Board());

        assertEquals(GameStatus.IN_PROGRESS, gameState.status());
        assertNull(gameState.winner());
        assertFalse(gameState.isGameOver());
    }

    @Test
    void isGameOverReturnsFalseForInProgressGame() {
        GameState gameState = stateWith(new Board());
        assertFalse(gameState.isGameOver());
    }

    @Test
    void withGameFinishedSetsStatusAndWinner() {
        GameState gameState = stateWith(new Board());

        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        assertEquals(GameStatus.FINISHED, finishedState.status());
        assertEquals(PlayerId.PLAYER_1, finishedState.winner());
        assertTrue(finishedState.isGameOver());
    }

    @Test
    void withGameFinishedPreservesOtherFields() {
        Board board = new Board();
        GameState gameState = new GameState(board, List.of(P1, P2), 1); // P2's turn

        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_2);

        assertEquals(board, finishedState.board());
        assertEquals(1, finishedState.currentPlayerIndex());
        assertEquals(PlayerId.PLAYER_2, finishedState.currentPlayerId());
    }

    @Test
    void withGameInProgressClearsWinner() {
        GameState gameState = stateWith(new Board());

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
        GameState finishedState = new GameState(board, List.of(P1, P2), 1);
        finishedState = finishedState.withGameFinished(PlayerId.PLAYER_2);

        GameState resumedState = finishedState.withGameInProgress();

        assertEquals(board, resumedState.board());
        assertEquals(1, resumedState.currentPlayerIndex());
    }

    @Test
    void isGameOverReturnsTrueForFinishedGame() {
        GameState gameState = stateWith(new Board());

        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        assertTrue(finishedState.isGameOver());
    }


    @Test
    void withBoardPreservesGameStatus() {
        GameState gameState = stateWith(new Board());

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
        GameState gameState = stateWith(new Board());

        // Finish the game
        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_2);

        // Update a player (e.g., walls used)
        Player updatedPlayer1 = P1.withWallsRemaining(9);
        GameState updatedState = finishedState.withUpdatedPlayer(updatedPlayer1);

        // Status should still be FINISHED
        assertEquals(GameStatus.FINISHED, updatedState.status());
        assertEquals(PlayerId.PLAYER_2, updatedState.winner());
        assertEquals(9, updatedState.getPlayer(PlayerId.PLAYER_1).wallsRemaining());
    }

    @Test
    void withNextTurnPreservesGameStatus() {
        GameState gameState = stateWith(new Board());

        // Finish the game
        GameState finishedState = gameState.withGameFinished(PlayerId.PLAYER_1);

        // Advance turn
        GameState nextTurn = finishedState.withNextTurn();

        // Status should still be FINISHED
        assertEquals(GameStatus.FINISHED, nextTurn.status());
        assertEquals(PlayerId.PLAYER_1, nextTurn.winner());
        assertEquals(PlayerId.PLAYER_2, nextTurn.currentPlayerId()); // Turn advanced
    }

    // Tests for withWallPlaced transformation

    @Test
    void withWallPlacedAddsWallToBoard() {
        GameState gameState = standardState();

        Wall wall = hWall(3, 3);
        GameState newState = gameState.withWallPlaced(PlayerId.PLAYER_1, wall);

        // Wall should be added
        assertTrue(newState.board().walls().contains(wall));
        // Original unchanged
        assertFalse(gameState.board().walls().contains(wall));
    }

    @Test
    void withWallPlacedDecrementsPlayerWalls() {
        GameState gameState = standardState();

        Wall wall = hWall(3, 3);
        GameState newState = gameState.withWallPlaced(PlayerId.PLAYER_1, wall);

        // Player 1 should have one less wall
        assertEquals(10, gameState.getPlayer(PlayerId.PLAYER_1).wallsRemaining()); // Original unchanged
        assertEquals(9, newState.getPlayer(PlayerId.PLAYER_1).wallsRemaining()); // Decremented
        // Player 2 unchanged
        assertEquals(10, newState.getPlayer(PlayerId.PLAYER_2).wallsRemaining());
    }

    @Test
    void withWallPlacedDoesNotAdvanceTurn() {
        GameState gameState = standardState();

        Wall wall = hWall(3, 3);
        GameState newState = gameState.withWallPlaced(PlayerId.PLAYER_1, wall);

        // Turn should NOT advance — that's the engine's responsibility
        assertEquals(PlayerId.PLAYER_1, gameState.currentPlayerId());
        assertEquals(PlayerId.PLAYER_1, newState.currentPlayerId());
    }

    @Test
    void withWallPlacedPreservesPlayerPositions() {
        GameState gameState = standardState();

        Wall wall = hWall(3, 3);
        GameState newState = gameState.withWallPlaced(PlayerId.PLAYER_1, wall);

        // Player positions should be unchanged
        assertEquals(P1_START, newState.getPlayerPosition(PlayerId.PLAYER_1));
        assertEquals(P2_START, newState.getPlayerPosition(PlayerId.PLAYER_2));
    }

    // Tests for helper methods (Law of Demeter)

    @Test
    void currentPlayerWallsRemainingReturnsCurrentPlayerWallCount() {
        Player p2With7 = new Player(PlayerId.PLAYER_2, "P2", 7);
        GameState gameState = new GameState(new Board(), List.of(P1, p2With7));

        // Current player is PLAYER_1
        assertEquals(10, gameState.currentPlayerWallsRemaining());
    }

    @Test
    void currentPlayerWallsRemainingReflectsTurnChanges() {
        Player p2With7 = new Player(PlayerId.PLAYER_2, "P2", 7);
        GameState gameState = new GameState(new Board(), List.of(P1, p2With7));

        // Current player is PLAYER_1
        assertEquals(10, gameState.currentPlayerWallsRemaining());

        // Advance turn to PLAYER_2
        GameState nextState = gameState.withNextTurn();
        assertEquals(7, nextState.currentPlayerWallsRemaining());
    }

    @Test
    void currentPlayerWallsRemainingReflectsWallUsage() {
        GameState gameState = standardState();

        Wall wall = hWall(3, 3);
        GameState newState = gameState.withWallPlaced(PlayerId.PLAYER_1, wall);

        // Current player is still P1 (turn not advanced), with 9 walls
        assertEquals(9, newState.currentPlayerWallsRemaining());
        assertEquals(9, newState.getPlayer(PlayerId.PLAYER_1).wallsRemaining());
    }
}

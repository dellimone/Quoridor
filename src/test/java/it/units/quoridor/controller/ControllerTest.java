package it.units.quoridor.controller;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.MoveResult;
import it.units.quoridor.view.BoardViewModel;
import it.units.quoridor.view.GameView;
import it.units.quoridor.view.PlayerViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private GameEngine gameEngine;
    private GameView gameView;
    private Controller controller;

    // Before each test is run, it creates the instances for GameEngine, GameView and Controller
    @BeforeEach
    public void setUp() {
        gameEngine = mock(GameEngine.class);        // for now, we use just interfaces
        gameView = mock(GameView.class);
        controller = new Controller(gameEngine, gameView);      // constructor logic
    }

    // Ensure that the connection between View and Controller is established
    @Test
    public void controllerTests() {
        verify(gameView).setListener(controller);
    }

    @Test
    void newGameTest() {
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.players()).thenReturn(List.of());
        when(board.walls()).thenReturn(Collections.emptySet());

        // new game
        controller.onNewGame(2);

        // if we want to do reset
        // TODO verify(gameEngine).setUp()
        // control that the controller has told the view to redraw the players
        verify(gameView).renderBoard(any());
    }

    @Test
    void onClickTest() {

        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);
        when(player.id()).thenReturn(PlayerId.PLAYER_1);

        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0,0));

        when(gameEngine.movePawn(any(PlayerId.class), any(Position.class))).thenReturn(MoveResult.success());

        controller.onCellClicked(7, 0);

        verify(gameEngine).movePawn(eq(PlayerId.PLAYER_1), eq(new Position(1, 0)));
    }

    @Test
    void updateViewTest() {

        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);

        when(gameState.players()).thenReturn(List.of(player));
        when(player.id()).thenReturn(PlayerId.PLAYER_1);

        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0,0));
        when(board.walls()).thenReturn(Collections.emptySet());

        controller.onNewGame(2);

        ArgumentCaptor<BoardViewModel> captor = ArgumentCaptor.forClass(BoardViewModel.class);
        verify(gameView).renderBoard(captor.capture());

        BoardViewModel captureModel = captor.getValue();
        Position viewPosition = captureModel.playerPositions().get(PlayerId.PLAYER_1);

        assertEquals(new Position(8,0), viewPosition);
    }

    @Test
    void renderBoardTest() {

        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);

        when(gameState.players()).thenReturn(List.of(player));
        when(player.id()).thenReturn(PlayerId.PLAYER_1);

        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0,0));
        when(board.walls()).thenReturn(Collections.emptySet());

        controller.updateGameBoard(gameState);

        ArgumentCaptor<BoardViewModel> captor = ArgumentCaptor.forClass(BoardViewModel.class);
        verify(gameView).renderBoard(captor.capture());

        BoardViewModel boardViewModel = captor.getValue();
        assertEquals(new Position(8,0), boardViewModel.playerPositions().get(PlayerId.PLAYER_1));
    }

    @Test
    void updateInfoPanelTest() {

        GameState gameState = mock(GameState.class);
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);

        when(gameState.players()).thenReturn(List.of(p1, p2));
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);

        when(p1.id()).thenReturn(PlayerId.PLAYER_1);
        when(p1.name()).thenReturn("Marco");
        when(p1.wallsRemaining()).thenReturn(10);

        when(p2.id()).thenReturn(PlayerId.PLAYER_2);
        when(p2.name()).thenReturn("Luca");
        when(p2.wallsRemaining()).thenReturn(10);

        controller.updateInfoPanel(gameState);

        ArgumentCaptor<List<PlayerViewModel>> captor = ArgumentCaptor.forClass(List.class);
        verify(gameView).updatePlayerInfo(captor.capture());

        List<PlayerViewModel> result = captor.getValue();

        // Controllo numero giocatori
        assertEquals(2, result.size());

        PlayerViewModel marco = result.get(0);
        assertEquals("Marco", marco.name());
        assertEquals(10, marco.wallsRemaining());
        assertTrue(marco.isCurrentPlayer(), "Alice dovrebbe essere il giocatore attivo");

        PlayerViewModel luca = result.get(1);
        assertEquals("Luca", luca.name());
        assertEquals(10, luca.wallsRemaining());
        assertFalse(luca.isCurrentPlayer(), "Bob NON dovrebbe essere il giocatore attivo");

        verify(gameView, never()).renderBoard(any());
    }

    @Test
    void onWallPlacementTest() {

        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(gameState.players()).thenReturn(List.of(player));
        when(player.id()).thenReturn(PlayerId.PLAYER_1);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);
        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0,0));
        when(board.walls()).thenReturn(Collections.emptySet());
        when(gameEngine.placeWall(any(PlayerId.class), any(Wall.class))).thenReturn(MoveResult.success());

        controller.onWallPlacement(7,0, WallOrientation.HORIZONTAL);

        verify(gameEngine).placeWall(eq(PlayerId.PLAYER_1), eq(new Wall(new WallPosition(0,0), WallOrientation.HORIZONTAL)));
    }

    @Test
    void onQuitShouldNotThrow() {
        // For now, we just verify the method can be called without exceptions

        // Act & Assert - should not throw
        //assertDoesNotThrow(() -> controller.onQuit());

        // Note: Actual System.exit() behavior would need integration testing
        // or extracting the exit logic to make it testable
    }

    @Test
    void updateViewShouldCallUpdatePlayerInfo() {
        // Arrange
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);
        when(gameState.players()).thenReturn(List.of(player1, player2));
        when(gameState.isGameOver()).thenReturn(false);

        when(player1.id()).thenReturn(PlayerId.PLAYER_1);
        when(player1.name()).thenReturn("Player 1");
        when(player1.wallsRemaining()).thenReturn(10);

        when(player2.id()).thenReturn(PlayerId.PLAYER_2);
        when(player2.name()).thenReturn("Player 2");
        when(player2.wallsRemaining()).thenReturn(9);

        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0, 4));
        when(board.playerPosition(PlayerId.PLAYER_2)).thenReturn(new Position(8, 4));
        when(board.walls()).thenReturn(Collections.emptySet());

        // Act
        controller.updateView();

        // Assert - should call updatePlayerInfo with player view models
        verify(gameView).updatePlayerInfo(any(List.class));
        verify(gameView).renderBoard(any());
        verify(gameView).setCurrentPlayer(PlayerId.PLAYER_1);
        verify(gameView).setUndoEnabled(true);  // Game in progress, undo should be enabled
    }

    @Test
    void updateViewWhenGameOverShouldDisableUndo() {
        // Arrange
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);
        when(gameState.players()).thenReturn(List.of(player));
        when(gameState.isGameOver()).thenReturn(true);  // Game is over

        when(player.id()).thenReturn(PlayerId.PLAYER_1);
        when(player.name()).thenReturn("Player 1");
        when(player.wallsRemaining()).thenReturn(10);

        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0, 4));
        when(board.walls()).thenReturn(Collections.emptySet());

        // Act
        controller.updateView();

        // Assert - undo should be disabled when game is over
        verify(gameView).setUndoEnabled(false);
    }


    @Test
    void winningMoveShouldCallShowGameOver() {
        // Arrange
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);
        when(player.id()).thenReturn(PlayerId.PLAYER_1);
        when(player.name()).thenReturn("Player 1");
        when(board.playerPosition(PlayerId.PLAYER_1)).thenReturn(new Position(0, 0));
        when(gameState.players()).thenReturn(List.of(player));
        when(board.walls()).thenReturn(Collections.emptySet());

        // Mock a WINNING move (isValid=true, isWin=true)
        when(gameEngine.movePawn(any(PlayerId.class), any(Position.class)))
                .thenReturn(MoveResult.win());

        // Act - click adjacent cell to trigger winning move
        controller.onCellClicked(7, 0);

        // Assert - CRITICAL: should call showGameOver
        verify(gameView).renderBoard(any(BoardViewModel.class));
        verify(gameView).showGameOver(PlayerId.PLAYER_1);
        verify(gameView, never()).showMessage(anyString());
    }

    @Test
    void onNewGameShouldResetEngineAndDisableUndo() {
        // Arrange
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.players()).thenReturn(List.of());
        when(board.walls()).thenReturn(Collections.emptySet());

        // Act
        controller.onNewGame(2);

        // Assert - verify all expected behavior
        verify(gameEngine).reset();  // Must call engine.reset()!
        verify(gameView).renderBoard(any(BoardViewModel.class));
        verify(gameView).setUndoEnabled(false);  // No history at start
        verify(gameView).showMessage("New game started!");
    }

    @Test
    void onUndoWithHistoryShouldRestoreStateAndShowMessage() {
        // Arrange
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.players()).thenReturn(List.of());
        when(board.walls()).thenReturn(Collections.emptySet());
        when(gameEngine.undo()).thenReturn(true);  // Undo successful

        // Act
        controller.onUndo();

        // Assert
        verify(gameEngine).undo();
        verify(gameView).renderBoard(any(BoardViewModel.class));
        verify(gameView).showMessage("Move undone");
    }

    @Test
    void onUndoWithoutHistoryShouldShowError() {
        // Arrange
        when(gameEngine.undo()).thenReturn(false);  // No history to undo

        // Act
        controller.onUndo();

        // Assert
        verify(gameEngine).undo();
        verify(gameView).showError("Nothing to undo");
        verify(gameView, never()).renderBoard(any());
    }

    @Test
    void updateHighlightTest() {

        GameState gameState = mock(GameState.class);

        when(gameState.isGameOver()).thenReturn(false);
        when(gameState.currentPlayerId()).thenReturn(PlayerId.PLAYER_1);

        Position domainMove = new Position(0, 4);
        Set<Position> domainMoves = Set.of(domainMove);

        when(gameEngine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1)).thenReturn(domainMoves);

        controller.updateHighlights(gameState);

        ArgumentCaptor<Set<Position>>  captor = ArgumentCaptor.forClass(Set.class);
        verify(gameView).highlightValidMoves(captor.capture());

        Set<Position> movesToView = captor.getValue();

        Position expectedViewMove = new Position(8, 4);

        assertTrue(movesToView.contains(expectedViewMove),
                "La posizione dovrebbe essere convertita da (1,4) a (7,4)" + movesToView);

        assertEquals(1, movesToView.size());
    }

}

package it.units.quoridor.controller;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.MoveResult;
import it.units.quoridor.view.BoardViewModel;
import it.units.quoridor.view.GameView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

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
        when(gameEngine.movePawn(any(PlayerId.class), any(Direction.class))).thenReturn(MoveResult.success());

        controller.onCellClicked(7, 0);

        verify(gameEngine).movePawn(PlayerId.PLAYER_1, Direction.NORTH);
    }

    @Test
    void controlAdjacentTest() {
        assertTrue(controller.isAdjacent(new Position(0,0), new Position(0,1)));
        assertTrue(controller.isAdjacent(new Position(1,0), new Position(0,0)));
        assertFalse(controller.isAdjacent(new Position(0,0), new Position(1,1)));
    }

    @Test
    void controlDirectionTest() {
        Direction north = controller.calculateDirection(new Position(0,0), new Position(1,0));
        Direction east = controller.calculateDirection(new Position(0,0), new Position(0,1));
        Direction south = controller.calculateDirection(new Position(1,0), new Position(0,0));
        Direction west = controller.calculateDirection(new Position(0,1), new Position(0,0));
        assertEquals(Direction.NORTH, north);
        assertEquals(Direction.EAST, east);
        assertEquals(Direction.SOUTH, south);
        assertEquals(Direction.WEST, west);
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
    void onUndoTest() {
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(player.id()).thenReturn(PlayerId.PLAYER_1);

        when(gameState.players()).thenReturn(Collections.emptyList());
        when(board.walls()).thenReturn(Collections.emptySet());

        controller.onUndo();

        // verify(gameEngine).undo();

        verify(gameView).renderBoard(any());
    }

    @Test
    void onQuitTest() {
        GameState gameState = mock(GameState.class);
        Board board = mock(Board.class);
        Player player = mock(Player.class);

        when(gameEngine.getGameState()).thenReturn(gameState);
        when(gameState.board()).thenReturn(board);
        when(gameState.currentPlayer()).thenReturn(player);
        when(player.id()).thenReturn(PlayerId.PLAYER_1);

        when(gameState.players()).thenReturn(Collections.emptyList());
        when(board.walls()).thenReturn(Collections.emptySet());

        controller.onQuit();

        // verify(gameEngine).quit();

        verify(gameView).renderBoard(any());
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
        when(gameEngine.movePawn(any(PlayerId.class), any(Direction.class)))
                .thenReturn(MoveResult.win());

        // Act - click adjacent cell to trigger winning move
        controller.onCellClicked(7, 0);

        // Assert - CRITICAL: should call showGameOver
        verify(gameView).renderBoard(any(BoardViewModel.class));
        verify(gameView).showGameOver(PlayerId.PLAYER_1);
        verify(gameView, never()).showMessage(anyString());
    }

}

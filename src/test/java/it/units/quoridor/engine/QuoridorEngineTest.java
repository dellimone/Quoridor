package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuoridorEngineTest {

    // needed mocks
    @Mock PawnMoveValidator pawnValidator;
    @Mock WallPlacementValidator wallValidator;
    @Mock WinChecker winChecker; // will be implemented later


    // 1. we want to test whether the GameEngine handles the board correctly -> initial state set correctly
    @Test
    void engineExposesInitialGameState() {
        // create a small example for board
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        GameState actual = engine.getGameState();

        // if the validator returns false the engine should not change state
        assertSame(initialState, actual);
    }

    // 19. reset game on demand
    @Test
    void resetGameOnDemand() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8); // p1 has no walls
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);


        // we do some actions (two valid moves)
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(true);
        MoveResult move1 = engine.placeWall(PlayerId.PLAYER_1, wall);

        // check if current state changed
        GameState oldGameState = engine.getGameState();
        assertNotSame(initialState, oldGameState);


        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_2), eq(Direction.EAST))).thenReturn(true);
        MoveResult move2 = engine.movePawn(PlayerId.PLAYER_2, Direction.EAST);

        // check if current state changed
        assertNotSame(oldGameState, engine.getGameState());

        // now we reset
        engine.reset();

        // and we should we back to the initial state
        assertEquals(initialState, engine.getGameState());
    }

    // 20. undo on new engine returns false
    @Test
    void undoNewGame() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8); // p1 has no walls
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        boolean undoAction = engine.undo();
        assertFalse(undoAction);
    }

    // 21. after valid pawn move, state changes and we save it in the history
    @Test
    void undoAfterValidPawnMove() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8); // p1 has no walls
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);


        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        assertNotSame(initialState, engine.getGameState());

        boolean undoAction = engine.undo();
        assertTrue(undoAction);
    }

    // 22. after valid pawn move, state changes; if we do undo(), we go back to the initial state
    @Test
    void undoAfterValidPawnMove_restorePreviousState() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8); // p1 has no walls
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);


        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        assertNotSame(initialState, engine.getGameState());

        boolean undoAction = engine.undo();
        assertTrue(undoAction);

        // we check that we actually went back to the previous state
        assertSame(initialState, engine.getGameState());
    }

    // 23. after valid wall placement, state changes; if we do undo(), we go back to the initial state
    @Test
    void undoAfterValidWallPlacement_restorePreviousState() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8); // p1 has no walls
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // make a valid wall placement
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(true);
        engine.placeWall(PlayerId.PLAYER_1, wall);

        // assert the state actually changed
        assertNotSame(initialState, engine.getGameState());
        assertEquals(9, engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining());
        // also assert walls changed

        // undo last wall action
        boolean undoAction = engine.undo();
        assertTrue(undoAction);

        // we check that we actually went back to the previous state
        assertSame(initialState, engine.getGameState());

        // assert P1 went back to 10 walls remaining
        assertEquals(10, engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining());
    }
}


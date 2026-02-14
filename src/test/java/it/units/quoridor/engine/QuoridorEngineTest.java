package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;


import it.units.quoridor.logic.rules.validation.PawnMoveValidator;
import it.units.quoridor.logic.rules.validation.WallPlacementValidator;
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
    @Mock
    PawnMoveValidator pawnValidator;
    @Mock
    WallPlacementValidator wallValidator;
    @Mock WinChecker winChecker; // will be implemented later


    // 1. we want to test whether the GameEngine handles the board correctly -> initial state set correctly
    @Test
    void engineExposesInitialGameState() {
        // Create engine with rules (automatically initialized)
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState actual = engine.getGameState();

        // Verify initial state matches rules
        assertNotNull(actual);
        assertEquals(PlayerId.PLAYER_1, actual.currentPlayerId());
        assertEquals(new Position(0, 4), actual.getPlayerPosition(PlayerId.PLAYER_1));
        assertEquals(new Position(8, 4), actual.getPlayerPosition(PlayerId.PLAYER_2));
    }

    // 19. reset game on demand
    @Test
    void resetGameOnDemand() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // we do some actions (two valid moves)
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(true);
        engine.placeWall(PlayerId.PLAYER_1, wall);

        // check if current state changed
        GameState oldGameState = engine.getGameState();
        assertNotEquals(initialState, oldGameState);

        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_2), eq(Direction.EAST))).thenReturn(true);
        engine.movePawn(PlayerId.PLAYER_2, Direction.EAST);

        // check if current state changed
        assertNotEquals(oldGameState, engine.getGameState());

        // now we reset
        engine.reset();

        // and we should be back to the initial state
        assertEquals(initialState, engine.getGameState());
    }

    // 20. undo on new engine returns false
    @Test
    void undoNewGame() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        boolean undoAction = engine.undo();
        assertFalse(undoAction);
    }

    // 21. after valid pawn move, state changes and we save it in the history
    @Test
    void undoAfterValidPawnMove() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        assertNotEquals(initialState, engine.getGameState());

        boolean undoAction = engine.undo();
        assertTrue(undoAction);
    }

    // 22. after valid pawn move, state changes; if we do undo(), we go back to the initial state
    @Test
    void undoAfterValidPawnMove_restorePreviousState() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        assertNotEquals(initialState, engine.getGameState());

        boolean undoAction = engine.undo();
        assertTrue(undoAction);

        // we check that we actually went back to the previous state
        assertEquals(initialState, engine.getGameState());
    }

    // 23. after valid wall placement, state changes; if we do undo(), we go back to the initial state
    @Test
    void undoAfterValidWallPlacement_restorePreviousState() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // make a valid wall placement
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(true);
        engine.placeWall(PlayerId.PLAYER_1, wall);

        // assert the state actually changed
        assertNotEquals(initialState, engine.getGameState());
        assertEquals(9, engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining());

        // undo last wall action
        boolean undoAction = engine.undo();
        assertTrue(undoAction);

        // we check that we actually went back to the previous state
        assertEquals(initialState, engine.getGameState());

        // assert P1 went back to 10 walls remaining
        assertEquals(10, engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining());
    }

    // 24. history should be cleared after a reset
    @Test
    void clearHistoryAfterReset() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // make a valid wall placement
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(true);
        engine.placeWall(PlayerId.PLAYER_1, wall);

        // assert the state actually changed
        assertNotEquals(initialState, engine.getGameState());
        assertEquals(9, engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining());

        // reset game
        engine.reset();

        // try undo last wall action (should fail because history was cleared)
        boolean undoAction = engine.undo();
        assertFalse(undoAction);

        // we check that we actually went back to the initial state
        assertEquals(initialState, engine.getGameState());

        // assert P1 went back to 10 walls remaining
        assertEquals(10, engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining());
    }

    // Test for new constructor + newGame() refactoring
    @Test
    void newGameCreatesInitialStateFromRules() {
        // Given: GameRules that define a 2-player setup
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(
            rules,
            pawnValidator,
            wallValidator,
            winChecker
        );

        // When: Starting a new game

        // Then: GameState should match the rules
        GameState state = engine.getGameState();

        // Players should be at start positions per rules
        assertEquals(new Position(0, 4), state.getPlayerPosition(PlayerId.PLAYER_1));
        assertEquals(new Position(8, 4), state.getPlayerPosition(PlayerId.PLAYER_2));

        // Players should have correct wall count (10 for 2-player)
        assertEquals(10, state.getPlayer(PlayerId.PLAYER_1).wallsRemaining());
        assertEquals(10, state.getPlayer(PlayerId.PLAYER_2).wallsRemaining());

        // Game should be in progress
        assertEquals(GameStatus.IN_PROGRESS, state.status());
        assertFalse(state.isGameOver());

        // Current player should be PLAYER_1
        assertEquals(PlayerId.PLAYER_1, state.currentPlayerId());
    }
}


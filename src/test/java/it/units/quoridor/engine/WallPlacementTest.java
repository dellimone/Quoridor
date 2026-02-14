package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WallPlacementTest {

    @Mock PawnMoveValidator pawnValidator;
    @Mock WallPlacementValidator wallValidator;
    @Mock WinChecker winChecker;

    // WALL PLACEMENT ORCHESTRATION

    // 9. Wall validation is delegated to the wall validator by the engine
    @Test
    void wallValidation() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // make the wall positioning move
        engine.placeWall(PlayerId.PLAYER_1, wall);

        // verify the wall validator is called
        verify(wallValidator).canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall));
        verifyNoMoreInteractions(wallValidator);
    }

    // 10. Invalid wall -> MoveResult.INVALID and unchanged state
    @Test
    void invalidWallPlacement() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return false for this wall placement
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(false);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertFalse(result.isValid());
        assertEquals(initialState, engine.getGameState());

        verify(wallValidator).canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall));
        verifyNoMoreInteractions(wallValidator);
    }

    // 11. Valid wall -> OK + turn advanced
    @Test
    void validWallPlacement() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(true);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertTrue(result.isValid());

        GameState nextState = engine.getGameState();
        assertEquals(PlayerId.PLAYER_2, nextState.currentPlayerId());

        verify(wallValidator).canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall));
        verifyNoMoreInteractions(wallValidator);
    }

    // 12. Wrong player CANNOT place wall
    @Test
    void wrongTurnInvalidPlacement() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_2, wall);

        // assertions
        assertFalse(result.isValid());
        assertEquals(initialState, engine.getGameState());

        // wall validator SHOULD NOT be called at all
        verifyNoInteractions(wallValidator);
    }

    // 13. walls cannot be placed after game ended
    @Test
    void noWallsAfterGameOver() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // Simulate a winning move to trigger game over
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.NORTH))).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(true);
        engine.movePawn(PlayerId.PLAYER_1, Direction.NORTH);

        assertTrue(engine.isGameOver());

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertFalse(result.isValid());
        assertTrue(engine.isGameOver()); // we should not change the "game over" mark

        // wall validator SHOULD NOT be called at all
        verifyNoInteractions(wallValidator);
    }

    // 14. placing valid walls should update the board
    @Test
    void boardUpdateValidWallPlacement() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        Board initialBoard = engine.getGameState().board();

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(true);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertTrue(result.isValid());

        GameState nextState = engine.getGameState();
        Board newBoard = engine.getGameState().board();

        assertEquals(PlayerId.PLAYER_2, nextState.currentPlayerId());
        assertEquals(initialBoard.addWall(wall), newBoard); // new board should reflect valid wall placement

        verify(wallValidator).canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall));
        verifyNoMoreInteractions(wallValidator);
    }

    // 16. valid wall placement should consume a wall
    @Test
    void validWallPlacementWallConsumption() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        int currentWalls = engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining();

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(true);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);
        assertTrue(result.isValid());

        // Verify wall was consumed
        Player updatedP1 = engine.getGameState().getPlayer(PlayerId.PLAYER_1);
        assertEquals(currentWalls - 1, updatedP1.wallsRemaining());

        verify(wallValidator).canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall));
        verifyNoMoreInteractions(wallValidator);
    }

    // 17. invalid wall placements should not consume walls
    @Test
    void invalidWallPlacementWallConsumption() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();
        int currentWalls = initialState.getPlayer(PlayerId.PLAYER_1).wallsRemaining();

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return false for this wall placement
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall))).thenReturn(false);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);
        int updatedWalls = engine.getGameState().getPlayer(PlayerId.PLAYER_1).wallsRemaining();

        assertFalse(result.isValid());
        assertEquals(currentWalls, updatedWalls);
        assertEquals(initialState, engine.getGameState());

        verify(wallValidator).canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), eq(wall));
        verifyNoMoreInteractions(wallValidator);
    }

    // 18. a player cannot place a wall when there are no walls remaining
    // - wall validator SHOULD not be even called
    @Test
    void wallPlacementNoWallsRemaining() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // Use up all walls for PLAYER_1 by manually updating the state
        GameState stateWithNoWalls = engine.getGameState()
                .withUpdatedPlayer(engine.getGameState().getPlayer(PlayerId.PLAYER_1).withWallsRemaining(0));

        // We need to simulate this - let's place 10 valid walls
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_1), any(Wall.class))).thenReturn(true);
        when(wallValidator.canPlaceWall(any(GameState.class), eq(PlayerId.PLAYER_2), any(Wall.class))).thenReturn(true);

        // Place 10 walls for P1 and 10 for P2 to exhaust P1's walls
        for (int i = 0; i < 10; i++) {
            engine.placeWall(PlayerId.PLAYER_1, new Wall(new WallPosition(i % 8, 0), WallOrientation.HORIZONTAL));
            engine.placeWall(PlayerId.PLAYER_2, new Wall(new WallPosition(i % 8, 1), WallOrientation.VERTICAL));
        }

        GameState initialState = engine.getGameState();
        assertEquals(0, initialState.getPlayer(PlayerId.PLAYER_1).wallsRemaining());

        // Try to place another wall
        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertFalse(result.isValid());
        assertEquals(initialState, engine.getGameState()); // state should not have changed

        // Validator should have been called 20 times (10 for each player), but not for the final attempt
        verify(wallValidator, times(20)).canPlaceWall(any(GameState.class), any(PlayerId.class), any(Wall.class));
    }
}

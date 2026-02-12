package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
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
        // test setup - we start considering player positions
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // make the wall positioning move
        engine.placeWall(PlayerId.PLAYER_1, wall);

        // verify the wall validator is called
        verify(wallValidator).canPlaceWall(initialState, PlayerId.PLAYER_1, wall);
        verifyNoMoreInteractions(wallValidator);

    }

    // 10. Invalid wall -> MoveResult.INVALID and unchanged state
    @Test
    void invalidWallPlacement() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return false for this wall placement
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(false);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertEquals(MoveResult.INVALID, result);
        assertSame(initialState, engine.getGameState());

        verify(wallValidator).canPlaceWall(initialState, PlayerId.PLAYER_1, wall);
        verifyNoMoreInteractions(wallValidator);
    }

    // 11. Valid wall -> OK + turn advanced
    @Test
    void validWallPlacement() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(true);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertSame(MoveResult.OK, result);

        GameState nextState = engine.getGameState();
        assertEquals(PlayerId.PLAYER_2, nextState.currentPlayerId());
        // since the move was valid and the wall placed, current game state should be updated


        verify(wallValidator).canPlaceWall(initialState, PlayerId.PLAYER_1, wall);
        verifyNoMoreInteractions(wallValidator);
    }

    // 12. Wrong player CANNOT place wall
    @Test
    void wrongTurnInvalidPlacement() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_2, wall);

        // assertions
        assertSame(MoveResult.INVALID, result);
        assertEquals(initialState, engine.getGameState());

        // wall validator SHOULD NOT be called at all
        verifyNoInteractions(wallValidator);
    }

    // 13. walls cannot be place after game ended (although with a wall we obviously cannot win :) )
    @Test
    void noWallsAfterGameOver() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        engine.endGame(PlayerId.PLAYER_1); // set a winner (thus, end the game)

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertSame(MoveResult.INVALID, result);
        assertTrue(engine.isGameOver()); // we should not change the "game over" mark

        // wall validator SHOULD NOT be called at all
        verifyNoInteractions(wallValidator);
    }

    // 14. placing valid walls should update the board
    @Test
    void boardUpdateValidWallPlacement() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(true);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertSame(MoveResult.OK, result);

        GameState nextState = engine.getGameState();
        Board newBoard = engine.getGameState().board();

        assertEquals(PlayerId.PLAYER_2, nextState.currentPlayerId());
        assertEquals(board.addWall(wall), newBoard); // new board should reflect valid wall placement

        verify(wallValidator).canPlaceWall(initialState, PlayerId.PLAYER_1, wall);
        verifyNoMoreInteractions(wallValidator);

    }

    // 16. valid wall placement should consume a wall
    @Test
    void validWallPlacementWallConsumption() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(true);

        int currentWalls = p1.wallsRemaining();
        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);
        assertEquals(MoveResult.OK, result);

        // because now the new player lives inside the new GameState
        Player updatedP1 = engine.getGameState().getPlayer(PlayerId.PLAYER_1);
        assertEquals(currentWalls - 1, updatedP1.wallsRemaining());
        // we should have removed one wall from those available to p1

        // since that now Player is immutable:
        assertNotSame(p1, updatedP1);

        verify(wallValidator).canPlaceWall(initialState, PlayerId.PLAYER_1, wall);
        verifyNoMoreInteractions(wallValidator);
    }

    // 17. invalid wall placements should not consume walls
    @Test
    void invalidWallPlacementWallConsumption() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        // return true for this wall placement
        when(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall)).thenReturn(false);

        int currentWalls = p1.wallsRemaining();
        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);
        int updatedWalls = p1.wallsRemaining();

        assertEquals(MoveResult.INVALID, result);
        assertEquals(currentWalls, updatedWalls);
        assertEquals(initialState, engine.getGameState());
        // we should have the same number of walls and the state should not have changed

        verify(wallValidator).canPlaceWall(initialState, PlayerId.PLAYER_1, wall);
        verifyNoMoreInteractions(wallValidator);
    }

    // 18. a player cannot place a wall when there are no walls remaining
    // - wall validator SHOULD not be even called
    @Test
    void wallPlacementNoWallsRemaining() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 0, 8); // p1 has no walls
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // define a new wall
        Wall wall = new Wall(new WallPosition(1, 2), WallOrientation.HORIZONTAL);

        MoveResult result = engine.placeWall(PlayerId.PLAYER_1, wall);

        // assertions
        assertEquals(MoveResult.INVALID, result);
        assertEquals(initialState, engine.getGameState()); // state should not have changed

        verifyNoInteractions(wallValidator);
    }
}

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


    // PAWN MOVES ORCHESTRATION
    // 2. When we call the engine on a move, the validator should check whether the move is valid and return the result
    // to the engine
    @Test
    void movePawnValidatorCheck() {
        // test setup
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // assume a player proposed an action: "Move Pawn EAST"
        engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);
        // to make it compile we add small movePawn methd

        // assertion
        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verifyNoMoreInteractions(pawnValidator);
    }


    // 3. we want the engine to react to the validator's outcome:
    // - simple version for MoveResult needed

    @Test
    void invalidPawnMove_unchangedState() {
        // test setup
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns false
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(false);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertEquals(MoveResult.INVALID, result); // check if the move was marked INVALID
        assertSame(initialState, engine.getGameState()); // check if current state is thus UNCHANGED

        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verifyNoMoreInteractions(pawnValidator);

    }


    // 4. Mirror case of test 3, this time we also need to handle the current game state change
    // - small changes to the engine.MovePawn needed!

    @Test
    void validPawnMove_unchangedState() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns true
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertEquals(MoveResult.OK, result); // check if the move was marked OK
        assertEquals(PlayerId.PLAYER_2, engine.getGameState().currentPlayerId()); // check if current state has CHANGED
        // for now we just check if the turn changed, not the board

        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verifyNoMoreInteractions(pawnValidator);

    }


    // 5. If some who is not a new player tries to make a move engine return MoveResult.INVALID,
    // engine state is unchanged and validator should not even be called

    @Test
    void wrongPlayerTurn() {
        // test setup
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // P2 tries to move when it's P1's turn
        MoveResult result = engine.movePawn(PlayerId.PLAYER_2, Direction.EAST);
        // need to change movePawn to reflect this case

        // assertion
        assertEquals(MoveResult.INVALID, result);
        assertSame(initialState, engine.getGameState());

        verifyNoMoreInteractions(pawnValidator);
    }

    // 6. after engine marks GameOver, every movePawn should return INVALID, state should not change and validator should
    // not be called -> after the match ends, reject all inputs (no rule checks post-game)

    @Test
    void gameOverTest() {
        // test setup
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // we need the engine to signal "game ended":
        engine.endGame(PlayerId.PLAYER_1);
        // modifications are needed in GameEngine

        // try to make a move
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertEquals(MoveResult.INVALID, result);
        assertSame(initialState, engine.getGameState());
        verifyNoInteractions(pawnValidator);
    }

    // 7. Valid pawn move can return WIN now and end the game

    @Test
    void validPawnMove_WinAchieved_GameOver() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(true);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertions
        assertEquals(MoveResult.WIN, result); // check that the move results in a win
        assertTrue(engine.isGameOver()); // check that the engine set game over
        assertEquals(PlayerId.PLAYER_1, engine.getWinner()); // check that the engine marked P1 as winner

        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verify(winChecker).isWin(any(GameState.class), eq(PlayerId.PLAYER_1));
    }


    // 8. Valid pawn move that is not winning should NOT end the game (mirrors test 7)

    @Test
    void ValidNonEndingPawnMove() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(false);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertions
        assertEquals(MoveResult.OK, result); // check that the move does not result in a win
        assertFalse(engine.isGameOver()); // check that the engine DID NOT set game over
        assertNull(engine.getWinner()); // check that the engine's winner is still null

        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verify(winChecker).isWin(any(GameState.class), eq(PlayerId.PLAYER_1));
    }

    // 15. making a valid pawn move should update the pawn position on the board
    @Test
    void ValidNonEndingPawnMoveUpdatesBoard() {
        // test setup
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);

        Position currentPosition = initialState.board().playerPosition(PlayerId.PLAYER_1); // P1 before moving
        Position desiredPosition = currentPosition.move(Direction.EAST);

        Board desiredBoardState = initialState.board().withPlayerAt(PlayerId.PLAYER_1, desiredPosition);
        GameState expectedGameState = new GameState(desiredBoardState, initialState.players(), initialState.currentPlayerIndex()).withNextTurn();

        // how the board SHOULD be after the move
        when(winChecker.isWin(expectedGameState, PlayerId.PLAYER_1)).thenReturn(false);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertios
        //assertEquals(MoveResult.OK, result); // check that the move does not result in a win
        assertFalse(engine.isGameOver()); // check that the engine DID NOT set game over
        assertNull(engine.getWinner()); // check that the engine's winner is still null
        assertEquals(desiredBoardState, engine.getGameState().board());


        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verify(winChecker).isWin(expectedGameState, PlayerId.PLAYER_1);
    }


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

}


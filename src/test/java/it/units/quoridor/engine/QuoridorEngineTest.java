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
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns true
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertEquals(MoveResult.OK, result); // check if the move was marked OK
        assertEquals(initialState.withNextTurn(), engine.getGameState()); // check if current state has CHANGED
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
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);
        when(winChecker.isWin(initialState.withNextTurn(), PlayerId.PLAYER_1)).thenReturn(true);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertios
        assertEquals(MoveResult.WIN, result); // check that the move results in a win
        assertTrue(engine.isGameOver()); // check that the engine set game over
        assertEquals(PlayerId.PLAYER_1, engine.getWinner()); // check that the engine marked P1 as winner

        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verify(winChecker).isWin(initialState.withNextTurn(), PlayerId.PLAYER_1);
    }


    // 8. Valid pawn move that is not winning should NOT end the game (mirrors test 7)

    @Test
    void ValidNonEndingPawnMove() {
        // test setup
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2)); // current player is PLAYER_1
        QuoridorEngine engine = new QuoridorEngine(initialState, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);
        when(winChecker.isWin(initialState.withNextTurn(), PlayerId.PLAYER_1)).thenReturn(false);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertios
        assertEquals(MoveResult.OK, result); // check that the move does not result in a win
        assertFalse(engine.isGameOver()); // check that the engine DID NOT set game over
        assertNull(engine.getWinner()); // check that the engine's winner is still null

        verify(pawnValidator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verify(winChecker).isWin(initialState.withNextTurn(), PlayerId.PLAYER_1);
    }
}

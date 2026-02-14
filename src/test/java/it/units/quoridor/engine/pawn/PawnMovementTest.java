package it.units.quoridor.engine.pawn;
import it.units.quoridor.domain.*;
import it.units.quoridor.engine.MoveResult;
import it.units.quoridor.engine.QuoridorEngine;
import it.units.quoridor.engine.WinChecker;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PawnMovementTest {

    @Mock
    PawnMoveValidator pawnValidator;
    @Mock
    WallPlacementValidator wallValidator;
    @Mock
    WinChecker winChecker; // will be implemented later

    // PAWN MOVES ORCHESTRATION
    // 2. When we call the engine on a move, the validator should check whether the move is valid and return the result
    // to the engine

    @Test
    void movePawnValidatorCheck() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // assume a player proposed an action: "Move Pawn EAST"
        engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        verify(pawnValidator).canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST));
        verifyNoMoreInteractions(pawnValidator);
    }

    // 3. we want the engine to react to the validator's outcome:
    // - simple version for MoveResult needed

    @Test
    void invalidPawnMove_unchangedState() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns false
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(false);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertFalse(result.isValid()); // check if the move was marked INVALID
        assertEquals(initialState, engine.getGameState()); // check if current state is thus UNCHANGED

        verify(pawnValidator).canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST));
        verifyNoMoreInteractions(pawnValidator);
    }


    // 4. Mirror case of test 3, this time we also need to handle the current game state change
    // - small changes to the engine.MovePawn needed!

    @Test
    void validPawnMove_unchangedState() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns true
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertTrue(result.isValid()); // check if the move was marked OK
        assertEquals(PlayerId.PLAYER_2, engine.getGameState().currentPlayerId()); // check if current state has CHANGED

        verify(pawnValidator).canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST));
        verifyNoMoreInteractions(pawnValidator);
    }


    // 5. If some who is not a new player tries to make a move engine return MoveResult.INVALID,
    // engine state is unchanged and validator should not even be called

    @Test
    void wrongPlayerTurn() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // P2 tries to move when it's P1's turn
        MoveResult result = engine.movePawn(PlayerId.PLAYER_2, Direction.EAST);

        // assertion
        assertFalse(result.isValid());
        assertEquals(initialState, engine.getGameState());

        verifyNoMoreInteractions(pawnValidator);
    }

    // 6. after engine marks GameOver, every movePawn should return INVALID, state should not change and validator should
    // not be called -> after the match ends, reject all inputs (no rule checks post-game)

    @Test
    void gameOverTest() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // Simulate a winning move to trigger game over
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(true);

        engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);
        assertTrue(engine.isGameOver());

        // Now try to make another move - should be rejected
        MoveResult result = engine.movePawn(PlayerId.PLAYER_2, Direction.WEST);

        // assertion
        assertFalse(result.isValid());
        // Validator should not be called for moves after game over
        verify(pawnValidator, times(1)).canMovePawn(any(GameState.class), any(PlayerId.class), any(Direction.class));
    }

    // 7. Valid pawn move can return WIN now and end the game

    @Test
    void validPawnMove_WinAchieved_GameOver() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(true);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertions
        assertTrue(result.isValid()); // check that the move results in a win
        assertTrue(engine.isGameOver()); // check that the engine set game over
        assertEquals(PlayerId.PLAYER_1, engine.getWinner()); // check that the engine marked P1 as winner

        verify(pawnValidator).canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST));
        verify(winChecker).isWin(any(GameState.class), eq(PlayerId.PLAYER_1));
    }


    // 8. Valid pawn move that is not winning should NOT end the game (mirrors test 7)

    @Test
    void ValidNonEndingPawnMove() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(false);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertions
        assertTrue(result.isValid()); // check that the move does not result in a win
        assertFalse(engine.isGameOver()); // check that the engine DID NOT set game over
        assertNull(engine.getWinner()); // check that the engine's winner is still null

        verify(pawnValidator).canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST));
        verify(winChecker).isWin(any(GameState.class), eq(PlayerId.PLAYER_1));
    }

    // 15. making a valid pawn move should update the pawn position on the board
    @Test
    void ValidNonEndingPawnMoveUpdatesBoard() {
        // Create engine with rules
        GameRules rules = new QuoridorGameRules();
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        GameState initialState = engine.getGameState();

        // the validator should just check the validity of the move, the winChecker whether that move leads to a win
        when(pawnValidator.canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST))).thenReturn(true);
        when(winChecker.isWin(any(GameState.class), eq(PlayerId.PLAYER_1))).thenReturn(false);

        Position currentPosition = initialState.board().playerPosition(PlayerId.PLAYER_1); // P1 before moving
        Position expectedPosition = currentPosition.move(Direction.EAST);

        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertions
        assertFalse(engine.isGameOver()); // check that the engine DID NOT set game over
        assertNull(engine.getWinner()); // check that the engine's winner is still null

        // Verify the player position was updated
        assertEquals(expectedPosition, engine.getGameState().board().playerPosition(PlayerId.PLAYER_1));

        verify(pawnValidator).canMovePawn(any(GameState.class), eq(PlayerId.PLAYER_1), eq(Direction.EAST));
        verify(winChecker).isWin(any(GameState.class), eq(PlayerId.PLAYER_1));
    }
}

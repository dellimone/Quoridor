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
public class GameEngineTest {

    @Mock
    ActionValidator validator;

    // 1. we want to test whether the GameEngine handles the board correctly -> initial state set correctly
    @Test
    void engineExposesInitialGameState() {
        // create a small example for board
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));
        GameEngine engine = new GameEngine(initialState, validator);

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
        GameEngine engine = new GameEngine(initialState, validator);

        // assume a player proposed an action: "Move Pawn EAST"
        engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);
        // to make it compile we add small movePawn methd

        // assertion
        verify(validator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verifyNoMoreInteractions(validator);
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
        GameEngine engine = new GameEngine(initialState, validator);

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns false
        when(validator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(false);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertEquals(MoveResult.INVALID, result); // check if the move was marked INVALID
        assertSame(initialState, engine.getGameState()); // check if current state is thus UNCHANGED

        verify(validator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verifyNoMoreInteractions(validator);

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
        GameEngine engine = new GameEngine(initialState, validator);

        // now for the Mockito setup: when we ask if moving EAST was valid, validator returns true
        when(validator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST)).thenReturn(true);

        // result from engine (MovePawn is subsequently changed for the test to pass)
        MoveResult result = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);

        // assertion
        assertEquals(MoveResult.OK, result); // check if the move was marked OK
        assertEquals(initialState.withNextTurn(), engine.getGameState()); // check if current state has CHANGED
        // for now we just check if the turn changed, not the board

        verify(validator).canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST);
        verifyNoMoreInteractions(validator);

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
        GameEngine engine = new GameEngine(initialState, validator);

        // P2 tries to move when it's P1's turn
        MoveResult result = engine.movePawn(PlayerId.PLAYER_2, Direction.EAST);
        // need to change movePawn to reflect this case

        // assertion
        assertEquals(MoveResult.INVALID, result);
        assertSame(initialState, engine.getGameState());

        verifyNoMoreInteractions(validator);

    }
}

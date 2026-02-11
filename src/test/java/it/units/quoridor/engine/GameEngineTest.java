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


}

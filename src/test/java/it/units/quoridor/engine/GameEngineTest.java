package it.units.quoridor.engine;

import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    // 1. we want to test whether the GameEngine handles the board correctly
    @Test
    void engineExposesInitialGameState() {
        // create a small example for board
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));

        ActionValidator validator = (s, p, d) -> false;
        GameEngine engine = new GameEngine(initialState, validator);

        GameState actual = engine.getGameState();

        // if the validator returns false the engine should not change state
        assertSame(initialState, actual);
    }

}

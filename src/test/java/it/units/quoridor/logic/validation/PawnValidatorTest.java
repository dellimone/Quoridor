package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.QuoridorEngine;
import it.units.quoridor.engine.WinChecker;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import it.units.quoridor.logic.rules.validation.PawnMoveValidator;
import it.units.quoridor.logic.rules.validation.RulesPawnMoveValidator;
import it.units.quoridor.logic.rules.validation.WallPlacementValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PawnValidatorTest {

    private final QuoridorGameRules rules = new QuoridorGameRules();
    private final PawnMoveValidator pawnValidator = new RulesPawnMoveValidator();

    // 1. returns true when proposed square is free
    @Test
    void returnsTrue_freeSquare() {
        // create a small example for board
        Board board = new Board();
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST));
    }
}

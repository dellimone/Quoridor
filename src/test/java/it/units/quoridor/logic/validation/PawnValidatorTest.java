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

    // 1. returns true when proposed square is free from walls
    @Test
    void returnsTrue_freeSquareFromWalls() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST));
    }

    // 2. returns true when proposed square is blocked by walls
    @Test
    void returnsFalse_occupiedSquareByWall() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        WallPosition wallPosition = new WallPosition(0,4);
        Wall wall = new Wall(wallPosition, WallOrientation.VERTICAL);
        Board newBoard = initialState.board().addWall(wall);
        GameState currentState = new GameState(newBoard, List.of(p1, p2));

        assertFalse(pawnValidator.canMovePawn(currentState, PlayerId.PLAYER_1, Direction.EAST));
    }
}

package it.units.quoridor.logic.validation;
import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.QuoridorEngine;
import it.units.quoridor.engine.WinChecker;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import it.units.quoridor.logic.rules.validation.PawnMoveValidator;
import it.units.quoridor.logic.rules.validation.RulesPawnMoveValidator;
import it.units.quoridor.logic.rules.validation.RulesWallPlacementValidator;
import it.units.quoridor.logic.rules.validation.WallPlacementValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class WallValidatorTest {
    private final QuoridorGameRules rules = new QuoridorGameRules();
    private final BfsPathFinder pathFinder = new BfsPathFinder();
    private final WallPlacementValidator wallValidator = new RulesWallPlacementValidator(rules, pathFinder);

    // 1. legal placement on empty board: placing a wall in the middle should return true
    @Test
    void legalWallPlacement() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        WallPosition wallPosition = new WallPosition(1,5);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertTrue(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, wall));
    }

    // 2. validator returns false when two walls overlap
    @Test
    void overlappingWalls_returnsFalse() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        WallPosition wallPosition = new WallPosition(3,3);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        Board board = new Board()
                .addWall(wall)
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        WallPosition newWallPosition = new WallPosition(3,4);
        Wall newWall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        assertFalse(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, newWall));
    }

    // 3. validator returns false if two walls cross, meaning they have same anchor but opposite orientation
    @Test
    void crossingWalls_returnFalse() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        WallPosition wallPosition = new WallPosition(3,3);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        Board board = new Board()
                .addWall(wall)
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        WallPosition newWallPosition = new WallPosition(3,3);
        Wall newWall = new Wall(wallPosition, WallOrientation.VERTICAL);

        assertFalse(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, newWall));
    }
}

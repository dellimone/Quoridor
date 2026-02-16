package it.units.quoridor.logic.validation;
import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)

public class WallValidatorTest {
    private final QuoridorGameRules rules = new QuoridorGameRules();
    private final BfsPathFinder pathFinder = new BfsPathFinder();
    private final WallPlacementValidator wallValidator = new RulesWallPlacementValidator(rules, pathFinder);

    // 1. legal placement on empty board: placing a wall in the middle should return true
    @Test
    void legalWallPlacement() {
        GameState initialState = standardState();
        assertTrue(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, hWall(1, 5)));
    }

    // 2. validator returns false when two walls overlap
    @Test
    void overlappingWalls_returnsFalse() {
        Wall wall = hWall(3, 3);

        Board board = standardBoard().addWall(wall);
        GameState initialState = stateWith(board);

        // same position, same orientation — overlap
        Wall newWall = hWall(3, 3);

        assertFalse(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, newWall));
    }

    // 3. validator returns false if two walls cross, meaning they have same anchor but opposite orientation
    @Test
    void crossingWalls_returnFalse() {
        Wall wall = hWall(3, 3);

        Board board = standardBoard().addWall(wall);
        GameState initialState = stateWith(board);

        // same anchor, perpendicular orientation — crossing
        Wall newWall = vWall(3, 3);

        assertFalse(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, newWall));
    }

    // 4. validator returns false if the new wall blocks all paths (leveraging BFS)
    @Test
    void wallBlockingAllPaths_returnFalse() {
        Board board = standardBoard();

        for (int c = 0; c <= 6; c++) {
            board = board.addWall(hWall(3, c));
        }

        GameState initialState = stateWith(board);

        // this should block all paths
        assertFalse(wallValidator.canPlaceWall(initialState, PlayerId.PLAYER_1, hWall(3, 7)));
    }

}

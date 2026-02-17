package it.units.quoridor.engine;
import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;


import it.units.quoridor.logic.rules.QuoridorWinChecker;
import it.units.quoridor.logic.rules.WinChecker;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.QuoridorPawnMoveValidator;
import it.units.quoridor.logic.validation.QuoridorWallPlacementValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Set;

import static it.units.quoridor.TestFixtures.hWall;
import static it.units.quoridor.TestFixtures.vWall;
import static it.units.quoridor.TestFixtures.stateWith;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)

public class QuoridorEngineIntegrationTest {

    private static void assertAllInBounds(Set<Position> positions) {
        for (Position p : positions) {
            assertTrue(p.row() >= 0 && p.row() <= 8, "row out of bounds: " + p);
            assertTrue(p.col() >= 0 && p.col() <= 8, "col out of bounds: " + p);
        }
    }
    GameRules rules = new QuoridorGameRules();
    PathFinder pathFinder = new BfsPathFinder();

    PawnMoveValidator pawnValidator = new QuoridorPawnMoveValidator();
    WallPlacementValidator wallValidator = new QuoridorWallPlacementValidator(rules, pathFinder);
    WinChecker winChecker = new QuoridorWinChecker(rules);

    // 1. legal pawn destinations does not throw
    @Test
    void legalPawnDestinations_noThrows() {
        QuoridorEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);

        assertDoesNotThrow(() -> engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1));

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        assertAllInBounds(dest);
    }

    // 2. legal pawn destinations does not throw in corners
    @Test
    void legalPawnDestinations_noThrows_corners() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 0))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState cornerState = stateWith(board);

        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, cornerState);

        assertDoesNotThrow(() -> engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1));

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        assertAllInBounds(dest);
    }

    // 3. legal pawn movements should include "normal" steps
    @Test
    void legalPawnDestinations_includesNormalSteps() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        assertTrue(dest.contains(new Position(3, 4)));
        assertTrue(dest.contains(new Position(1, 4)));
        assertTrue(dest.contains(new Position(2, 3)));
        assertTrue(dest.contains(new Position(2, 5)));
    }

    // 4. valid step moves should update the state
    @Test
    void validMove_updateState() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));
        GameState state = stateWith(board);

        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        MoveResult res = engine.movePawn(PlayerId.PLAYER_1, new Position(2,5));
        assertTrue(res.isValid());

        assertEquals(new Position(2, 5), engine.gameState().playerPosition(PlayerId.PLAYER_1));
    }

    // 5. legalPawnDestinations should include jumps when available
    @Test
    void legalPawnDestinations_jumpAvailable() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4)); // adjacent SOUTH to enbale the jump

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        assertTrue(dest.contains(new Position(0, 4))); // can jump over pawn which is in front but:
        assertFalse(dest.contains(new Position(1, 4))); // cannot overlap pawn
    }

    // 6. diagonal jump has to become a possibility when straight jump is not accessible
    @Test
    void diagonalMoveAvailable_JumpNotAccessible() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4)) // adjacent SOUTH
                .withPlayerAt(PlayerId.PLAYER_3, new Position(0, 4)); // jump square occupied by another player

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        assertFalse(dest.contains(new Position(0, 4)));

        // diagonals around the adjacent pawn should become legal
        assertTrue(dest.contains(new Position(1, 3)));
        assertTrue(dest.contains(new Position(1, 5)));
    }

    // mirror test but with a wall
    @Test
    void diagonalMoveAvailable_JumpNotAccessibleWallCase() {
       Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4)) // adjacent SOUTH
                .addWall(hWall(0, 4)); // jump square occupied by another player

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        assertFalse(dest.contains(new Position(0, 4)));

        // diagonals around the adjacent pawn should become legal
        assertTrue(dest.contains(new Position(1, 3)));
        assertTrue(dest.contains(new Position(1, 5)));
    }

    // 7. diagonal moves non available when jumping straight is
    @Test
    void diagonalMoveNotAvailable_JumpAccessible() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4)); // adjacent SOUTH

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        assertTrue(dest.contains(new Position(0, 4)));

        // diagonals around the adjacent pawn should become not legal since we have jump
        assertFalse(dest.contains(new Position(1, 3)));
        assertFalse(dest.contains(new Position(1, 5)));
    }

    // 8. diagonal legal moves should update the state
    @Test
    void diagonalMoveAvailable_updateState() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4)) // adjacent SOUTH
                .addWall(hWall(0, 4)); // jump square occupied by wall

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        MoveResult res = engine.movePawn(PlayerId.PLAYER_1, new Position(1, 3));
        assertTrue(res.isValid());

        assertEquals(new Position(1, 3), engine.gameState().playerPosition(PlayerId.PLAYER_1));
    }


    // 9. standard one-step in a cardinal direction tests
    @Test
    void standardOneStepTest_bothPlayers() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(6, 5))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(3, 0))
                .addWall(vWall(3, 0))
                .addWall(vWall(4, 5))
                .addWall(hWall(6, 4));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        Set<Position> destP2 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_2);

        //legal moves for player1
        assertTrue(destP1.contains(new Position(6, 4)));
        assertTrue(destP1.contains(new Position(5, 5)));

        // legal moves for player2
        assertTrue(destP2.contains(new Position(4, 0)));
        assertTrue(destP2.contains(new Position(2, 0)));
    }

    // 10. straight and diagonal jump cases
    @Test
    void straightAndDiagonal_JumpCases_bothPlayers() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(6, 5))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(5, 5))
                .addWall(hWall(6, 4));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        Set<Position> destP2 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_2);

        // legal moves for player1
        assertTrue(destP1.contains(new Position(6, 4)));
        assertTrue(destP1.contains(new Position(6, 6)));
        assertTrue(destP1.contains(new Position(4, 5)));

        // legal moves for player2
        assertTrue(destP2.contains(new Position(6, 4)));
        assertTrue(destP2.contains(new Position(6, 6)));
        assertTrue(destP2.contains(new Position(5, 4)));
        assertTrue(destP2.contains(new Position(5, 6)));
        assertTrue(destP1.contains(new Position(4, 5)));
    }

    // 11. edge cases for diagonals and jump for player 1
    @Test
    void straightAndDiagonalJumps_playerOneCases() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(4, 3))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(4, 4))
                .addWall(hWall(5, 4));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        // legal moves for player1
        assertTrue(destP1.contains(new Position(5, 3)));
        assertTrue(destP1.contains(new Position(3, 3)));
        assertTrue(destP1.contains(new Position(4, 2)));
        assertTrue(destP1.contains(new Position(4, 5)));
    }

    // edge case in which we disable the jump and thus enable the diagonal by putting a wall behind
    @Test
    void straightAndDiagonalJumps_playerOneEdgeCases() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(4, 3))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(4, 4))
                .addWall(hWall(5, 4))
                .addWall(vWall(4, 4));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);


        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        // legal moves for player1
        assertTrue(destP1.contains(new Position(5, 3)));
        assertTrue(destP1.contains(new Position(3, 3)));
        assertTrue(destP1.contains(new Position(4, 2)));
        assertTrue(destP1.contains(new Position(3, 4)));
    }

    // 12. verify that:
    /*
    - UI-style highlight query works
    - engine enforces turn
    - engine updates immutable state
    - turn changes after move
     */
    @Test
    void integration_cornerMove_executesAndTurnAdvances() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 0))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 8));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        assertEquals(Set.of(new Position(1, 0), new Position(0, 1)), destP1);

        // execute a valid move
        MoveResult r = engine.movePawn(PlayerId.PLAYER_1, new Position(0, 1));
        assertTrue(r.isValid()); // adapt if your MoveResult API differs

        // state updated
        GameState after = engine.gameState();
        assertEquals(new Position(0, 1), after.playerPosition(PlayerId.PLAYER_1));

        // and finally assert that the turn advanced to P2
        assertEquals(PlayerId.PLAYER_2, after.currentPlayerId());
    }

    // 13. available straight jump
    @Test
    void integration_straightJump_executesCorrectLanding() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(4, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(5, 4)); // directly NORTH of P1

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        // jump landing should be legal from (4,4) to (6,4)
        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        assertTrue(destP1.contains(new Position(6, 4)));

        MoveResult r = engine.movePawn(PlayerId.PLAYER_1, new Position(6, 4));
        assertTrue(r.isValid());

        GameState after = engine.gameState();
        assertEquals(new Position(6, 4), after.playerPosition(PlayerId.PLAYER_1));
        assertEquals(PlayerId.PLAYER_2, after.currentPlayerId());
    }

    // from the plays.md

    // Play #23
    /**
     * Setup: A at (0, 0),
     * Valid moves: SOUTH, EAST only,
     * Invalid: NORTH (edge), WEST (edge)
     */
    @Test
    void cornerMovement_playerAt00_onlyTwoLegalDestinations() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 0))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 8));

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        // only two legal moves from the corner (no walls):
        assertTrue(destP1.contains(new Position(1, 0)));
        assertTrue(destP1.contains(new Position(0, 1)));

        // and nothing else
        assertEquals(Set.of(new Position(1, 0), new Position(0, 1)), destP1);
    }


    // Play #24
    /**
     * Setup:
     * - A at (4, 4)
     * - B at (3, 4)
     * - C at (4, 5),
     * Action: A moves toward B,
     * Result: Normal jump rules apply to B,
     */
    @Test
    void threePawnsAdjacent_jumpTowardB_isStillAvailable() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(4, 4)) // A
                .withPlayerAt(PlayerId.PLAYER_2, new Position(3, 4)) // B (toward direction)
                .withPlayerAt(PlayerId.PLAYER_3, new Position(4, 5)) // C (adjacent, irrelevant for A->B)
                .withPlayerAt(PlayerId.PLAYER_4, new Position(8, 8)); // somewhere else

        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> destA = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        // A should be able to jump over B to the square behind B (normal jump rule)
        assertTrue(destA.contains(new Position(2, 4)));
    }

}

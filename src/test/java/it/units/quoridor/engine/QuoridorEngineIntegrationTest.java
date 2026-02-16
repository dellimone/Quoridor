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
                .addWall(new Wall(new WallPosition(0, 4), WallOrientation.HORIZONTAL)); // jump square occupied by another player

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
                .addWall(new Wall(new WallPosition(0, 4), WallOrientation.HORIZONTAL)); // jump square occupied by another player

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
                .addWall(new Wall(new WallPosition(3, 0), WallOrientation.VERTICAL))
                .addWall(new Wall(new WallPosition(4, 5), WallOrientation.VERTICAL))
                .addWall(new Wall(new WallPosition(6, 4), WallOrientation.HORIZONTAL));


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
                .addWall(new Wall(new WallPosition(6, 4), WallOrientation.HORIZONTAL));


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
                .addWall(new Wall(new WallPosition(5, 4), WallOrientation.HORIZONTAL));

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
                .addWall(new Wall(new WallPosition(5, 4), WallOrientation.HORIZONTAL))
                .addWall(new Wall(new WallPosition(4, 4), WallOrientation.VERTICAL));


        GameState state = stateWith(board);
        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);


        Set<Position> destP1 = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        // legal moves for player1
        assertTrue(destP1.contains(new Position(5, 3)));
        assertTrue(destP1.contains(new Position(3, 3)));
        assertTrue(destP1.contains(new Position(4, 2)));
        assertTrue(destP1.contains(new Position(3, 4)));
    }
}

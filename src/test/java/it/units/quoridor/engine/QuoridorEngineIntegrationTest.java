package it.units.quoridor.engine;
import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;


import it.units.quoridor.logic.rules.QuoridorWinChecker;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.RulesPawnMoveValidator;
import it.units.quoridor.logic.validation.RulesWallPlacementValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    PawnMoveValidator pawnValidator = new RulesPawnMoveValidator();
    WallPlacementValidator wallValidator = new RulesWallPlacementValidator(rules, pathFinder);
    WinChecker winChecker = new QuoridorWinChecker();

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
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 0))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState cornerState = new GameState(board, List.of(p1, p2));

        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, cornerState);

        assertDoesNotThrow(() -> engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1));

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);
        assertAllInBounds(dest);
    }

    // 3. legal pawn movements should include "normal" steps
    @Test
    void legalPawnDestinations_includesNormalSteps() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState state = new GameState(board, List.of(p1, p2));

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
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState state = new GameState(board, List.of(p1, p2));

        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        MoveResult res = engine.movePawn(PlayerId.PLAYER_1, Direction.EAST);
        assertTrue(res.isValid());

        assertEquals(new Position(2, 5), engine.getGameState().getPlayerPosition(PlayerId.PLAYER_1));
    }

    // 5. legalPawnDestinations should include jumps when available
    @Test
    void legalPawnDestinations_jumpAvailable() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4)); // adjacent SOUTH to enbale the jump

        GameState state = new GameState(board, List.of(p1, p2));

        QuoridorEngine engine = QuoridorEngine.forTesting(rules, pawnValidator, wallValidator, winChecker, state);

        Set<Position> dest = engine.legalPawnDestinationsForPlayer(PlayerId.PLAYER_1);

        assertTrue(dest.contains(new Position(0, 4))); // can jump over pawn which is in front but:
        assertFalse(dest.contains(new Position(1, 4))); // cannot overlap pawn
    }

    // 6. diagonal jump has to become a possibility when straight jump is not accessible
}

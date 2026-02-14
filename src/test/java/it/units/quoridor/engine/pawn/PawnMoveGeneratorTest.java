package it.units.quoridor.engine.pawn;
import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;


import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.RulesPawnMoveValidator;
import it.units.quoridor.engine.moves.PawnMoveGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PawnMoveGeneratorTest {

    // unit tests
    // 0. ensure generator obeys the validator
    @Test
    void noDestinations_whenValidatorRejects() {
        PawnMoveValidator mockValidator = mock(PawnMoveValidator.class);
        when(mockValidator.canMovePawn(any(), any(), any())).thenReturn(false);

        PawnMoveGenerator generator = new PawnMoveGenerator(mockValidator);

        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        Optional<Position> result =
                generator.resolveDestination(initialState, PlayerId.PLAYER_1, Direction.EAST);

        assertTrue(result.isEmpty());
    }


    // actual integration tests: generator + validator
    private final PawnMoveValidator validator = new RulesPawnMoveValidator();
    private final PawnMoveGenerator generator = new PawnMoveGenerator(validator);

    // 1. if we have a "normal" adjacent move, resolveDestination returns the adjacent square
    @Test
    void returnsAdjacentDestination_onNormalMove() {

        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        Optional<Position> result = generator.resolveDestination(initialState, PlayerId.PLAYER_1, Direction.EAST);

        assertTrue(result.isPresent()); // assert that the generator actually generated a position
        assertEquals(new Position(0, 5), result.get()); // assert that we actually moved EAST from (0,4) -> (0,5)
    }

    // 2. when the move is blocked by a wall, generator should return empty
    @Test void generatorReturnsEmpty_directionBlockedByWall() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .addWall(new Wall(new WallPosition(0,4), WallOrientation.VERTICAL))
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        Optional<Position> result = generator.resolveDestination(initialState, PlayerId.PLAYER_1, Direction.EAST);

        assertTrue(result.isEmpty()); // assert that the generator found no valid positions
    }

    // 3. jump logic: if the validator allows the jump, then the generator should return the square BEHIND
    @Test
    void generatorReturnsResult_jumpAllowed() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 5));

        GameState initialState = new GameState(board, List.of(p1, p2));

        Optional<Position> result = generator.resolveDestination(initialState, PlayerId.PLAYER_1, Direction.EAST);

        assertTrue(result.isPresent()); // assert that the generator actually generated a position
        assertEquals(new Position(0, 6), result.get()); // assert that we actually jumped EAST from (0,4) -> (0,6)
    }

    // 4. generator returns nothing if the pawn tries to go out of bounds
    @Test
    void generatorReturnsEmpty_outOfBoundsMove() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 5));

        GameState initialState = new GameState(board, List.of(p1, p2));

        Optional<Position> result = generator.resolveDestination(initialState, PlayerId.PLAYER_1, Direction.SOUTH);

        assertTrue(result.isEmpty()); // assert that the generator actually generated a position
    }

    // 5. generator returns nothing if the pawn tries to JUMP out of bounds
    @Test
    void generatorReturnsEmpty_jumpOutOfBounds() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10, 8);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10, 0);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 7))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 8));

        GameState initialState = new GameState(board, List.of(p1, p2));

        Optional<Position> result = generator.resolveDestination(initialState, PlayerId.PLAYER_1, Direction.EAST);

        assertTrue(result.isEmpty()); // assert that the generator actually generated a position
        }
}

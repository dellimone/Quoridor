package it.units.quoridor.engine.pawn;
import it.units.quoridor.domain.*;

import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.QuoridorPawnMoveValidator;
import it.units.quoridor.engine.moves.PawnMoveGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PawnMoveGeneratorTest {

    // unit test: generator obeys the validator
    @Test
    void noDestinations_whenValidatorRejectsAll() {
        PawnMoveValidator mockValidator = mock(PawnMoveValidator.class);
        when(mockValidator.canMovePawn(any(GameState.class), any(PlayerId.class), any(Position.class)))
                .thenReturn(false);

        PawnMoveGenerator generator = new PawnMoveGenerator(mockValidator);

        GameState initialState = standardState();

        Set<Position> result = generator.legalDestinations(initialState, PlayerId.PLAYER_1);

        assertTrue(result.isEmpty());
    }


    // integration tests: generator + real validator
    private final PawnMoveValidator validator = new QuoridorPawnMoveValidator();
    private final PawnMoveGenerator generator = new PawnMoveGenerator(validator);

    // 1. adjacent move appears in legal destinations
    @Test
    void adjacentMoveIncludedInLegalDestinations() {
        GameState initialState = standardState();

        Set<Position> result = generator.legalDestinations(initialState, PlayerId.PLAYER_1);

        assertTrue(result.contains(new Position(0, 5))); // EAST
        assertTrue(result.contains(new Position(0, 3))); // WEST
        assertTrue(result.contains(new Position(1, 4))); // NORTH
        assertEquals(3, result.size()); // SOUTH is off-board (row 0), so only 3 destinations
    }

    // 2. wall-blocked direction excluded from legal destinations
    @Test
    void wallBlockedDirectionExcluded() {
        Board board = standardBoard().addWall(vWall(0, 4));

        GameState initialState = stateWith(board);

        Set<Position> result = generator.legalDestinations(initialState, PlayerId.PLAYER_1);

        assertFalse(result.contains(new Position(0, 5))); // EAST blocked by wall
    }

    // 3. jump: when adjacent to opponent with clear path behind, jump destination is included
    @Test
    void jumpDestinationIncluded() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 5));

        GameState initialState = stateWith(board);

        Set<Position> result = generator.legalDestinations(initialState, PlayerId.PLAYER_1);

        assertTrue(result.contains(new Position(0, 6))); // jump over P2
        assertFalse(result.contains(new Position(0, 5))); // P2's cell is occupied
    }

    // 4. out-of-bounds positions never appear
    @Test
    void outOfBoundsExcluded() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 5));

        GameState initialState = stateWith(board);

        Set<Position> result = generator.legalDestinations(initialState, PlayerId.PLAYER_1);

        // no position should have row < 0
        for (Position p : result) {
            assertTrue(p.row() >= 0 && p.row() <= 8);
            assertTrue(p.col() >= 0 && p.col() <= 8);
        }
    }

    // 5. jump out of bounds: opponent at board edge, jump would go off-board
    @Test
    void jumpOutOfBoundsExcluded() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 7))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 8));

        GameState initialState = stateWith(board);

        Set<Position> result = generator.legalDestinations(initialState, PlayerId.PLAYER_1);

        // (0, 9) doesn't exist, so straight jump EAST should not appear
        assertFalse(result.contains(new Position(0, 8))); // occupied by P2
    }
}

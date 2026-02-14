package it.units.quoridor.engine;
import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;


import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import it.units.quoridor.engine.moves.PawnMoveGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PawnMoveGeneratorTest {

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

}

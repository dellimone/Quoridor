package it.units.quoridor.engine.pawn;
import it.units.quoridor.domain.*;


import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.QuoridorEngine;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.QuoridorPawnMoveValidator;
import it.units.quoridor.engine.moves.PawnMoveGenerator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PawnMovementIntegratedTest {

    private final PawnMoveValidator pawnValidator = new QuoridorPawnMoveValidator();
    private final PawnMoveGenerator pawnMoveGenerator = new PawnMoveGenerator(pawnValidator);
    private final QuoridorGameRules rules = new QuoridorGameRules();

    // do not need real wall validators and win checkers for now
    @Mock
    WallPlacementValidator wallValidator;

    @Mock
    WinChecker winChecker;


    // 1. engine applies normal move
    @Test
    void engineAppliesAdjacentMove_ifAvailable() {

        GameEngine engine = new QuoridorEngine(rules, pawnValidator, wallValidator, winChecker);
        engine.movePawn(PlayerId.PLAYER_1, new Position(0, 5));

        assertEquals(new Position(0, 5), engine.gameState().playerPosition(PlayerId.PLAYER_1));
    }

}

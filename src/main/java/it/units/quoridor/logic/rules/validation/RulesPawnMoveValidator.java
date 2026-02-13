package it.units.quoridor.logic.rules.validation;

import it.units.quoridor.domain.Direction;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.domain.PlayerId;

public class RulesPawnMoveValidator implements PawnMoveValidator{

    public boolean canMovePawn(GameState state, PlayerId player, Direction direction) {
        return true;
    }
}

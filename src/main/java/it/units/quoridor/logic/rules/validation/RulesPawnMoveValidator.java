package it.units.quoridor.logic.rules.validation;

import it.units.quoridor.domain.*;

public class RulesPawnMoveValidator implements PawnMoveValidator{

    public boolean canMovePawn(GameState state, PlayerId player, Direction direction) {

        Board currentBoard = state.board();
        Position currentPosition = state.getPlayerPosition(player);
        Position proposedPosition = currentPosition.move(direction);

        // we have to return true if the square in the proposed direction is free (from walls and players)
        if (currentBoard.isEdgeBlocked(currentPosition, direction)) {
            return false;
        }

        return true;
    }
}

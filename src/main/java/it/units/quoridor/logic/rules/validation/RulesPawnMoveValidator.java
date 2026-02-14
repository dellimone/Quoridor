package it.units.quoridor.logic.rules.validation;

import it.units.quoridor.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RulesPawnMoveValidator implements PawnMoveValidator{

    public boolean canMovePawn(GameState state, PlayerId player, Direction direction) {

        Board currentBoard = state.board();
        Position currentPosition = state.getPlayerPosition(player);

        // we encode all the previous if statement:
        // try to move the pawn -> first check if the move is valid (inside the board and not blocked by a wall)
        // then allow if the target square is empty
        // otherwise allow if jump is possible
        // if any step fails -> false
        return currentPosition.tryMove(direction)
                .filter(to -> !currentBoard.isEdgeBlocked(currentPosition, direction))
                .map(to -> currentBoard.occupantAt(to).isEmpty() || canJump(currentBoard, to, direction))
                .orElse(false);
    }


    boolean canJump(Board currentBoard, Position proposedPosition, Direction direction) {
        if (currentBoard.isEdgeBlocked(proposedPosition, direction)) {
            return false; // no jump
        }

        // try to see if the behind position is free from walls and players
        Optional<Position> maybeBehindPosition = proposedPosition.tryMove(direction);
        if (maybeBehindPosition.isEmpty()) { return false; }

        // - if behind the player there is another player
        Position behindPosition = maybeBehindPosition.get();
        return currentBoard.occupantAt(behindPosition).isEmpty();
    }

}

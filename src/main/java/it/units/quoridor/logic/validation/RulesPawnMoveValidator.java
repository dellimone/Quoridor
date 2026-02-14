package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;

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


    boolean canJump(Board board, Position occupiedAdj, Direction dir) {
        // behind square must exist
        Optional<Position> maybeBehind = occupiedAdj.tryMove(dir);
        if (maybeBehind.isEmpty()) return false;

        Position behind = maybeBehind.get();

        // the edge between occupiedAdj <-> behind must not be blocked
        if (board.isEdgeBlocked(occupiedAdj, dir) || board.isEdgeBlocked(behind, dir.opposite())) {
            return false;
        }

        // behind must be free
        return board.occupantAt(behind).isEmpty();
    }


}

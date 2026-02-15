package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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

    @Override
    public boolean canMovePawn(GameState state, PlayerId player, Position target) {
        Board board = state.board();
        Position from = state.getPlayerPosition(player);

        int dr = target.row() - from.row();
        int dc = target.col() - from.col();
        int manhattan = Math.abs(dr) + Math.abs(dc);

        return switch (manhattan) {
            // if manhattan distance is 1, then we are stepping in one of the cardinal directions
            case 1 -> Direction.fromUnitDelta(dr, dc)
                    .map(dir -> canStep(board, from, dir))
                    .orElse(false);

            // if target has MD of 2, we can either jump or diagonal
            case 2 -> canDistance2(board, from, dr, dc, target);

            default -> false;
        };
    }

    // logic for stepping in one of the cardinal directions: move only if it's free from walls and players
    private boolean canStep(Board board, Position from, Direction dir) {
        return from.tryMove(dir)
                .filter(to -> !board.isEdgeBlocked(from, dir))
                .filter(to -> board.occupantAt(to).isEmpty())
                .isPresent();
    }

    // logic for one of the two cases in which MD is 2 -> straight jump
    private boolean canStraightJump(Board board, Position from, Direction dir) {
        return from.tryMove(dir)
                .filter(adj -> !board.isEdgeBlocked(from, dir))
                .filter(adj -> board.occupantAt(adj).isPresent())
                .filter(adj -> canJump(board, adj, dir)) // your existing helper
                .isPresent();
    }

    // another case for MD=2 for later (diagonal jump)
    private boolean canDiagonalJump(Board board, Position from, Position target, int stepDr, int stepDc) {

        if (board.occupantAt(target).isPresent()) return false;

        return Direction.fromUnitDelta(stepDr, 0)
                .flatMap(front -> from.tryMove(front)
                        .filter(adj -> !board.isEdgeBlocked(from, front))
                        .filter(adj -> board.occupantAt(adj).isPresent())
                        .filter(adj -> !canStraightJump(board, from, front))
                        .flatMap(adj -> Direction.fromUnitDelta(0, stepDc)
                                .flatMap(side -> adj.tryMove(side)
                                        .filter(p -> p.equals(target))
                                        .filter(p -> !board.isEdgeBlocked(adj, side))
                                )
                        )
                )
                .isPresent();
    }


    private boolean canDistance2(Board board, Position from, int dr, int dc, Position target) {
        int stepDr = Integer.signum(dr);
        int stepDc = Integer.signum(dc);

        // straight jump target: (±2,0) or (0,±2)
        if ((Math.abs(dr) == 2) ^ (Math.abs(dc) == 2)) { // XOR: exactly one axis has 2
            return Direction.fromUnitDelta(stepDr, stepDc)
                    .map(dir -> canStraightJump(board, from, dir))
                    .orElse(false);
        }

        // diagonal target: (±1,±1) -> implement later
        if (Math.abs(dr) == 1 && Math.abs(dc) == 1) {
            return canDiagonalJump(board, from, target, stepDr, stepDc); // placeholder for now
        }

        return false;
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

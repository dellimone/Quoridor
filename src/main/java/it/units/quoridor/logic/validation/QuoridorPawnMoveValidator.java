package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;

import java.util.Optional;

/**
 * Validates pawn moves by dispatching on Manhattan distance to the target:
 *
 * <ul>
 *   <li><b>MD=1</b> — simple step: one square orthogonally, target must be empty and no wall between.</li>
 *   <li><b>MD=2, one axis</b> — straight jump: an opponent sits on the adjacent square and the
 *       landing square behind them is clear (no wall, no board edge).</li>
 *   <li><b>MD=2, both axes</b> — diagonal jump: an opponent is adjacent but a straight jump over
 *       them is blocked (wall or board edge behind), so the pawn sidesteps diagonally.</li>
 * </ul>
 *
 * Manhattan distance is the dispatch key because it cleanly separates all three
 * movement types without needing to inspect opponent positions first.
 */
public class QuoridorPawnMoveValidator implements PawnMoveValidator {

    public boolean canMovePawn(GameState state, PlayerId player, Direction direction) {

        Board currentBoard = state.board();
        Position currentPosition = state.playerPosition(player);

        return currentPosition.tryMove(direction)
                .filter(to -> !currentBoard.isEdgeBlocked(currentPosition, direction))
                .map(to -> currentBoard.occupantAt(to).isEmpty() || canJump(currentBoard, to, direction))
                .orElse(false);
    }

    @Override
    public boolean canMovePawn(GameState state, PlayerId player, Position target) {
        Board board = state.board();
        Position from = state.playerPosition(player);

        int dr = target.row() - from.row();
        int dc = target.col() - from.col();
        int manhattan = Math.abs(dr) + Math.abs(dc);

        return switch (manhattan) {
            case 1 -> Direction.fromUnitDelta(dr, dc)
                    .map(dir -> canStep(board, from, dir))
                    .orElse(false);
            case 2 -> canDistance2(board, from, dr, dc, target);

            default -> false;
        };
    }

    private boolean canStep(Board board, Position from, Direction dir) {
        return from.tryMove(dir)
                .filter(to -> !board.isEdgeBlocked(from, dir))
                .filter(to -> board.occupantAt(to).isEmpty())
                .isPresent();
    }

    private boolean canStraightJump(Board board, Position from, Direction dir) {
        return from.tryMove(dir)
                .filter(adj -> !board.isEdgeBlocked(from, dir))
                .filter(adj -> board.occupantAt(adj).isPresent())
                .filter(adj -> canJump(board, adj, dir))
                .isPresent();
    }

    private boolean canDiagonalJump(Board board, Position from, Position target, int stepDr, int stepDc) {
        if (board.occupantAt(target).isPresent()) return false;

        Optional<Direction> verticalFront   = Direction.fromUnitDelta(stepDr, 0);
        Optional<Direction> horizontalFront = Direction.fromUnitDelta(0, stepDc);

        boolean viaVertical = verticalFront.isPresent()
                && horizontalFront.isPresent()
                && from.tryMove(verticalFront.get())
                .filter(adj -> !board.isEdgeBlocked(from, verticalFront.get()))
                .filter(adj -> board.occupantAt(adj).isPresent())
                .filter(adj -> !canStraightJump(board, from, verticalFront.get()))
                .flatMap(adj -> adj.tryMove(horizontalFront.get())
                        .filter(p -> p.equals(target))
                        .filter(p -> !board.isEdgeBlocked(adj, horizontalFront.get()))
                )
                .isPresent();

        if (viaVertical) return true;

        return horizontalFront.isPresent()
                && verticalFront.isPresent()
                && from.tryMove(horizontalFront.get())
                .filter(adj -> !board.isEdgeBlocked(from, horizontalFront.get()))
                .filter(adj -> board.occupantAt(adj).isPresent())
                .filter(adj -> !canStraightJump(board, from, horizontalFront.get()))
                .flatMap(adj -> adj.tryMove(verticalFront.get())
                        .filter(p -> p.equals(target))
                        .filter(p -> !board.isEdgeBlocked(adj, verticalFront.get()))
                )
                .isPresent();
    }


    private boolean canDistance2(Board board, Position from, int dr, int dc, Position target) {
        int stepDr = Integer.signum(dr);
        int stepDc = Integer.signum(dc);

        if ((Math.abs(dr) == 2) ^ (Math.abs(dc) == 2)) {
            return Direction.fromUnitDelta(stepDr, stepDc)
                    .map(dir -> canStraightJump(board, from, dir))
                    .orElse(false);
        }

        if (Math.abs(dr) == 1 && Math.abs(dc) == 1) {
            return canDiagonalJump(board, from, target, stepDr, stepDc);
        }

        return false;
    }


    boolean canJump(Board board, Position occupiedAdj, Direction dir) {
        Optional<Position> maybeBehind = occupiedAdj.tryMove(dir);
        if (maybeBehind.isEmpty()) return false;

        Position behind = maybeBehind.get();
        if (board.isEdgeBlocked(occupiedAdj, dir) || board.isEdgeBlocked(behind, dir.opposite())) {
            return false;
        }

        return board.occupantAt(behind).isEmpty();
    }

}

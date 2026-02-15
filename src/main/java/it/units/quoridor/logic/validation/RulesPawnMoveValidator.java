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
        return legalTargets(state, player).anyMatch(target::equals);
    }

    // for now: step or straight jump in the 4 cardinal directions
    Stream<Position> legalTargets(GameState state, PlayerId player) {
        Board board = state.board();
        Position from = state.getPlayerPosition(player);

        return Arrays.stream(Direction.values())
                .map(dir -> resolveCardinalDestination(board, from, dir))
                .flatMap(Optional::stream);
    }

    private Optional<Position> resolveCardinalDestination(Board board, Position from, Direction dir) {
        // step candidate must exist and not be blocked by a wall
        Optional<Position> maybeAdj = from.tryMove(dir)
                .filter(adj -> !board.isEdgeBlocked(from, dir));

        if (maybeAdj.isEmpty()) return Optional.empty();
        Position adj = maybeAdj.get();

        // normal step
        if (board.occupantAt(adj).isEmpty()) {
            return Optional.of(adj);
        }

        // straight jump
        return canJump(board, adj, dir)
                ? adj.tryMove(dir)
                : Optional.empty();
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

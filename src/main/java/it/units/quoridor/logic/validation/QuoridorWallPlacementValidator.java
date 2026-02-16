package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.GameRules;

import java.util.Set;

/**
 * Three-step wall placement validation:
 *
 * <ol>
 *   <li><b>Overlap</b> — the wall's blocked edges must not duplicate any existing blocked edge.</li>
 *   <li><b>Crossing</b> — two perpendicular walls cannot share the same intersection point.</li>
 *   <li><b>Path preservation</b> — after tentatively placing the wall, BFS verifies every player
 *       can still reach their goal row. This is the critical Quoridor rule that prevents
 *       players from being completely walled off.</li>
 * </ol>
 */
public class QuoridorWallPlacementValidator implements WallPlacementValidator {

    private final GameRules rules;
    private final PathFinder pathFinder;

    public QuoridorWallPlacementValidator(GameRules rules, PathFinder pathFinder) {
        this.rules = rules;
        this.pathFinder = pathFinder;
    }

    @Override
    public boolean canPlaceWall(GameState state, PlayerId player, Wall wall) {

        Board board = state.board();
        Set<BlockedEdge> existingBlocked = board.allBlockedEdges();

        for (BlockedEdge edge : wall.blockedEdges()) {
            if (existingBlocked.contains(edge)) {
                return false;
            }
        }

        boolean crosses = board.walls().stream()
                .anyMatch(wall1 -> wall1.position().equals(wall.position()) &&
                        wall1.orientation() != wall.orientation());
        if (crosses) return false;

        Board withWall = board.addWall(wall);
        for (Player p : state.players()) {
            if (!hasPathToGoalRow(withWall, state.playerPosition(p.id()), rules.getGoalRow(p.id()))) {
                return false;
            }
        }

        return true;
    }

    private boolean hasPathToGoalRow(Board board, Position playerPosition, int goalRow) {
        for (int col = Position.MIN_COORDINATE; col <= Position.MAX_COORDINATE; col++) {
            Position target = new Position(goalRow, col);
            if (pathFinder.pathExists(board, playerPosition, target)) {
                return true;
            }
        }

        return false;
    }
}

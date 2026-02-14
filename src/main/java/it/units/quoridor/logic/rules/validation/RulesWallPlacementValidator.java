package it.units.quoridor.logic.rules.validation;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.GameRules;

import java.util.Set;

public class RulesWallPlacementValidator implements WallPlacementValidator{

    private final GameRules rules;
    private final PathFinder pathFinder;

    public RulesWallPlacementValidator(GameRules rules, PathFinder pathFinder) {
        this.rules = rules;
        this.pathFinder = pathFinder;
    }

    @Override
    public boolean canPlaceWall(GameState state, PlayerId player, Wall wall) {

        Board board = state.board();

        Set<BlockedEdge> existingBlocked = board.getAllBlockedEdges();

        // compute edges blocked by new all and check if those are already blocked
        for (BlockedEdge edge : wall.getBlockedEdges()) {
            if (existingBlocked.contains(edge)) {
                return false;
            }
        }

        // check for crossing (same anchor different orientations)
        boolean crosses = board.walls().stream()
                .anyMatch(wall1 -> wall1.position().equals(wall.position()) &&
                        wall1.orientation() != wall.orientation());
        if (crosses) return false;

        // path constraint: placing a wall must not block all paths to goal row
        Board withWall = board.addWall(wall);

        // each player must have an open path to goal after wall placement
        for (Player p : state.players()) {
            if (!hasPathToGoalRow(withWall, state.getPlayerPosition(p.id()), rules.getGoalRow(p.id()))) {
                return false;
            }
        }

        return true;
    }

    // use the path finder to check reachability to any cell in the goal row
    private boolean hasPathToGoalRow(Board board, Position playerPosition, int goalRow) {

        for (int col = Position.MIN_COORDINATE; col <= Position.MAX_COORDINATE; col++) {
            Position target = new Position(goalRow, col);

            // if there is still an open path to goal row validator returns true
            if (pathFinder.pathExists(board, playerPosition, target)) {
                return true;
            }
        }

        return false;
    }
}

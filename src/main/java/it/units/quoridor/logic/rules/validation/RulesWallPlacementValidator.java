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

        return true;
    }
}

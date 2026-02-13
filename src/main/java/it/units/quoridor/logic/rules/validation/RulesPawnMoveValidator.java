package it.units.quoridor.logic.rules.validation;

import it.units.quoridor.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RulesPawnMoveValidator implements PawnMoveValidator{

    public boolean canMovePawn(GameState state, PlayerId player, Direction direction) {

        Board currentBoard = state.board();
        Position currentPosition = state.getPlayerPosition(player);
        Position proposedPosition = currentPosition.move(direction);

        // we have to return true if the square in the proposed direction is free (from walls and players)
        if (currentBoard.isEdgeBlocked(currentPosition, direction)) {
            return false;
        }

        // we want to find whether a player is already occupying the proposed position
        Map<PlayerId, Position> playerPositions = currentBoard.playerPositions();
        PlayerId occupantPlayer = playerPositions.entrySet().stream()
                .filter(e -> e.getValue().equals(proposedPosition))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null); // if yes we return PlayerID else null

        // if the position is not occupied by anybody just move
        if (occupantPlayer == null) {
            return true;
        }

        // JUMP mechanic:
        // - if behind the player there is a wall
        if (currentBoard.isEdgeBlocked(proposedPosition, direction)) {
            return false; // no jump
        }

        return true;
    }
}

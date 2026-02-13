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
        Optional<Position> maybePosition = currentPosition.tryMove(direction);

        if (maybePosition.isEmpty()) {
            return false;
        }

        Position proposedPosition = maybePosition.get();

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
        // we first try to jump
        return canJump(currentBoard, proposedPosition, playerPositions, direction);
    }

    boolean canJump(Board currentBoard, Position proposedPosition, Map<PlayerId, Position> playerPositions, Direction direction) {
        if (currentBoard.isEdgeBlocked(proposedPosition, direction)) {
            return false; // no jump
        }

        Optional<Position> maybeBehindPosition = proposedPosition.tryMove(direction);

        if (maybeBehindPosition.isEmpty()) {
            return false;
        }

        Position behindPosition = maybeBehindPosition.get();
        // - if behind the player there is another player

        return !playerPositions.containsValue(behindPosition); // no jump
    }
}

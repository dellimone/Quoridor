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
        Optional<PlayerId> occupantPlayer =  currentBoard.occupantAt(proposedPosition); // return Optional

        // if the position is occupied by nobody
        if (occupantPlayer.isEmpty()) {
            return true;
        }
        // we first try to jump

        Map<PlayerId, Position> playerPositions = currentBoard.playerPositions();
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

    // small helper method to identify whether there is a player in that position
    private Optional<PlayerId> findOccupantPlayerID(
            Map<PlayerId, Position> playerPositions,
            Position position
    ) {
        return playerPositions.entrySet().stream()
                .filter(e -> e.getValue().equals(position))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}

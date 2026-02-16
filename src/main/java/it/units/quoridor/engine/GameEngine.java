package it.units.quoridor.engine;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import it.units.quoridor.domain.Wall;
import it.units.quoridor.domain.GameState;

import java.util.Set;

public interface GameEngine {

    // only expose user-facing actions (to be furtherly expanded)
    GameState getGameState();
    MoveResult movePawn(PlayerId player, Position position);
    MoveResult placeWall(PlayerId player, Wall wall);
    Set<Position> legalPawnDestinationsForPlayer(PlayerId player);

    void reset();
    boolean undo();
    boolean isGameOver();
    PlayerId getWinner();
}

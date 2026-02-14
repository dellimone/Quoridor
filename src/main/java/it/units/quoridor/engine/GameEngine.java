package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

import java.util.Set;

public interface GameEngine {

    // only expose user-facing actions (to be furtherly expanded)
    GameState getGameState();
    MoveResult movePawn(PlayerId player, Direction direction);
    MoveResult movePawn(PlayerId player, Position position);
    MoveResult placeWall(PlayerId player, Wall wall);
    Set<Position> legalPawnDestinationsForPlayer();

    void reset();
    boolean undo();
    boolean isGameOver();
    PlayerId getWinner();
}

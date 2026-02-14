package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

public interface GameEngine {

    // only expose user-facing actions (to be furtherly expanded)
    GameState getGameState();
    MoveResult movePawn(PlayerId player, Direction direction);
    MoveResult movePawn(PlayerId player, Position position);
    MoveResult placeWall(PlayerId player, Wall wall);

    void reset();
    boolean undo();
    boolean isGameOver();
    PlayerId getWinner();
}

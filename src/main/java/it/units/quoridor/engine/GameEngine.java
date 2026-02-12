package it.units.quoridor.engine;

import it.units.quoridor.domain.*;

public interface GameEngine {

    // only expose user-facing actions (to be furtherly expanded)
    GameState getGameState();
    MoveResult movePawn(PlayerId player, Direction direction);
    MoveResult placeWall(PlayerId player, Wall wall);

    void newGame();
    boolean isGameOver();
    PlayerId getWinner();
}

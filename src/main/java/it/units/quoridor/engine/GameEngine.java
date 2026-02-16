package it.units.quoridor.engine;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import it.units.quoridor.domain.Wall;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.rules.PlayerCount;

import java.util.List;
import java.util.Set;

/**
 * Orchestrates game flow: delegates validation to the Logic layer,
 * manages state transitions, and maintains move history for undo.
 */
public interface GameEngine {

    /** Current game state snapshot. */
    GameState gameState();
    /** Attempt to move a player's pawn to the target position. */
    MoveResult movePawn(PlayerId player, Position position);
    /** Attempt to place a wall for the given player. */
    MoveResult placeWall(PlayerId player, Wall wall);
    /** All positions the player can legally move to this turn. */
    Set<Position> legalPawnDestinationsForPlayer(PlayerId player);

    /** Start a new game with the given player count and player names. */
    void newGame(PlayerCount playerCount, List<String> playerNames);
    /** Reset to a fresh game with the last used player count. */
    void reset();
    /** Undo the last move. Returns false if nothing to undo. */
    boolean undo();
    boolean isGameOver();
    PlayerId winner();
}

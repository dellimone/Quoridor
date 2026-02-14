package it.units.quoridor.logic.rules.setup;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.PlayerCount;

import java.util.List;


// uses the official GameRules to build the initial GameState of a match
public class InitialStateFactory {

    // create the initial game state for a two players match given the set of rules
    public static GameState create(
            GameRules rules,
            PlayerCount playerCount,
            List<PlayerSpec> specs
    ) {

        if (specs == null || specs.isEmpty()) {
            throw new IllegalArgumentException("player list cannot be empty");
        }

        // specs size has to match player count
        if (playerCount == PlayerCount.TWO_PLAYERS && specs.size() != 2) {
            throw new IllegalArgumentException("TWO_PLAYERS requires exactly 2 players");
        }
        if (playerCount == PlayerCount.FOUR_PLAYERS && specs.size() != 4) {
            throw new IllegalArgumentException("FOUR_PLAYERS requires exactly 4 players");
        }

        // our "truth" for the initialization will be the implemented rules -> single source of truth!!
        int walls = rules.getInitialWallCount(playerCount);

        // we build the players
        List<Player> players = specs.stream()
                .map(s -> new Player(s.id(), s.name(), walls, rules.getGoalRow(s.id())))
                .toList();

        Board board = new Board();
        for (PlayerSpec spec : specs) {
            board = board.withPlayerAt(spec.id(), rules.getStartPosition(spec.id()));
        }


        // create the initial snapshot of the game
        return new GameState(board, players, 0, GameStatus.IN_PROGRESS, null);
    }

    // we don't want the class to be instantiated
    private InitialStateFactory() {}
}

package it.units.quoridor.logic.rules.setup;

import it.units.quoridor.domain.*;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.PlayerCount;

import java.util.List;


/** Creates the initial GameState from rules, player count, and player specs. */
public class InitialStateFactory {

    public static GameState create(
            GameRules rules,
            PlayerCount playerCount,
            List<PlayerSpec> specs
    ) {

        if (specs == null || specs.isEmpty()) {
            throw new IllegalArgumentException("player list cannot be empty");
        }

        if (playerCount == PlayerCount.TWO_PLAYERS && specs.size() != 2) {
            throw new IllegalArgumentException("TWO_PLAYERS requires exactly 2 players");
        }
        if (playerCount == PlayerCount.FOUR_PLAYERS && specs.size() != 4) {
            throw new IllegalArgumentException("FOUR_PLAYERS requires exactly 4 players");
        }

        int walls = rules.getInitialWallCount(playerCount);
        List<Player> players = specs.stream()
                .map(s -> new Player(s.id(), s.name(), walls))
                .toList();

        Board board = new Board();
        for (PlayerSpec spec : specs) {
            board = board.withPlayerAt(spec.id(), rules.getStartPosition(spec.id()));
        }


        return new GameState(board, players, 0, GameStatus.IN_PROGRESS, null);
    }

    private InitialStateFactory() {}
}

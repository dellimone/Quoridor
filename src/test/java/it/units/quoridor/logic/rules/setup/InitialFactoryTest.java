package it.units.quoridor.logic.rules.setup;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.*;

import it.units.quoridor.logic.rules.PlayerCount;
import it.units.quoridor.logic.rules.QuoridorGameRules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InitialFactoryTest {

    private final QuoridorGameRules rules = new QuoridorGameRules();

    // 25. factory creates correct starting positions -> two players
    // given the standard rules, when we create the initial state it should be correct according to the rules

    @Test
    void factoryCreatesCorrectStartingState_twoPlayers() {

        // create specs for two players
        List<PlayerSpec> playerSpecs = List.of(
                new PlayerSpec(PlayerId.PLAYER_1, "Alice"),
                new PlayerSpec(PlayerId.PLAYER_2, "Bob")
        );

        GameState initialState = InitialStateFactory.create(
                rules,
                PlayerCount.TWO_PLAYERS,
                playerSpecs
        );

        // we assert whether they are initialised according to the rules
        assertEquals(rules.getStartPosition(PlayerId.PLAYER_1), initialState.playerPosition(PlayerId.PLAYER_1));
        assertEquals(rules.getStartPosition(PlayerId.PLAYER_2), initialState.playerPosition(PlayerId.PLAYER_2));

        // check whether they have the correct wall count as rules expect
        int expectedWalls = rules.getInitialWallCount(PlayerCount.TWO_PLAYERS);
        assertEquals(expectedWalls, initialState.player(PlayerId.PLAYER_1).wallsRemaining());
        assertEquals(expectedWalls, initialState.player(PlayerId.PLAYER_2).wallsRemaining());

        // check that player1 is the starting player
        assertEquals(PlayerId.PLAYER_1, initialState.currentPlayerId());
    }

    // 26. wrong specs should throw an exception
    @Test
    void create_rejectsWrongSpecs() {

        // wrongly create spec for one player
        List<PlayerSpec> playerSpecs = List.of(
                new PlayerSpec(PlayerId.PLAYER_1, "Alice")
        );

        assertThrows(IllegalArgumentException.class,
                () -> InitialStateFactory.create(
                    rules,
                    PlayerCount.TWO_PLAYERS,
                    playerSpecs)
        );
    }

}

package it.units.quoridor.logic.rules;

import it.units.quoridor.domain.PlayerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuoridorGameRulesTest {

    private final QuoridorGameRules rules = new QuoridorGameRules();

    // Test 2-player turn rotation: P1 → P2 → P1
    @Test
    void getNextPlayer_twoPlayers_player1ToPlayer2() {
        PlayerId nextPlayer = rules.getNextPlayer(PlayerId.PLAYER_1, PlayerCount.TWO_PLAYERS);
        assertEquals(PlayerId.PLAYER_2, nextPlayer);
    }

    @Test
    void getNextPlayer_twoPlayers_player2ToPlayer1() {
        PlayerId nextPlayer = rules.getNextPlayer(PlayerId.PLAYER_2, PlayerCount.TWO_PLAYERS);
        assertEquals(PlayerId.PLAYER_1, nextPlayer);
    }

    // Test 4-player turn rotation: P1 → P2 → P3 → P4 → P1
    @Test
    void getNextPlayer_fourPlayers_player1ToPlayer2() {
        PlayerId nextPlayer = rules.getNextPlayer(PlayerId.PLAYER_1, PlayerCount.FOUR_PLAYERS);
        assertEquals(PlayerId.PLAYER_2, nextPlayer);
    }

    @Test
    void getNextPlayer_fourPlayers_player2ToPlayer3() {
        PlayerId nextPlayer = rules.getNextPlayer(PlayerId.PLAYER_2, PlayerCount.FOUR_PLAYERS);
        assertEquals(PlayerId.PLAYER_3, nextPlayer);
    }

    @Test
    void getNextPlayer_fourPlayers_player3ToPlayer4() {
        PlayerId nextPlayer = rules.getNextPlayer(PlayerId.PLAYER_3, PlayerCount.FOUR_PLAYERS);
        assertEquals(PlayerId.PLAYER_4, nextPlayer);
    }

    @Test
    void getNextPlayer_fourPlayers_player4ToPlayer1() {
        PlayerId nextPlayer = rules.getNextPlayer(PlayerId.PLAYER_4, PlayerCount.FOUR_PLAYERS);
        assertEquals(PlayerId.PLAYER_1, nextPlayer);
    }
}
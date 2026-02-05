package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerIdTest {

    @Test
    void player1HasOrdinal0() {
        assertEquals(0, PlayerId.PLAYER_1.ordinal());
    }

    @Test
    void player2HasOrdinal1() {
        assertEquals(1, PlayerId.PLAYER_2.ordinal());
    }

    @Test
    void player3HasOrdinal2() {
        assertEquals(2, PlayerId.PLAYER_3.ordinal());
    }

    @Test
    void player4HasOrdinal3() {
        assertEquals(3, PlayerId.PLAYER_4.ordinal());
    }

}
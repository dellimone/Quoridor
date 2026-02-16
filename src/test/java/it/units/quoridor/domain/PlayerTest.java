package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void createPlayerWithIdNameAndWalls() {
        PlayerId id = PlayerId.PLAYER_1;
        String name = "Alice";
        int wallsRemaining = 10;

        Player player = new Player(id, name, wallsRemaining);

        assertEquals(id, player.id());
        assertEquals(name, player.name());
        assertEquals(wallsRemaining, player.wallsRemaining());
    }

    @Test
    void playerKnowsItsId() {
        Player player = new Player(PlayerId.PLAYER_2, "Bob", 10);
        assertEquals(PlayerId.PLAYER_2, player.id());
    }

    @Test
    void playerKnowsItsName() {
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 10);
        assertEquals("Alice", player.name());
    }

    @Test
    void useWallDecreaseRemainingWalls(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 10);

        // since now player is immutable
        Player updatedPlayer = player.useWall();
        assertEquals(9, updatedPlayer.wallsRemaining());
    }

    @Test
    void useWallWithNoRemainingWallsThrowsException(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 0);
        assertThrows(IllegalStateException.class, player::useWall);
    }
}

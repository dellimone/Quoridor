package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void createPlayerWithIdNameGoalRowAndWalls() {
        PlayerId id = PlayerId.PLAYER_1;
        String name = "Alice";
        int goalRow = 0;
        int wallsRemaining = 10;

        Player player = new Player(id, name, wallsRemaining, goalRow);

        assertEquals(id, player.id());
        assertEquals(name, player.name());
        assertEquals(goalRow, player.goalRow());
        assertEquals(wallsRemaining, player.wallsRemaining());
    }

    @Test
    void playerKnowsItsId() {
        Player player = new Player(PlayerId.PLAYER_2, "Bob", 10, 8);
        assertEquals(PlayerId.PLAYER_2, player.id());
    }

    @Test
    void playerKnowsItsName() {
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        assertEquals("Alice", player.name());
    }

    @Test
    void playerKnowsItsGoalRow() {
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 10, 8);
        assertEquals(8, player.goalRow());
    }

    @Test
    void useWallDecreaseRemainingWalls(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 10, 0);
        player.useWall();
        assertEquals(9, player.wallsRemaining());
    }

    @Test
    void useWallWithNoRemainingWallsThrowsException(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", 0, 0);
        assertThrows(IllegalStateException.class, player::useWall);
    }
}

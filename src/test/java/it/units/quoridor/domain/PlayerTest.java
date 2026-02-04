package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void createPlayerWithIdNamePositionGoalRowAndWalls() {
        PlayerId id = PlayerId.PLAYER_1;
        String name = "Alice";
        Position position = new Position(8, 4);
        int goalRow = 0;
        int wallsRemaining = 10;

        Player player = new Player(id, name, position, wallsRemaining, goalRow);

        assertEquals(id, player.id());
        assertEquals(name, player.name());
        assertEquals(position, player.position());
        assertEquals(goalRow, player.goalRow());
        assertEquals(wallsRemaining, player.wallsRemaining());
    }

    @Test
    void playerKnowsItsId() {
        Player player = new Player(PlayerId.PLAYER_2, "Bob", new Position(0, 4), 10, 8);
        assertEquals(PlayerId.PLAYER_2, player.id());
    }

    @Test
    void playerKnowsItsName() {
        Player player = new Player(PlayerId.PLAYER_1, "Alice", new Position(8, 4), 10, 8);
        assertEquals("Alice", player.name());
    }

    @Test
    void playerKnowsItsPosition() {
        Position position = new Position(8, 4);
        Player player = new Player(PlayerId.PLAYER_1, "Alice", position, 10, 8 );
        assertEquals(position, player.position());
    }

    @Test
    void playerKnowsItsGoalRow() {
        Player player = new Player(PlayerId.PLAYER_1, "Alice", new Position(8, 4), 10, 8);
        assertEquals(8, player.goalRow());
    }

    @Test
    void movePlayerToNewPositionUpdatePosition(){
        Position startingPosition = new Position(4, 4);
        Position endingPosition = new Position(6, 6);
        Player player = new Player(PlayerId.PLAYER_1, "Alice", startingPosition, 10, 8);
        player.move(endingPosition);
        assertEquals(endingPosition, player.position());
    }

    @Test
    void useWallDecreaseRemainingWalls(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", new Position(8, 4), 10, 8);
        player.useWall();
        assertEquals(9, player.wallsRemaining());
    }

    @Test
    void useWallWithNoRemainingWallsThrowsException(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", new Position(8, 4), 0, 8);
        assertThrows(IllegalStateException.class, player::useWall);
    }

    @Test
    void playerAtGoalRowHasReachedGoal(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", new Position(8, 4), 10, 8);
        assertTrue(player.hasReachedGoal());
    }

    @Test
    void playerNotAtGoalRowRowHasNotReachedGoal(){
        Player player = new Player(PlayerId.PLAYER_1, "Alice", new Position(0, 4), 10, 8);
        assertFalse(player.hasReachedGoal());
    }
}

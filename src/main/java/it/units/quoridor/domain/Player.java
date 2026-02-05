package it.units.quoridor.domain;

public class Player {
    private final PlayerId id;
    private final String name;
    private int wallsRemaining;
    private final int goalRow;

    public Player(
            PlayerId playerId,
            String name,
            int startingWalls,
            int goalRow) {
        this.id = playerId;
        this.name = name;
        this.wallsRemaining = startingWalls;
        this.goalRow = goalRow;
    }

    public PlayerId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int wallsRemaining() {
        return wallsRemaining;
    }

    public int goalRow() {
        return goalRow;
    }

    public void useWall() {
        if (this.wallsRemaining > 0) {
            this.wallsRemaining--;
        }
        else  {
            throw new IllegalStateException("No walls remaining");
        }
    }
}

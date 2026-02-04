package it.units.quoridor.domain;

public class Player {
    private final PlayerId id;
    private final String name;
    private Position position;
    private int wallsRemaining;
    private final int goalRow;


    public Player(
            PlayerId playerId,
            String name,
            Position startingPosition,
            int startingWalls,
            int goalRow) {
        this.id = playerId;
        this.name = name;
        this.position = startingPosition;
        this.wallsRemaining = startingWalls;
        this.goalRow = goalRow;
    }

    public PlayerId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Position position() {
        return position;

    }
    public int wallsRemaining() {
        return wallsRemaining;
    }

    public int goalRow() {
        return goalRow;
    }
}

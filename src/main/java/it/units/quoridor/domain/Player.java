package it.units.quoridor.domain;

public record Player(
    PlayerId id,
    String name,
    int wallsRemaining,
    int goalRow
){
    public Player {
        if (wallsRemaining < 0) throw new IllegalArgumentException("wallsRemaining must be >= 0");
    }

    // instead of mutating, we return a new player
    public Player useWall() {
        if (wallsRemaining <= 0) {
            throw new IllegalStateException("no walls remaining");
        }

        return new Player(id, name, wallsRemaining-1, goalRow);
    }

    public Player withWallsRemaining(int walls) {
        if (walls < 0) throw new IllegalArgumentException("wallsRemaining must be >= 0");
        return new Player(id, name, walls, goalRow);
    }
}

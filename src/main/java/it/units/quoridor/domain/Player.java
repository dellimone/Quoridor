package it.units.quoridor.domain;

/** Immutable player state: identity, name, and remaining wall count. */
public record Player(
    PlayerId id,
    String name,
    int wallsRemaining
){
    public Player {
        if (wallsRemaining < 0) throw new IllegalArgumentException("wallsRemaining must be >= 0");
    }

    public Player useWall() {
        if (wallsRemaining <= 0) {
            throw new IllegalStateException("no walls remaining");
        }

        return new Player(id, name, wallsRemaining - 1);
    }

    public Player withWallsRemaining(int walls) {
        if (walls < 0) throw new IllegalArgumentException("wallsRemaining must be >= 0");
        return new Player(id, name, walls);
    }
}

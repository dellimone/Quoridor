package it.units.quoridor;

import it.units.quoridor.domain.*;

import java.util.List;

/**
 * Shared test constants and factory methods to reduce boilerplate.
 * Use via: import static it.units.quoridor.TestFixtures.*;
 */
public final class TestFixtures {

    private TestFixtures() {}

    // ── Players ──────────────────────────────────────────────

    public static final Player P1 = new Player(PlayerId.PLAYER_1, "P1", 10);
    public static final Player P2 = new Player(PlayerId.PLAYER_2, "P2", 10);
    public static final Player P3 = new Player(PlayerId.PLAYER_3, "P3", 5);
    public static final Player P4 = new Player(PlayerId.PLAYER_4, "P4", 5);

    // ── Positions ────────────────────────────────────────────

    public static final Position P1_START = new Position(0, 4);
    public static final Position P2_START = new Position(8, 4);
    public static final Position P3_START = new Position(4, 0);
    public static final Position P4_START = new Position(4, 8);

    // ── Board factories ──────────────────────────────────────

    public static Board standardBoard() {
        return new Board()
                .withPlayerAt(PlayerId.PLAYER_1, P1_START)
                .withPlayerAt(PlayerId.PLAYER_2, P2_START);
    }

    public static Board fourPlayerBoard() {
        return new Board()
                .withPlayerAt(PlayerId.PLAYER_1, P1_START)
                .withPlayerAt(PlayerId.PLAYER_2, P2_START)
                .withPlayerAt(PlayerId.PLAYER_3, P3_START)
                .withPlayerAt(PlayerId.PLAYER_4, P4_START);
    }

    // ── GameState factories ──────────────────────────────────

    public static GameState standardState() {
        return new GameState(standardBoard(), List.of(P1, P2));
    }

    public static GameState fourPlayerState() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 5);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 5);
        return new GameState(fourPlayerBoard(), List.of(p1, p2, P3, P4));
    }

    public static GameState stateWith(Board board) {
        return new GameState(board, List.of(P1, P2));
    }

    // ── Wall factories ───────────────────────────────────────

    public static Wall hWall(int row, int col) {
        return new Wall(new WallPosition(row, col), WallOrientation.HORIZONTAL);
    }

    public static Wall vWall(int row, int col) {
        return new Wall(new WallPosition(row, col), WallOrientation.VERTICAL);
    }
}

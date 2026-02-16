package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PawnValidatorTest {

    private final QuoridorGameRules rules = new QuoridorGameRules();
    private final PawnMoveValidator pawnValidator = new RulesPawnMoveValidator();

    // 1. returns true when proposed square is free from walls
    @Test
    void returnsTrue_freeSquareFromWalls() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST));
    }

    // 2. returns false when proposed square is blocked by walls
    @Test
    void returnsFalse_occupiedSquareByWall() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(0, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(8, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        WallPosition wallPosition = new WallPosition(0,4);
        Wall wall = new Wall(wallPosition, WallOrientation.VERTICAL);
        Board newBoard = initialState.board().addWall(wall);
        GameState currentState = new GameState(newBoard, List.of(p1, p2));

        assertFalse(pawnValidator.canMovePawn(currentState, PlayerId.PLAYER_1, Direction.EAST));
    }


    // 3. returns true when proposed square is occupied by another player but the one behind is free
    @Test
    void returnsTrue_proposedSquareOccupiedBehindFree() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 4. returns false when proposed square is occupied by another player but the one behind is blocked by a wall
    @Test
    void returnsFalse_proposedSquareOccupiedBehindWall() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);
        WallPosition wallPosition = new WallPosition(0,4);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        Board board = new Board()
                .addWall(wall)
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 5. returns false when proposed square is occupied by another player but the one behind is blocked by a player
    @Test
    void returnsFalse_proposedSquareOccupiedBehindAnotherPlayer() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);
        Player p3 = new Player(PlayerId.PLAYER_3, "P3", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4))
                .withPlayerAt(PlayerId.PLAYER_3, new Position(0, 4));

        GameState initialState = new GameState(board, List.of(p1, p2, p3));

        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 6. returns false when proposed square is occupied by another player but the one behind is outside the board
    @Test
    void returnsFalse_proposedSquareOccupiedBehindVoid() {
        // create a small example for board
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(1, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));

        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 7. diagonal allowed when jump is blocked by a wall behind BUT side is open
    @Test
    void allowedDiagonal_jumpBlocked_sideOpen() {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);
        WallPosition wallPosition = new WallPosition(0,4);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        Board board = new Board()
                .addWall(wall)
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));
        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, new Position(1, 3)));

    }

    // 8. diagonal is not allowed when jump is not allowed and side is blocked by a wall
    @Test
    void noDiagonal_jumpBlocked_sideWallBlocked () {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);
        WallPosition wallPosition = new WallPosition(1,4);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        WallPosition wallPosition1 = new WallPosition(2,4);
        Wall wall1 = new Wall(wallPosition, WallOrientation.VERTICAL);

        Board board = new Board()
                .addWall(wall)
                .addWall(wall1)
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = new GameState(board, List.of(p1, p2));
        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, new Position(1, 3)));

    }

    // 9. diagonal is allowed only in the "horizontal case" -> diagonals on N/S
    @Test
    void Diagonal_horizontalCase () {
        Player p1 = new Player(PlayerId.PLAYER_1, "P1", 10);
        Player p2 = new Player(PlayerId.PLAYER_2, "P2", 10);
        WallPosition wallPosition = new WallPosition(2,2);
        Wall wall = new Wall(wallPosition, WallOrientation.VERTICAL);

        Board board = new Board()
                .addWall(wall)
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(2, 3));

        GameState initialState = new GameState(board, List.of(p1, p2));
        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, new Position(3, 3)));
    }

}

package it.units.quoridor.logic.validation;

import it.units.quoridor.domain.*;
import it.units.quoridor.domain.GameState;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static it.units.quoridor.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PawnValidatorTest {

    private final PawnMoveValidator pawnValidator = new QuoridorPawnMoveValidator();

    // 1. returns true when proposed square is free from walls
    @Test
    void returnsTrue_freeSquareFromWalls() {
        GameState initialState = standardState();
        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.EAST));
    }

    // 2. returns false when proposed square is blocked by walls
    @Test
    void returnsFalse_occupiedSquareByWall() {
        Board newBoard = standardBoard().addWall(vWall(0, 4));
        GameState currentState = stateWith(newBoard);

        assertFalse(pawnValidator.canMovePawn(currentState, PlayerId.PLAYER_1, Direction.EAST));
    }

    // 3. returns true when proposed square is occupied by another player but the one behind is free
    @Test
    void returnsTrue_proposedSquareOccupiedBehindFree() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = stateWith(board);

        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 4. returns false when proposed square is occupied by another player but the one behind is blocked by a wall
    @Test
    void returnsFalse_proposedSquareOccupiedBehindWall() {
        Board board = new Board()
                .addWall(hWall(0, 4))
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = stateWith(board);

        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 5. returns false when proposed square is occupied by another player but the one behind is blocked by a player
    @Test
    void returnsFalse_proposedSquareOccupiedBehindAnotherPlayer() {
        Player p3 = new Player(PlayerId.PLAYER_3, "P3", 10);

        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4))
                .withPlayerAt(PlayerId.PLAYER_3, new Position(0, 4));

        GameState initialState = new GameState(board, List.of(P1, P2, p3));

        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 6. returns false when proposed square is occupied by another player but the one behind is outside the board
    @Test
    void returnsFalse_proposedSquareOccupiedBehindVoid() {
        Board board = new Board()
                .withPlayerAt(PlayerId.PLAYER_1, new Position(1, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(0, 4));

        GameState initialState = stateWith(board);

        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, Direction.SOUTH));
    }

    // 7. diagonal allowed when jump is blocked by a wall behind BUT side is open
    @Test
    void allowedDiagonal_jumpBlocked_sideOpen() {
        Board board = new Board()
                .addWall(hWall(0, 4))
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = stateWith(board);
        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, new Position(1, 3)));
    }

    // 8. diagonal is not allowed when jump is not allowed and side is blocked by a wall
    @Test
    void noDiagonal_jumpBlocked_sideWallBlocked () {
        Board board = new Board()
                .addWall(hWall(1, 4))
                .addWall(new Wall(new WallPosition(1, 4), WallOrientation.VERTICAL))
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(1, 4));

        GameState initialState = stateWith(board);
        assertFalse(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, new Position(1, 3)));
    }

    // 9. diagonal is allowed only in the "horizontal case" -> diagonals on N/S
    @Test
    void Diagonal_horizontalCase () {
        Board board = new Board()
                .addWall(vWall(2, 2))
                .withPlayerAt(PlayerId.PLAYER_1, new Position(2, 4))
                .withPlayerAt(PlayerId.PLAYER_2, new Position(2, 3));

        GameState initialState = stateWith(board);
        assertTrue(pawnValidator.canMovePawn(initialState, PlayerId.PLAYER_1, new Position(3, 3)));
    }

}

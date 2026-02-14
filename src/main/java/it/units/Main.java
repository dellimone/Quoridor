package it.units;

import it.units.quoridor.controller.Controller;
import it.units.quoridor.domain.*;
import it.units.quoridor.engine.*;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import it.units.quoridor.logic.rules.QuoridorWinChecker;
import it.units.quoridor.logic.rules.validation.PawnMoveValidator;
import it.units.quoridor.logic.rules.validation.WallPlacementValidator;
import it.units.quoridor.view.SwingGameView;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the Quoridor game application.
 * Wires up all components using dependency injection.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Create stateless logic components
            PathFinder pathFinder = new BfsPathFinder();
            GameRules rules = new QuoridorGameRules();
            WinChecker winChecker = new QuoridorWinChecker();

            // 2. Create validators as lambdas (temporary implementations)
            PawnMoveValidator pawnValidator = createPawnValidator();
            WallPlacementValidator wallValidator = createWallValidator(pathFinder, rules);

            // 3. Create engine (auto-initializes via newGame() in constructor)
            QuoridorEngine engine = new QuoridorEngine(
                    rules,
                    pawnValidator,
                    wallValidator,
                    winChecker
            );

            // 4. Create view
            SwingGameView view = new SwingGameView();

            // 5. Create controller (wires engine + view together)
            new Controller(engine, view);

            // 6. Show window
            view.setVisible(true);
        });
    }

    /**
     * Creates a simplified pawn move validator.
     * Checks: bounds, wall blocking, but NO jump logic yet.
     */
    private static PawnMoveValidator createPawnValidator() {
        return (state, playerId, direction) -> {
            try {
                Position current = state.getPlayerPosition(playerId);

                if (state.board().isEdgeBlocked(current, direction)) {
                    return false;
                }

                Position target = current.move(direction);


                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        };
    }

    /**
     * Creates a wall placement validator.
     * Checks: overlap, path blocking using BFS.
     */
    private static WallPlacementValidator createWallValidator(PathFinder pathFinder, GameRules rules) {
        return (state, playerId, wall) -> {
            if (state.board().walls().contains(wall)) {
                return false;
            }

            WallOrientation perpendicular = wall.orientation() == WallOrientation.HORIZONTAL
                    ? WallOrientation.VERTICAL
                    : WallOrientation.HORIZONTAL;
            Wall perpendicularWall = new Wall(wall.position(), perpendicular);
            if (state.board().walls().contains(perpendicularWall)) {
                return false;
            }

            Board tempBoard = state.board().addWall(wall);

            for (Player player : state.players()) {
                Position playerPos = tempBoard.playerPosition(player.id());
                int goalRow = player.goalRow();

                boolean hasPath = false;
                for (int col = 0; col <= 8; col++) {
                    try {
                        Position goalPos = new Position(goalRow, col);
                        if (pathFinder.pathExists(tempBoard, playerPos, goalPos)) {
                            hasPath = true;
                            break;
                        }
                    } catch (IllegalArgumentException e) {
                    }
                }

                if (!hasPath) {
                    return false;
                }
            }

            return true;
        };
    }
}

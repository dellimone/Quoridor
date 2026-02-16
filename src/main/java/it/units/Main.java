package it.units;

import it.units.quoridor.controller.Controller;
import it.units.quoridor.engine.*;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.*;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import it.units.quoridor.logic.validation.QuoridorWallPlacementValidator;
import it.units.quoridor.logic.validation.QuoridorPawnMoveValidator;

import it.units.quoridor.view.SwingGameView;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the Quoridor game application.
 * Wires up all components using dependency injection.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PathFinder pathFinder = new BfsPathFinder();
            GameRules rules = new QuoridorGameRules();
            WinChecker winChecker = new QuoridorWinChecker(rules);

            PawnMoveValidator pawnValidator = new QuoridorPawnMoveValidator();
            WallPlacementValidator wallValidator = new QuoridorWallPlacementValidator(rules, pathFinder);

            QuoridorEngine engine = new QuoridorEngine(
                    rules, pawnValidator, wallValidator, winChecker
            );

            SwingGameView view = new SwingGameView();
            new Controller(engine, view);
            view.setVisible(true);
        });
    }
}

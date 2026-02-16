package it.units;

import it.units.quoridor.controller.Controller;
import it.units.quoridor.domain.*;
import it.units.quoridor.engine.*;
import it.units.quoridor.logic.pathFinder.BfsPathFinder;
import it.units.quoridor.logic.pathFinder.PathFinder;
import it.units.quoridor.logic.rules.GameRules;
import it.units.quoridor.logic.rules.QuoridorGameRules;
import it.units.quoridor.logic.rules.QuoridorWinChecker;
import it.units.quoridor.logic.validation.PawnMoveValidator;
import it.units.quoridor.logic.validation.WallPlacementValidator;
import it.units.quoridor.logic.validation.RulesWallPlacementValidator;
import it.units.quoridor.logic.validation.RulesPawnMoveValidator;

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
            WinChecker winChecker = new QuoridorWinChecker(rules);

            // 2. Create validators as lambdas (temporary implementations)
            PawnMoveValidator pawnValidator =
                    new RulesPawnMoveValidator();
            WallPlacementValidator wallValidator =
                    new RulesWallPlacementValidator(rules, pathFinder);


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
}

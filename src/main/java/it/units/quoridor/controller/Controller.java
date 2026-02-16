package it.units.quoridor.controller;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.MoveResult;
import it.units.quoridor.view.BoardViewModel;
import it.units.quoridor.view.GameView;
import it.units.quoridor.view.PlayerViewModel;
import it.units.quoridor.view.ViewListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Translates view events into engine calls and engine state into view models.
 * Flips row coordinates between view (0=top) and domain (0=bottom).
 */
public class Controller implements ViewListener {

    private final GameEngine engine;
    private final GameView view;

    private static final int MAX_ROW_INDEX = 8;
    private static final int MAX_WALL_INDEX = 7;

    // Coordinate conversion: view (0=top) ↔ domain (0=bottom)
    // Both directions use the same formula (involution: applying twice = identity)
    private static int flipRow(int row) { return MAX_ROW_INDEX - row; }
    private static int flipWallRow(int row) { return MAX_WALL_INDEX - row; }

    public Controller(GameEngine gameEngine, GameView gameView) {
        engine = gameEngine;
        view = gameView;
        view.setListener(this);
    }

    @Override
    public void onNewGame(int playerCount) {
        engine.reset();
        view.hideOverlays();
        updateView();
        view.setUndoEnabled(false);
        view.showMessage("New game started!");
    }

    /**
     * Manage the click on a cell
     * Converts the coordinate from the view to the logic for the engine and update the view
     *
     * @param rowE the row clicked (0-8)
     * @param col the column clicked (0-8)
     */
    @Override
    public void onCellClicked(int rowE, int col) {

        GameState gameState = engine.gameState();
        Player currentPlayer = gameState.currentPlayer();

        Position targetPosition = new Position(flipRow(rowE), col);

        MoveResult moveResult = engine.movePawn(currentPlayer.id(), targetPosition);

        if (moveResult.isValid()) {
            updateView();
            if (moveResult.isWin()) {
                view.showGameOver(currentPlayer.id());
            }
        } else {
            view.showError(moveResult.message());
        }
    }

    /**
     * Manage the insertion of a wall
     * Converts the coordinate from the view to the logic for the engine and update the view
     *
     * @param row wall intersection row (0-7)
     * @param col wall intersection column (0-7)
     * @param orientation HORIZONTAL or VERTICAL
     */
    @Override
    public void onWallPlacement(int row, int col, WallOrientation orientation) {

        Player currentPlayer = engine.gameState().currentPlayer();

        try {
            WallPosition wallPosition = new WallPosition(flipWallRow(row), col);
            Wall wall  = new Wall(wallPosition, orientation);
            MoveResult result = engine.placeWall(currentPlayer.id(), wall);

            if (result.isValid()) {
                updateView();
            } else {
                view.showError("Invalid Wall Placement");
            }
        } catch (IllegalArgumentException e) {
            view.showError("Invalid Wall Placement, outside of board");
        }
    }

    @Override
    public void onUndo() {
        boolean success = engine.undo();
        if (success) {
            updateView();
            view.showMessage("Move undone");
        } else {
            view.showMessage("Nothing to undo");
        }
    }

    @Override
    public void onQuit() {
        System.exit(0);
    }

    /**
     * Synchronize the view with the game engine
     * Read from the state, convert the coordinates and pass to the View
     */
    void updateView() {

        GameState gameState = engine.gameState();
        if (gameState == null) return;

        updateGameBoard(gameState);
        updateInfoPanel(gameState);
        updateHighlights(gameState);

        view.setCurrentPlayer(gameState.currentPlayerId());
        view.setUndoEnabled(!gameState.isGameOver());
    }

    void updateGameBoard(GameState gameState) {
        Map<PlayerId, Position> viewPosition = new HashMap<>();
        for (Player p: gameState.players()) {
            Position position = gameState.board().playerPosition(p.id());
            if (position != null) {
                viewPosition.put(p.id(), new Position(flipRow(position.row()), position.col()));
            }
        }

        Set<Wall> viewWalls = new HashSet<>();
        for (Wall w: gameState.board().walls()) {
            WallPosition domainPos = w.position();
            WallPosition wallPosition = new WallPosition(flipWallRow(domainPos.row()), domainPos.col());
            viewWalls.add(new Wall(wallPosition, w.orientation()));
        }

        BoardViewModel viewModel = new BoardViewModel(viewPosition, viewWalls);
        view.renderBoard(viewModel);
    }

    void updateInfoPanel(GameState gameState) {
        List<PlayerViewModel> playerViewModels = gameState.players().stream()
                .map(p -> new PlayerViewModel(
                        p.id(),
                        p.name(),
                        p.wallsRemaining(),
                        p.id().equals(gameState.currentPlayerId())
                ))
                .toList();
        view.updatePlayerInfo(playerViewModels);
    }

    void updateHighlights(GameState gameState) {

        if (gameState.isGameOver()) {
            view.clearHighlights();
            return;
        }

        PlayerId currentPlayer = gameState.currentPlayerId();
        Set<Position> domainMoves = engine.legalPawnDestinationsForPlayer(currentPlayer);

        Set<Position> highMoves = new HashSet<>();

        for (Position pos: domainMoves) {
            Position move = new Position(flipRow(pos.row()), pos.col());
            highMoves.add(move);
        }

        view.highlightValidMoves(highMoves);
    }
}

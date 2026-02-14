package it.units.quoridor.controller;

import it.units.quoridor.domain.*;
import it.units.quoridor.engine.GameEngine;
import it.units.quoridor.engine.MoveResult;
import it.units.quoridor.view.BoardViewModel;
import it.units.quoridor.view.GameView;
import it.units.quoridor.view.ViewListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Controller implements ViewListener {

    private final GameEngine engine;
    private final GameView view;

    // The board is a 9x9
    // Index board goes from 0 to 8
    private static final int MAX_ROW_INDEX = 8;
    // Wall are intersection so 8x8, index goes from 0 to 7
    private static final int MAX_WALL_INDEX = 7;

    // Constructor of the controller
    public Controller(GameEngine gameEngine, GameView gameView) {
        this.engine = gameEngine;
        this.view = gameView;
        this.view.setListener(this);
    }

    // Start a new game
    @Override
    public void onNewGame(int playerCount) {
        // engine.setup();
        updateView();
    }

    /**
     * Manage the click on a cell
     * Converts the coordinate from the view to the logic for the engine and update the view
     *
     * @param row_e the row clicked (0-8)
     * @param col the column clicked (0-8)
     */
    @Override
    public void onCellClicked(int row_e, int col) {

        // Actual context of the game
        GameState gameState = engine.getGameState();
        Player currentPlayer = gameState.currentPlayer();
        Position currentPosition = gameState.board().playerPosition(currentPlayer.id());

        // Change of coordinates
        int row = MAX_ROW_INDEX -  row_e;

        // Target position for the engine
        Position targetPosition = new Position(row, col);

        // Check if the current position and the target position are adjacent
        if (isAdjacent(currentPosition, targetPosition)) {

            Direction direction = calculateDirection(currentPosition, targetPosition);      // Calculate the direction
            MoveResult moveResult = engine.movePawn(currentPlayer.id(), direction);         // Check the rules of the game

            // Update the view and check the victory
            if (moveResult == MoveResult.OK) {
                updateView();
            } else if (moveResult == MoveResult.WIN) {
                updateView();
                view.showMessage(currentPlayer.name() + " win!");
            }
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

        // Info about the player
        Player currentPlayer = engine.getGameState().currentPlayer();

        try {
            // Change of the coordinates for the engine
            // The wall lives in the intersection -> 8x8 grid
            WallPosition wallPosition = new WallPosition(MAX_WALL_INDEX - row, col);

            // Ask the engine to place the wall
            // Control if the player has wall to place, if they overlap or block the path
            MoveResult result = engine.placeWall(currentPlayer.id(), wallPosition, orientation);

            // Check the result
            if (result == MoveResult.OK) {
                updateView();
            } else if (result == MoveResult.INVALID) {
                view.showMessage("Invalid Wall Placement");
            }
        } catch (IllegalArgumentException e) {
            view.showMessage("Invalid Wall Placement, outside of board");
        }
    }

    @Override
    public void onUndo() {
        // engine.undo();
        updateView();
    }

    @Override
    public void onQuit() {
        // engine.quit();
        updateView();
        // System.exit(0);
    }

    // Method to calculate if the cell clicked is adjacent to the current position // not private to enable test
    boolean isAdjacent(Position curPos, Position target) {
        int dRow = Math.abs(curPos.row() - target.row());
        int dCol = Math.abs(curPos.col() - target.col());
        return (dRow + dCol) == 1;
    }

    // Method to convert the coordinates into direction // not private to enable test
    Direction calculateDirection(Position curPos, Position target) {
        if (curPos.row() < target.row()) return Direction.NORTH;
        if (curPos.row() > target.row()) return Direction.SOUTH;
        if (curPos.col() < target.col()) return Direction.EAST;
        return Direction.WEST;
    }

    /**
     * Synchronize the view with the game engine
     * Read from the state, convert the coordinates and pass to the View
     */
    void updateView() {

        // Take the actual game state
        GameState gameState = engine.getGameState();
        if (gameState == null) return;

        // Associate each player id and its position
        Map<PlayerId, Position> viewPosition = new HashMap<>();

        // For each player game state we take the id and the position and add them to viewPosition
        for (Player p: gameState.players()) {
            Position position = gameState.board().playerPosition(p.id());
            if (position != null ) {
                // The position is converted
                viewPosition.put(p.id(), new Position(MAX_ROW_INDEX-position.row(), position.col()));
            }
        }

        // Set so we don't have duplicate of walls
        Set<Wall> viewWalls = new HashSet<>();

        // For each wall in the game state convert the row index and add them to the viewWalls
        for (Wall w: gameState.board().walls()) {
            WallPosition domainPos = w.position();
            WallPosition wallPosition = new WallPosition(MAX_WALL_INDEX-domainPos.row(), domainPos.col());
            viewWalls.add(new Wall(wallPosition, w.orientation()));
        }

        // viewModel contain the data for the view
        BoardViewModel viewModel = new BoardViewModel(viewPosition, viewWalls);
        // Update the current view
        view.renderBoard(viewModel);

        // Update the current player
        view.setCurrentPlayer(gameState.currentPlayerId());
    }
}

package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;
import it.units.quoridor.domain.Wall;
import it.units.quoridor.domain.WallOrientation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Panel that renders the 9x9 Quoridor board.
 *
 * Responsibilities:
 * - Draw grid lines for the 9x9 cell board
 * - Render player pawns at their positions
 * - Render placed walls
 * - Highlight valid move destinations
 * - Detect and forward clicks to ViewListener
 */
public class BoardPanel extends JPanel {

    // Layout constants
    private static final int CELL_SIZE = 60;
    private static final int GRID_SIZE = 9;
    private static final int WALL_THICKNESS = 6;
    private static final int BOARD_SIZE = CELL_SIZE * GRID_SIZE;
    private static final int PADDING = 20;

    // Colors
    private static final Color GRID_COLOR = new Color(100, 100, 100);
    private static final Color CELL_COLOR = new Color(240, 240, 240);
    private static final Color HIGHLIGHT_COLOR = new Color(144, 238, 144, 150);  // Light green, semi-transparent
    private static final Color WALL_COLOR = new Color(139, 69, 19);  // Brown
    private static final Color HOVER_CELL_COLOR = new Color(173, 216, 230, 100);  // Light blue, semi-transparent
    private static final Color HOVER_WALL_COLOR = new Color(255, 165, 0, 150);  // Orange, semi-transparent

    // State
    private BoardViewModel currentBoard;
    private Set<Position> highlightedCells;
    private ViewListener listener;

    // Hover state
    private Position hoveredCell;
    private WallHover hoveredWall;

    // Helper record for wall hover
    private record WallHover(int row, int col, WallOrientation orientation) {}

    public BoardPanel() {
        this.highlightedCells = new HashSet<>();

        setPreferredSize(new Dimension(
                BOARD_SIZE + 2 * PADDING,
                BOARD_SIZE + 2 * PADDING
        ));
        setBackground(Color.WHITE);

        // Add mouse listener for clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Clear hover when mouse leaves panel
                hoveredCell = null;
                hoveredWall = null;
                repaint();
            }
        });

        // Add mouse motion listener for hover
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e);
            }
        });
    }

    // === Public API ===

    public void render(BoardViewModel board) {
        this.currentBoard = board;
        repaint();
    }

    public void highlightCells(Set<Position> positions) {
        this.highlightedCells = new HashSet<>(positions);
        repaint();
    }

    public void clearHighlights() {
        this.highlightedCells.clear();
        repaint();
    }

    public void setViewListener(ViewListener listener) {
        this.listener = listener;
    }

    // === Rendering ===

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw components in order
        drawCells(g2d);
        drawGrid(g2d);
        drawHighlights(g2d);
        drawHover(g2d);  // Draw hover after highlights but before walls/pawns

        if (currentBoard != null) {
            drawWalls(g2d);
            drawPawns(g2d);
        }
    }

    private void drawCells(Graphics2D g) {
        g.setColor(CELL_COLOR);
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = PADDING + col * CELL_SIZE;
                int y = PADDING + row * CELL_SIZE;
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        g.setStroke(new BasicStroke(1));

        // Draw vertical lines
        for (int col = 0; col <= GRID_SIZE; col++) {
            int x = PADDING + col * CELL_SIZE;
            g.drawLine(x, PADDING, x, PADDING + BOARD_SIZE);
        }

        // Draw horizontal lines
        for (int row = 0; row <= GRID_SIZE; row++) {
            int y = PADDING + row * CELL_SIZE;
            g.drawLine(PADDING, y, PADDING + BOARD_SIZE, y);
        }
    }

    private void drawHighlights(Graphics2D g) {
        g.setColor(HIGHLIGHT_COLOR);
        for (Position pos : highlightedCells) {
            int x = PADDING + pos.col() * CELL_SIZE;
            int y = PADDING + pos.row() * CELL_SIZE;
            g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
        }
    }

    private void drawHover(Graphics2D g) {
        // Draw hovered cell
        if (hoveredCell != null) {
            g.setColor(HOVER_CELL_COLOR);
            int x = PADDING + hoveredCell.col() * CELL_SIZE;
            int y = PADDING + hoveredCell.row() * CELL_SIZE;
            g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
        }

        // Draw hovered wall
        if (hoveredWall != null) {
            g.setColor(HOVER_WALL_COLOR);
            int wallRow = hoveredWall.row();
            int wallCol = hoveredWall.col();

            if (hoveredWall.orientation() == WallOrientation.HORIZONTAL) {
                int x = PADDING + wallCol * CELL_SIZE;
                int y = PADDING + (wallRow + 1) * CELL_SIZE - WALL_THICKNESS / 2;
                int width = 2 * CELL_SIZE;
                int height = WALL_THICKNESS;
                g.fillRect(x, y, width, height);
            } else {
                int x = PADDING + (wallCol + 1) * CELL_SIZE - WALL_THICKNESS / 2;
                int y = PADDING + wallRow * CELL_SIZE;
                int width = WALL_THICKNESS;
                int height = 2 * CELL_SIZE;
                g.fillRect(x, y, width, height);
            }
        }
    }

    private void drawWalls(Graphics2D g) {
        if (currentBoard == null) return;

        g.setColor(WALL_COLOR);
        for (Wall wall : currentBoard.walls()) {
            drawWall(g, wall);
        }
    }

    private void drawWall(Graphics2D g, Wall wall) {
        int wallRow = wall.position().row();
        int wallCol = wall.position().col();

        if (wall.orientation() == WallOrientation.HORIZONTAL) {
            // Horizontal wall: spans 2 cells horizontally
            int x = PADDING + wallCol * CELL_SIZE;
            int y = PADDING + (wallRow + 1) * CELL_SIZE - WALL_THICKNESS / 2;
            int width = 2 * CELL_SIZE;
            int height = WALL_THICKNESS;
            g.fillRect(x, y, width, height);
        } else {
            // Vertical wall: spans 2 cells vertically
            int x = PADDING + (wallCol + 1) * CELL_SIZE - WALL_THICKNESS / 2;
            int y = PADDING + wallRow * CELL_SIZE;
            int width = WALL_THICKNESS;
            int height = 2 * CELL_SIZE;
            g.fillRect(x, y, width, height);
        }
    }

    private void drawPawns(Graphics2D g) {
        if (currentBoard == null) return;

        for (Map.Entry<PlayerId, Position> entry : currentBoard.playerPositions().entrySet()) {
            PlayerId playerId = entry.getKey();
            Position position = entry.getValue();
            drawPawn(g, playerId, position);
        }
    }

    private void drawPawn(Graphics2D g, PlayerId playerId, Position position) {
        // Calculate center of cell
        int centerX = PADDING + position.col() * CELL_SIZE + CELL_SIZE / 2;
        int centerY = PADDING + position.row() * CELL_SIZE + CELL_SIZE / 2;
        int radius = CELL_SIZE / 3;

        // Choose color based on player
        Color pawnColor = getPawnColor(playerId);

        // Draw pawn as a circle
        g.setColor(pawnColor);
        g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Draw border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    private Color getPawnColor(PlayerId playerId) {
        return switch (playerId) {
            case PLAYER_1 -> new Color(220, 20, 60);    // Crimson
            case PLAYER_2 -> new Color(30, 144, 255);   // Dodger Blue
            case PLAYER_3 -> new Color(50, 205, 50);    // Lime Green
            case PLAYER_4 -> new Color(255, 215, 0);    // Gold
        };
    }

    // === Mouse Handling ===

    private void handleMouseMove(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Check if mouse is within board bounds
        if (mouseX < PADDING || mouseX > PADDING + BOARD_SIZE ||
                mouseY < PADDING || mouseY > PADDING + BOARD_SIZE) {
            hoveredCell = null;
            hoveredWall = null;
            repaint();
            return;
        }

        // Convert pixel coordinates to grid coordinates (float)
        float gridX = (mouseX - PADDING) / (float) CELL_SIZE;
        float gridY = (mouseY - PADDING) / (float) CELL_SIZE;

        // Get the cell we're in
        int cellRow = (int) gridY;
        int cellCol = (int) gridX;

        // Ensure within valid cell range
        if (cellRow < 0 || cellRow >= GRID_SIZE || cellCol < 0 || cellCol >= GRID_SIZE) {
            hoveredCell = null;
            hoveredWall = null;
            repaint();
            return;
        }

        // Get position within the cell (0.0 to 1.0)
        float localX = gridX - cellCol;
        float localY = gridY - cellRow;

        // Edge threshold (30% from edge = wall zone)
        float edgeThreshold = 0.3f;

        WallHover newWall = null;
        Position newCell = null;

        // Check if hovering near an edge
        if (localY < edgeThreshold) {
            // Near TOP edge of cell - horizontal wall above this cell
            if (cellRow > 0) {
                // Determine wall position based on which half of the edge
                int wallCol = (localX < 0.5f) ? Math.max(0, cellCol - 1) : cellCol;
                if (wallCol <= 7) {
                    newWall = new WallHover(cellRow - 1, wallCol, WallOrientation.HORIZONTAL);
                }
            }
        } else if (localY > (1.0f - edgeThreshold)) {
            // Near BOTTOM edge of cell - horizontal wall below this cell
            if (cellRow < GRID_SIZE - 1) {
                // Determine wall position based on which half of the edge
                int wallCol = (localX < 0.5f) ? Math.max(0, cellCol - 1) : cellCol;
                if (wallCol <= 7) {
                    newWall = new WallHover(cellRow, wallCol, WallOrientation.HORIZONTAL);
                }
            }
        } else if (localX < edgeThreshold) {
            // Near LEFT edge of cell - vertical wall to the left
            if (cellCol > 0) {
                // Determine wall position based on which half of the edge
                int wallRow = (localY < 0.5f) ? Math.max(0, cellRow - 1) : cellRow;
                if (wallRow <= 7) {
                    newWall = new WallHover(wallRow, cellCol - 1, WallOrientation.VERTICAL);
                }
            }
        } else if (localX > (1.0f - edgeThreshold)) {
            // Near RIGHT edge of cell - vertical wall to the right
            if (cellCol < GRID_SIZE - 1) {
                // Determine wall position based on which half of the edge
                int wallRow = (localY < 0.5f) ? Math.max(0, cellRow - 1) : cellRow;
                if (wallRow <= 7) {
                    newWall = new WallHover(wallRow, cellCol, WallOrientation.VERTICAL);
                }
            }
        } else {
            // In the center of the cell
            newCell = new Position(cellRow, cellCol);
        }

        // Update hover state if changed
        if ((newWall != null && !newWall.equals(hoveredWall)) ||
                (newCell != null && !newCell.equals(hoveredCell)) ||
                (newWall == null && newCell == null && (hoveredWall != null || hoveredCell != null))) {
            hoveredWall = newWall;
            hoveredCell = newCell;
            repaint();
        }
    }

    private void handleMouseClick(MouseEvent e) {
        if (listener == null) return;

        // Simply use the current hover state to determine what to click
        if (hoveredWall != null) {
            listener.onWallPlacement(
                    hoveredWall.row(),
                    hoveredWall.col(),
                    hoveredWall.orientation()
            );
        } else if (hoveredCell != null) {
            listener.onCellClicked(hoveredCell.row(), hoveredCell.col());
        }
    }

    // === For Testing ===

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BoardPanel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BoardPanel panel = new BoardPanel();

            // Test with some data
            Map<PlayerId, Position> positions = new HashMap<>();
            positions.put(PlayerId.PLAYER_1, new Position(8, 4));
            positions.put(PlayerId.PLAYER_2, new Position(0, 4));

            Set<Wall> walls = new HashSet<>();
            walls.add(new Wall(
                    new it.units.quoridor.domain.WallPosition(3, 4),
                    WallOrientation.HORIZONTAL
            ));
            walls.add(new Wall(
                    new it.units.quoridor.domain.WallPosition(5, 2),
                    WallOrientation.VERTICAL
            ));

            BoardViewModel board = new BoardViewModel(positions, walls);
            panel.render(board);

            // Highlight some cells
            Set<Position> highlights = new HashSet<>();
            highlights.add(new Position(7, 4));
            highlights.add(new Position(8, 3));
            highlights.add(new Position(8, 5));
            panel.highlightCells(highlights);

            // Add click listener
            panel.setViewListener(new ViewListener() {
                @Override
                public void onNewGame(int playerCount) {}

                @Override
                public void onCellClicked(int row, int col) {
                    System.out.println("Cell clicked: (" + row + ", " + col + ")");
                }

                @Override
                public void onWallPlacement(int row, int col, WallOrientation orientation) {
                    System.out.println("Wall placement: " + row + ", " + col + " " + orientation);
                }

                @Override
                public void onUndo() {}

                @Override
                public void onQuit() {}
            });

            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
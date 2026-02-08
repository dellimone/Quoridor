package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Swing implementation of GameView (Humble Dialog pattern).
 *
 * This is the main game window containing:
 * - BoardPanel (center) - renders the 9x9 grid
 * - PlayerInfoPanel (right) - shows player info
 * - Control panel (bottom) - buttons and messages
 */
public class SwingGameView extends JFrame implements GameView {

    // Components
    private JPanel boardPanel;           // Placeholder for now
    private PlayerInfoPanel playerInfoPanel;
    private JButton undoButton;
    private JButton newGameButton;
    private JLabel messageLabel;

    // Listener
    private ViewListener listener;

    public SwingGameView() {
        setTitle("Quoridor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initializeComponents();
        layoutComponents();

        pack();
        setLocationRelativeTo(null);  // Center on screen
    }

    private void initializeComponents() {
        // Placeholder panels (will be replaced with real implementations)
        boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(600, 600));
        boardPanel.setBackground(Color.LIGHT_GRAY);
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        playerInfoPanel = new PlayerInfoPanel();

        // Control buttons
        undoButton = new JButton("Undo");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> {
            if (listener != null) {
                listener.onUndo();
            }
        });

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            if (listener != null) {
                // TODO: Show dialog to select player count
                listener.onNewGame(2);  // Default to 2 players for now
            }
        });

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void layoutComponents() {
        // Board in center
        add(boardPanel, BorderLayout.CENTER);

        // Player info on right
        add(playerInfoPanel, BorderLayout.EAST);

        // Control panel at top
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Message in center of control panel
        controlPanel.add(messageLabel, BorderLayout.CENTER);

        // Buttons on right of control panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(undoButton);
        buttonPanel.add(newGameButton);
        controlPanel.add(buttonPanel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.NORTH);
    }

    // === GameView Interface Implementation ===

    @Override
    public void renderBoard(BoardViewModel board) {
        // TODO: Delegate to BoardPanel when implemented
        System.out.println("Render board: " + board.playerPositions().size() + " players, "
                + board.walls().size() + " walls");
    }

    @Override
    public void highlightValidMoves(Set<Position> positions) {
        // TODO: Delegate to BoardPanel when implemented
        System.out.println("Highlight valid moves: " + positions);
    }

    @Override
    public void clearHighlights() {
        // TODO: Delegate to BoardPanel when implemented
        System.out.println("Clear highlights");
    }

    @Override
    public void updatePlayerInfo(List<PlayerViewModel> players) {
        playerInfoPanel.updatePlayers(players);
    }

    @Override
    public void setCurrentPlayer(PlayerId player) {
        playerInfoPanel.setCurrentPlayer(player);
    }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void showError(String error) {
        JOptionPane.showMessageDialog(
                this,
                error,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showGameOver(PlayerId winner) {
        JOptionPane.showMessageDialog(
                this,
                winner + " wins!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void setUndoEnabled(boolean enabled) {
        undoButton.setEnabled(enabled);
    }

    @Override
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    // === For Testing ===

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGameView view = new SwingGameView();

            // Set up a test listener
            view.setListener(new ViewListener() {
                @Override
                public void onNewGame(int playerCount) {
                    System.out.println("New game: " + playerCount + " players");
                    view.showMessage("Game started!");
                }

                @Override
                public void onCellClicked(int row, int col) {
                    System.out.println("Cell clicked: (" + row + ", " + col + ")");
                }

                @Override
                public void onWallPlacement(int row, int col, it.units.quoridor.domain.WallOrientation orientation) {
                    System.out.println("Wall placement: (" + row + ", " + col + ") " + orientation);
                }

                @Override
                public void onUndo() {
                    System.out.println("Undo clicked");
                    view.showMessage("Move undone");
                }

                @Override
                public void onQuit() {
                    System.out.println("Quit");
                    System.exit(0);
                }
            });

            List<PlayerViewModel> testPlayers = List.of(
                    new PlayerViewModel(PlayerId.PLAYER_1, "Alice", 10, true),
                    new PlayerViewModel(PlayerId.PLAYER_2, "Bob", 7, false)
            );
            view.updatePlayerInfo(testPlayers);
            view.setCurrentPlayer(PlayerId.PLAYER_1);
            view.showMessage("Player 1's turn");

            view.setVisible(true);
        });
    }
}
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
    private BoardPanel boardPanel;
    private PlayerInfoPanel playerInfoPanel;
    private JButton undoButton;
    private JButton newGameButton;
    private JLabel messageLabel;
    private JPanel overlay;

    // Auto-clear timer for transient messages
    private Timer messageClearTimer;

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

        // Show welcome screen on startup
        showWelcomeScreen();
    }

    private void initializeComponents() {
        boardPanel = new BoardPanel();
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
                listener.onNewGame(2);
            }
        });

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void layoutComponents() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(700, 700));

        // Board in base layer
        boardPanel.setBounds(0, 0, 700, 700);
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);

        // Single overlay (initially hidden)
        overlay = createOverlayPanel();
        overlay.setBounds(0, 0, 700, 700);
        layeredPane.add(overlay, JLayeredPane.MODAL_LAYER);
        overlay.setVisible(false);

        // Make layers resize with the window
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = layeredPane.getWidth();
                int height = layeredPane.getHeight();

                // Force board to resize (it has a fixed preferred size)
                boardPanel.setBounds(0, 0, width, height);
                boardPanel.setSize(width, height);
                boardPanel.revalidate();

                overlay.setBounds(0, 0, width, height);
            }
        });

        add(layeredPane, BorderLayout.CENTER);

        // Player info on right
        add(playerInfoPanel, BorderLayout.EAST);

        // Control panel at top
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Message in center
        controlPanel.add(messageLabel, BorderLayout.CENTER);

        // Buttons on right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(undoButton);
        buttonPanel.add(newGameButton);
        controlPanel.add(buttonPanel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.NORTH);
    }

    // === GameView Interface Implementation ===

    @Override
    public void renderBoard(BoardViewModel board) {
        boardPanel.render(board);
    }

    @Override
    public void highlightValidMoves(Set<Position> positions) {
        boardPanel.highlightCells(positions);
    }

    @Override
    public void clearHighlights() {
        boardPanel.clearHighlights();
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
        displayMessage(message, Color.BLACK, false, 1000);
    }

    @Override
    public void showError(String error) {
        displayMessage(error, new Color(220, 53, 69), false, 1000);
    }

    @Override
    public void showGameOver(PlayerId winner) {
        displayMessage("🎉 " + winner + " WINS! 🎉", new Color(40, 167, 69), true, 0);
        showVictoryScreen(winner);
    }

    @Override
    public void hideOverlays() {
        if (overlay != null) {
            overlay.setVisible(false);
        }
    }

    @Override
    public void setUndoEnabled(boolean enabled) {
        undoButton.setEnabled(enabled);
    }

    @Override
    public void setListener(ViewListener listener) {
        this.listener = listener;
        boardPanel.setViewListener(listener);
    }

    // === Overlay System ===

    /**
     * Creates the reusable overlay panel with semi-transparent background
     */
    private JPanel createOverlayPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 0, 0, 200),
                    0, getHeight(), new Color(20, 20, 40, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
    }

    /**
     * Shows the welcome screen
     */
    private void showWelcomeScreen() {
        overlay.removeAll();
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 20, 15, 20);

        // Title
        gbc.gridy = 0;
        overlay.add(createLabel("QUORIDOR", 72, new Color(255, 215, 0), true), gbc);

        // Subtitle
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 25, 20);
        overlay.add(createLabel("The Strategic Board Game", 18, new Color(220, 220, 220), false), gbc);

        // Start button
        gbc.gridy = 2;
        gbc.insets = new Insets(25, 20, 15, 20);
        overlay.add(createButton("Start New Game", 24, new Color(40, 167, 69), 280, 60), gbc);

        // Instructions
        gbc.gridy = 3;
        gbc.insets = new Insets(25, 20, 15, 20);
        JLabel instructions = createLabel(
            "<html><center>Reach the opposite side before your opponent<br>Use walls to block their path!</center></html>",
            14, new Color(200, 200, 200), false
        );
        instructions.setFont(instructions.getFont().deriveFont(Font.ITALIC));
        overlay.add(instructions, gbc);

        overlay.setVisible(true);
        overlay.revalidate();
        overlay.repaint();
    }

    /**
     * Shows the victory screen
     */
    private void showVictoryScreen(PlayerId winner) {
        overlay.removeAll();
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 20, 15, 20);

        // Trophy
        gbc.gridy = 0;
        overlay.add(createLabel("👑", 90, Color.WHITE, false), gbc);

        // Victory text
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 10, 20);
        overlay.add(createLabel("VICTORY!", 56, new Color(255, 215, 0), true), gbc);

        // Winner
        gbc.gridy = 2;
        overlay.add(createLabel(winner + " WINS!", 36, Color.WHITE, true), gbc);

        // New Game button
        gbc.gridy = 3;
        gbc.insets = new Insets(35, 20, 15, 20);
        overlay.add(createButton("Play Again", 20, new Color(40, 167, 69), 220, 55), gbc);

        overlay.setVisible(true);
        overlay.revalidate();
        overlay.repaint();
    }

    /**
     * Helper to create styled labels
     */
    private JLabel createLabel(String text, int fontSize, Color color, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(color);
        return label;
    }

    /**
     * Helper to create styled buttons
     */
    private JButton createButton(String text, int fontSize, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        button.addActionListener(e -> {
            hideOverlays();
            if (listener != null) {
                listener.onNewGame(2);
            }
        });

        return button;
    }

    /**
     * Display a styled message with optional auto-clear
     */
    private void displayMessage(String message, Color color, boolean bold, int autoClearMs) {
        if (messageClearTimer != null && messageClearTimer.isRunning()) {
            messageClearTimer.stop();
        }

        String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        String styledMessage = String.format(
            "<html><span style='font-size:%dpx; color:%s;%s'>%s</span></html>",
            bold ? 16 : 14,
            colorHex,
            bold ? " font-weight:bold;" : "",
            message
        );

        messageLabel.setText(styledMessage);

        if (autoClearMs > 0) {
            messageClearTimer = new Timer(autoClearMs, e -> messageLabel.setText(" "));
            messageClearTimer.setRepeats(false);
            messageClearTimer.start();
        }
    }

    // === For Testing ===

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGameView view = new SwingGameView();

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
                    System.exit(0);
                }
            });

            view.setVisible(true);
        });
    }
}
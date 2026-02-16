package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

    // Remembers last player count for "Play Again"
    private int lastPlayerCount = 2;

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
        newGameButton.addActionListener(e -> showWelcomeScreen());

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
    public void showGameOver(String winnerName) {
        displayMessage("🎉 " + winnerName + " WINS! 🎉", new Color(40, 167, 69), true, 0);
        showVictoryScreen(winnerName);
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
        JPanel panel = new JPanel() {
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

        // Consume all mouse events so they don't reach the board behind
        panel.addMouseListener(new java.awt.event.MouseAdapter() {});
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {});

        return panel;
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

        // Player count buttons
        gbc.gridy = 2;
        gbc.insets = new Insets(25, 20, 10, 20);
        overlay.add(createPlayerCountButton("2 Players", 24, new Color(40, 167, 69), 280, 60, 2), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 15, 20);
        overlay.add(createPlayerCountButton("4 Players", 24, new Color(0, 123, 255), 280, 60, 4), gbc);

        // Instructions
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 20, 2, 20);
        JLabel line1 = createLabel("Reach the opposite side before your opponent", 14, new Color(200, 200, 200), false);
        line1.setFont(line1.getFont().deriveFont(Font.ITALIC));
        overlay.add(line1, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(2, 20, 15, 20);
        JLabel line2 = createLabel("Use walls to block their path!", 14, new Color(200, 200, 200), false);
        line2.setFont(line2.getFont().deriveFont(Font.ITALIC));
        overlay.add(line2, gbc);

        overlay.setVisible(true);
        overlay.revalidate();
        overlay.repaint();
    }

    /**
     * Shows the victory screen
     */
    private void showVictoryScreen(String winnerName) {
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
        overlay.add(createLabel(winnerName + " WINS!", 36, Color.WHITE, true), gbc);

        // Play Again button (goes to name entry with last player count)
        gbc.gridy = 3;
        gbc.insets = new Insets(35, 20, 15, 20);
        overlay.add(createPlayerCountButton("Play Again", 20, new Color(40, 167, 69), 220, 55, lastPlayerCount), gbc);

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
     * Shows name entry screen for the given number of players.
     */
    private void showNameEntryScreen(int playerCount) {
        lastPlayerCount = playerCount;
        overlay.removeAll();
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Title
        gbc.gridy = 0;
        overlay.add(createLabel("Enter Player Names", 36, new Color(255, 215, 0), true), gbc);

        // Name fields
        List<JTextField> nameFields = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(8, 20, 8, 5);
            overlay.add(createLabel("Player " + i + ":", 18, Color.WHITE, false), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(8, 5, 8, 20);
            JTextField field = new JTextField("Player " + i, 15);
            field.setFont(new Font("SansSerif", Font.PLAIN, 18));
            nameFields.add(field);
            overlay.add(field, gbc);

            gbc.gridx = 0;
        }

        // Start button
        gbc.gridy = playerCount + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 20, 15, 20);
        JButton startButton = createStyledButton("Start Game", 24, new Color(40, 167, 69), 280, 60);
        startButton.addActionListener(e -> {
            List<String> names = new ArrayList<>();
            for (int idx = 0; idx < nameFields.size(); idx++) {
                String text = nameFields.get(idx).getText().trim();
                names.add(text.isEmpty() ? "Player " + (idx + 1) : text);
            }
            hideOverlays();
            if (listener != null) {
                listener.onNewGame(playerCount, names);
            }
        });
        overlay.add(startButton, gbc);

        // Back button
        gbc.gridy = playerCount + 2;
        gbc.insets = new Insets(5, 20, 15, 20);
        JButton backButton = createStyledButton("Back", 16, new Color(108, 117, 125), 140, 40);
        backButton.addActionListener(e -> showWelcomeScreen());
        overlay.add(backButton, gbc);

        overlay.setVisible(true);
        overlay.revalidate();
        overlay.repaint();

        // Focus first name field
        nameFields.get(0).requestFocusInWindow();
        nameFields.get(0).selectAll();
    }

    /**
     * Helper to create styled buttons that navigate to name entry.
     */
    private JButton createPlayerCountButton(String text, int fontSize, Color bgColor, int width, int height, int playerCount) {
        JButton button = createStyledButton(text, fontSize, bgColor, width, height);
        button.addActionListener(e -> showNameEntryScreen(playerCount));
        return button;
    }

    /**
     * Helper to create a styled button (no action listener).
     */
    private JButton createStyledButton(String text, int fontSize, Color bgColor, int width, int height) {
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

        return button;
    }

    /**
     * Display a styled message with optional auto-clear
     */
    private void displayMessage(String message, Color color, boolean bold, int autoClearMs) {
        if (messageClearTimer != null && messageClearTimer.isRunning()) {
            messageClearTimer.stop();
        }

        messageLabel.setText(message);
        messageLabel.setForeground(color);
        messageLabel.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, bold ? 16 : 14));

        if (autoClearMs > 0) {
            messageClearTimer = new Timer(autoClearMs, e -> messageLabel.setText(" "));
            messageClearTimer.setRepeats(false);
            messageClearTimer.start();
        }
    }
}
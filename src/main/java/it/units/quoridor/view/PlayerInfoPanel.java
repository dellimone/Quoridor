package it.units.quoridor.view;

import it.units.quoridor.domain.PlayerId;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel that displays information about all players in the game.
 *
 * Shows for each player:
 * - Player name
 * - Walls remaining
 * - Visual indicator if it's their turn (highlighted)
 */
public class PlayerInfoPanel extends JPanel {

    private static final Color CURRENT_PLAYER_BG = new Color(255, 255, 200);  // Light yellow
    private static final Color NORMAL_BG = Color.WHITE;
    private static final Border CURRENT_PLAYER_BORDER = BorderFactory.createLineBorder(
            new Color(255, 215, 0), 3  // Gold border
    );
    private static final Border NORMAL_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);

    // Store player cards for updating
    private final Map<PlayerId, PlayerCard> playerCards;

    public PlayerInfoPanel() {
        this.playerCards = new HashMap<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 600));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Players"));
    }

    /**
     * Update the panel with current player information.
     */
    public void updatePlayers(List<PlayerViewModel> players) {
        // Clear existing cards
        removeAll();
        playerCards.clear();

        // Create a card for each player
        for (PlayerViewModel player : players) {
            PlayerCard card = new PlayerCard(player);
            playerCards.put(player.id(), card);
            add(card);
            add(Box.createVerticalStrut(10));  // Spacing between cards
        }

        // Fill remaining space
        add(Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    /**
     * Highlight the current player's card.
     */
    public void setCurrentPlayer(PlayerId currentPlayer) {
        for (Map.Entry<PlayerId, PlayerCard> entry : playerCards.entrySet()) {
            boolean isCurrent = entry.getKey().equals(currentPlayer);
            entry.getValue().setHighlighted(isCurrent);
        }
    }

    // === Inner Class: PlayerCard ===

    /**
     * A card representing a single player's information.
     */
    private static class PlayerCard extends JPanel {

        public PlayerCard(PlayerViewModel player) {

            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createCompoundBorder(
                    NORMAL_BORDER,
                    new EmptyBorder(10, 10, 10, 10)
            ));
            setBackground(NORMAL_BG);
            setMaximumSize(new Dimension(180, 80));

            // Color indicator (small colored square)
            JPanel colorIndicator = new JPanel();
            colorIndicator.setPreferredSize(new Dimension(20, 20));
            colorIndicator.setBackground(getPlayerColor(player.id()));
            colorIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Player name
            JLabel nameLabel = new JLabel(player.name());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            // Walls remaining
            JLabel wallsLabel = new JLabel("Walls: " + player.wallsRemaining());
            wallsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            // Layout
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            topPanel.setOpaque(false);
            topPanel.add(colorIndicator);
            topPanel.add(nameLabel);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.add(topPanel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(wallsLabel);

            add(infoPanel, BorderLayout.CENTER);
        }

        public void setHighlighted(boolean highlighted) {
            if (highlighted) {
                setBackground(CURRENT_PLAYER_BG);
                setBorder(BorderFactory.createCompoundBorder(
                        CURRENT_PLAYER_BORDER,
                        new EmptyBorder(10, 10, 10, 10)
                ));
            } else {
                setBackground(NORMAL_BG);
                setBorder(BorderFactory.createCompoundBorder(
                        NORMAL_BORDER,
                        new EmptyBorder(10, 10, 10, 10)
                ));
            }
            repaint();
        }

        private Color getPlayerColor(PlayerId playerId) {
            return switch (playerId) {
                case PLAYER_1 -> new Color(220, 20, 60);    // Crimson
                case PLAYER_2 -> new Color(30, 144, 255);   // Dodger Blue
                case PLAYER_3 -> new Color(50, 205, 50);    // Lime Green
                case PLAYER_4 -> new Color(255, 215, 0);    // Gold
            };
        }
    }
}
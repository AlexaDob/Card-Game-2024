import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CombinedInterface {
    private JFrame frame;
    private Game game;
    //private boolean bsCalled = false;
    
    // Components for card selection scene
    private java.util.List<JCheckBox> checkBoxes;
    private JTextArea selectedArea;
    private JButton addButton;
    private JPanel checkboxPanel;
    private Player currentPlayer;
    
    // NEW: Store the game log to preserve it between player switches
    private String gameLog = "";
    
    public CombinedInterface() {
        frame = new JFrame("Game of BS");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        StartingScreen();
        frame.setVisible(true);
    }
    
    //Starting Screen: Welcoms the PLayer
   private void StartingScreen(){
        frame.getContentPane().removeAll();
        frame.setLayout(new GridBagLayout()); // Centers everything
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JLabel welcomeLabel = new JLabel("Welcome to Liar's Club!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        JButton startButton = new JButton("Start");
        startButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        
        startButton.addActionListener(e -> {
            showPlayerSelectionScene();
        });
        
        // Add components with spacing
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30))); // 30px spacing
        mainPanel.add(startButton);
        
        frame.add(mainPanel); // GridBagLayout centers the panel
        
        frame.revalidate();
        frame.repaint();
    }
    
    // Scene 1: Player selection
    private void showPlayerSelectionScene() {
        // Clear the frame
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());
        
        // Create components for player selection
        JLabel nameLabel = new JLabel("Enter the Amount of Players: *Player count has to be 2-4 Players*", JLabel.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField numofPlayers = new JTextField(15);
        JButton submitButton = new JButton("Enter");
        JLabel resultLabel = new JLabel("");
        JLabel warningLabel = new JLabel("*GAME STARTS IMMEDIATELY! GIVE DEVICE TO PLAYER 1*");
        warningLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        warningLabel.setForeground(Color.RED);
        
        // Button action
        submitButton.addActionListener(e -> {
            try {
                int number = Integer.parseInt(numofPlayers.getText());
                if (number >= 2 && number <= 4) {
                    game = new Game(number);
                    gameLog = "=== GAME STARTED ===\n"; // Initialize game log
                    gameLog += "It's a " + game.getSetCard().getName() + "'s table \n\n";
                    showCardSelectionScene(); // Switch to card selection scene
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter 2-4 players!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number!");
            }
        });
        
        // Add components
        frame.add(nameLabel);
        frame.add(numofPlayers);
        frame.add(submitButton);
        frame.add(resultLabel);
        frame.add(warningLabel);
        
        // Refresh the display
        frame.revalidate();
        frame.repaint();
    }
    
    // Scene 2: Card selection interface
    private void showCardSelectionScene() {
        // Clear the frame
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        
        // Get current player
        currentPlayer = game.getCurrentPlayer(); // Use getCurrentPlayer instead of getPlayer(0)
        
        // Update title
        frame.setTitle("Liar's Club - " + currentPlayer.getName() + "'s Turn");
        
        // Create top panel with game info
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Select cards to play - " + currentPlayer.getName() + " Lives: " + currentPlayer.getLives(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel with checkboxes
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBorder(BorderFactory.createTitledBorder("Your Cards:"));
        checkBoxes = new ArrayList<>();
        
        // Create checkboxes for current player's cards
        for (int i = 0; i < currentPlayer.getHandSize(); i++) {
            JCheckBox checkBox = new JCheckBox(currentPlayer.getCard(i).toString());
            checkBoxes.add(checkBox);
            checkboxPanel.add(checkBox);
        }
        
        JScrollPane checkboxScrollPane = new JScrollPane(checkboxPanel);
        checkboxScrollPane.setPreferredSize(new Dimension(300, 300));
        
        // Text area to display game log - PRESERVE EXISTING LOG
        selectedArea = new JTextArea();
        selectedArea.setEditable(false);
        
        // Set the text to include all previous game log + current player info
        gameLog += "Current player: " + currentPlayer.getName() + " (Lives: " + currentPlayer.getLives() + ")\n";
        selectedArea.setText(gameLog);
        
        // Auto-scroll to bottom
        selectedArea.setCaretPosition(selectedArea.getDocument().getLength());
        
        JScrollPane textScrollPane = new JScrollPane(selectedArea);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Play Cards button
        addButton = new JButton("Play Cards");
        addButton.addActionListener(e -> {
            ArrayList<Integer> selectedIndices = new ArrayList<>();
            ArrayList<JCheckBox> checkboxesToRemove = new ArrayList<>();
            ArrayList<Cards> playedCards = new ArrayList<>();

            // Collect selected indices and checkboxes
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selectedIndices.add(i);
                    checkboxesToRemove.add(checkBoxes.get(i));
                    playedCards.add(currentPlayer.getCard(i));
                }
            }
            
            int count = selectedIndices.size();
            if (count == 0) {
                JOptionPane.showMessageDialog(frame, "Please select cards first!");
                return;
            }
            
            // Record the play FIRST
            game.recordPlay(currentPlayer, playedCards);
            
            // Create the message
            String message = currentPlayer.getName() + " has laid down " + count + " " + game.getSetCard().getName() + "(s)\n\n";
            
            // Add to game log AND display immediately
            gameLog += message;
            selectedArea.append(message);
            
            // Debug prints
            System.out.println("Message added: " + message);
            System.out.println("Current game log: " + gameLog);
            
            // Remove cards from player hand (reverse order)
            Collections.sort(selectedIndices, Collections.reverseOrder());
            for (int index : selectedIndices) {
                currentPlayer.removeCard(index);
            }
            
            // INLINE REFRESH - Remove the selected checkboxes from GUI
            for (JCheckBox checkBox : checkboxesToRemove) {
                checkboxPanel.remove(checkBox);
                checkBoxes.remove(checkBox);
            }
            
            // Refresh the display
            checkboxPanel.revalidate();
            checkboxPanel.repaint();
            
            // Auto-scroll to bottom
            selectedArea.setCaretPosition(selectedArea.getDocument().getLength());
            
            // Check if player has no cards left
            if (currentPlayer.getHandSize() == 0) {
                JOptionPane.showMessageDialog(frame, currentPlayer.getName() + " has no cards left!");
                
                // Check if game is over
                if (game.isGameOver()) {
                    showGameOverScene();
                } else {
                    showNewRound();
                }
                return; // Don't continue to next player
            }
            
            // Move to next player and refresh scene
            NextPlayerScene(); // Now this preserves the game log!
        });
        
        // FIXED Call BS button
        JButton callBSButton = new JButton("Call BS");
        callBSButton.addActionListener(e -> {
            // Check if there's a previous play to call BS on
            if (game.getPreviousPlayer() == null) {
                JOptionPane.showMessageDialog(frame, "No previous play to call BS on!");
                return;
            }
            
            // Debug information
            System.out.println("=== BS CALL DEBUG ===");
            System.out.println("Current player (BS caller): " + currentPlayer.getName());
            System.out.println("Previous player: " + game.getPreviousPlayer().getName());
            System.out.println("Set card: " + game.getSetCard().getName());
            System.out.println("checkBS result: " + game.checkBS());
            
            String bsMessage = currentPlayer.getName() + " calls BS on " + game.getPreviousPlayer().getName() + "!\n";
            //bsCalled = true;
            gameLog += bsMessage;
            selectedArea.append(bsMessage);
            
            // Check if the previous play was valid
            boolean previousPlayWasValid = game.checkBS();
            
            if (previousPlayWasValid) {
                // BS call was WRONG - previous player was telling the truth
                // Current player (BS caller) loses a life
                String wrongMessage = "BS call was WRONG! " + currentPlayer.getName() + " loses a life!\n";
                gameLog += wrongMessage;
                selectedArea.append(wrongMessage);
                
                currentPlayer.rollLife();
                
                String livesMessage = currentPlayer.getName() + " now has " + currentPlayer.getLives() + " lives\n";
                gameLog += livesMessage;
                selectedArea.append(livesMessage);
                
                // Check if current player died
                if (currentPlayer.getLives() <= 0) {
                    String deathMessage = currentPlayer.getName() + " has been eliminated!\n";
                    gameLog += deathMessage;
                    selectedArea.append(deathMessage);
                }
                
                showNewRound();
                
            } else {
                // BS call was CORRECT - previous player was lying
                // Previous player loses a life
                Player previousPlayer = game.getPreviousPlayer();
                String correctMessage = "BS call was CORRECT! " + previousPlayer.getName() + " was lying and loses a life!\n";
                gameLog += correctMessage;
                selectedArea.append(correctMessage);
                
                previousPlayer.rollLife();
                
                String livesMessage = previousPlayer.getName() + " now has " + previousPlayer.getLives() + " lives\n";
                gameLog += livesMessage;
                selectedArea.append(livesMessage);
                
                // Check if previous player died
                if (previousPlayer.getLives() <= 0) {
                    String deathMessage = previousPlayer.getName() + " has been eliminated!\n";
                    gameLog += deathMessage;
                    selectedArea.append(deathMessage);
                }
                
                showNewRound();
            }
            
            gameLog += "\n";
            selectedArea.append("\n");
            
            // Check if game is over
            if (game.isGameOver()) {
                Player winner = game.getWinningPlayer();
                JOptionPane.showMessageDialog(frame, 
                    winner.getName() + " wins the game!\nThey are the last player standing!");
                showGameOverScene();
                return;
            }
            
            // Auto-scroll to bottom
            selectedArea.setCaretPosition(selectedArea.getDocument().getLength());
        });
        
        // New Game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            gameLog = ""; // Reset game log
            showPlayerSelectionScene(); // Go back to start
        });
        

        buttonPanel.add(addButton);
        buttonPanel.add(callBSButton);
        buttonPanel.add(newGameButton);
        
        // Layout setup
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(checkboxScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(textScrollPane, BorderLayout.CENTER);
        
        // Refresh the display
        frame.revalidate();
        frame.repaint();
    }

    // Scene 3: New round scene
    private void showNewRound() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        
        // Check if game should end
        if (game.isGameOver()) {
            showGameOverScene();
            return;
        }
        
                // Create main container
        JPanel mainPanel = new JPanel(new BorderLayout());

        // BS message at top
        JLabel bsMessage = new JLabel(currentPlayer.getName() + " calls BS on " + game.getPreviousPlayer().getName() + "!", JLabel.CENTER);
        bsMessage.setFont(new Font("Arial", Font.BOLD, 18));
        bsMessage.setForeground(Color.RED);
        bsMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Player info panel
        JPanel playerInfoPanel = new JPanel(new GridLayout(game.getPlayerCount(), 2));
        playerInfoPanel.setBorder(BorderFactory.createTitledBorder("Player Status"));

        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
            JLabel playerName = new JLabel(player.getName());
            playerName.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel playerStatus;
            if (player.getLives() > 0) {
                playerStatus = new JLabel("Lives: " + player.getLives());
                playerStatus.setFont(new Font("Arial", Font.PLAIN, 14));
                playerStatus.setForeground(Color.BLACK);
            } else {
                playerStatus = new JLabel("ELIMINATED");
                playerStatus.setFont(new Font("Arial", Font.BOLD, 14));
                playerStatus.setForeground(Color.RED);
            }

            playerInfoPanel.add(playerName);
            playerInfoPanel.add(playerStatus);
        }

        // Combine them
        mainPanel.add(bsMessage, BorderLayout.NORTH);
        mainPanel.add(playerInfoPanel, BorderLayout.CENTER);

        // Add main panel to frame
        frame.add(mainPanel, BorderLayout.CENTER);
        // Show how many players are still in the game
        JLabel aliveCountLabel = new JLabel(
            game.getAlivePlayerCount() + " players remaining", 
            JLabel.CENTER
        );
        aliveCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton nextRoundButton = new JButton("Next Round");
        nextRoundButton.addActionListener(e -> {
            game.dealNewRound();
            gameLog += "\n=== NEW ROUND STARTED ===\n";
            gameLog += "It's a " + game.getSetCard().getName() + "'s table \n\n";
            showCardSelectionScene();
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(nextRoundButton);
        buttonPanel.add(exitButton);
        
        //frame.add(string);
        frame.add(aliveCountLabel, BorderLayout.NORTH);
        frame.add(playerInfoPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.revalidate();
        frame.repaint();
    }
    
    //Change Player Scene
    private void NextPlayerScene(){
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        
        JLabel currentPlayerLabel = new JLabel(currentPlayer.getName() + " Turn Has Ended", JLabel.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Show how many players are still alive
        int aliveCount = game.getAlivePlayerCount();
        JLabel alivePlayersLabel = new JLabel(aliveCount + " players remaining", JLabel.CENTER);
        alivePlayersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel nextPlayerLabel = new JLabel("Give The Next Player the Device", JLabel.CENTER);
        nextPlayerLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        
        JButton nextButton = new JButton("Next Player");
        
        nextButton.addActionListener(e -> {
            game.nextAlivePlayer(); // Skip to next alive player
            
            // Check if game is over after switching players
            if (game.isGameOver()) {
                Player winner = game.getWinningPlayer();
                JOptionPane.showMessageDialog(frame, 
                    winner.getName() + " wins the game!\nThey are the last player standing!");
                showGameOverScene();
            } else {
                showCardSelectionScene();
            }
        });
        
        // Create panels for better layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(currentPlayerLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(alivePlayersLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(nextButton);
        
        frame.add(topPanel, BorderLayout.NORTH);   
        frame.add(nextPlayerLabel, BorderLayout.CENTER);     
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.revalidate();
        frame.repaint();
    }
    
    private void showGameOverScene() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        
        Player winner = game.getWinningPlayer();
        
        JLabel gameOverLabel = new JLabel("GAME OVER!", JLabel.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 32));
        gameOverLabel.setForeground(Color.RED);
        
        JLabel winnerLabel = new JLabel(
            winner.getName() + " WINS!", 
            JLabel.CENTER
        );
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel survivorLabel = new JLabel(
            "Last player standing with " + winner.getLives() + " lives!", 
            JLabel.CENTER
        );
        survivorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        // Show final standings
        JTextArea finalStandings = new JTextArea(8, 40);
        finalStandings.setEditable(false);
        finalStandings.setText("Final Standings:\n\n");
        
        // Sort players by lives (winner first)
        ArrayList<Player> sortedPlayers = new ArrayList<>();
        for (int i = 0; i < game.getPlayerCount(); i++) {
            sortedPlayers.add(game.getPlayer(i));
        }
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getLives(), p1.getLives()));
        
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            String status = player.getLives() > 0 ? 
                player.getLives() + " lives" : "ELIMINATED";
            finalStandings.append((i + 1) + ". " + player.getName() + " - " + status + "\n");
        }
        
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener(e -> {
            gameLog = "";
            showPlayerSelectionScene();
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(gameOverLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        topPanel.add(winnerLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(survivorLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(finalStandings), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.revalidate();
        frame.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CombinedInterface());
    }
}
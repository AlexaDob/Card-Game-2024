import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// METHOD 1: Remove all components and add new ones (simplest for your case)
class Interface {
    private JFrame frame;
    private Game game;
    
    public Interface() {
        frame = new JFrame("Game of BS");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        showPlayerSelectionScene();
        frame.setVisible(true);
    }
    
    // Scene 1: Player selection
    private void showPlayerSelectionScene() {
        // Clear the frame
        frame.getContentPane().removeAll();
        frame.setLayout(new FlowLayout());
        
        // Create components for player selection
        JLabel nameLabel = new JLabel("Enter the Amount of Players: *Player count has to be 2-4 Players*", JLabel.LEFT);
        JTextField numofPlayers = new JTextField(15);
        JButton submitButton = new JButton("Enter");
        JLabel resultLabel = new JLabel("");
        
        // Button action
        submitButton.addActionListener(e -> {
            try {
                int number = Integer.parseInt(numofPlayers.getText());
                if (number >= 2 && number <= 4) {
                    game = new Game(number);
                    showGameScene(); // Switch to game scene
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
        
        // Refresh the display
        frame.revalidate();
        frame.repaint();
    }
    
    // Scene 2: Game interface
    private void showGameScene() {
        // Clear the frame
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        
        // Create game components
        JLabel titleLabel = new JLabel("Game of BS - In Progress...", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JTextArea gameInfo = new JTextArea(10, 40);
        gameInfo.setText(game.toString()); // Show game state
        gameInfo.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameInfo);
        
        JPanel buttonPanel = new JPanel();
        JButton callBSButton = new JButton("Call BS");
        JButton playCardsButton = new JButton("Play Cards");
        JButton newGameButton = new JButton("New Game");
        
        // Button actions
        callBSButton.addActionListener(e -> {
            // Handle BS call
            JOptionPane.showMessageDialog(frame, "BS Called!");
            updateGameDisplay(gameInfo);
        });
        
        playCardsButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "How many cards to play?");
            try {
                int cards = Integer.parseInt(input);
                JOptionPane.showMessageDialog(frame, "You played " + cards + " cards!");
                updateGameDisplay(gameInfo);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number!");
            }
        });
        
        newGameButton.addActionListener(e -> {
            showPlayerSelectionScene(); // Go back to player selection
        });
        
        buttonPanel.add(callBSButton);
        buttonPanel.add(playCardsButton);
        buttonPanel.add(newGameButton);
        
        // Add components
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        // Refresh the display
        frame.revalidate();
        frame.repaint();
    }
    
    private void updateGameDisplay(JTextArea gameInfo) {
        if (game != null) {
            gameInfo.setText(game.toString());
        }
    }
    
    public static void main(String[] args) {
        new Interface();
    }
}


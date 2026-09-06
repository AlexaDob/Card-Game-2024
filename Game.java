import java.util.ArrayList;
import javax.swing.*;

public class Game {
    private ArrayList<Player> players;
    private int amountofPlayers;
    private int currentPlayerIndex;
    private Cards setCard;
    private Player currentPlayer;
    private Player previousPlayer;
    private ArrayList<Cards> previousPlayedCards;
    
    public Game(int n){
        players = new ArrayList<Player>();
        for(int i = 1; i <= n; i++){
            players.add(new Player(i));
        }
        amountofPlayers = n;
        currentPlayerIndex = 0; // Initialize current player index
        newSetCard();
    }
    
    // Basic player access methods
    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        }
        return null;
    }
    
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % amountofPlayers;
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    // Previous play tracking methods
    public Player getPreviousPlayer() {
        return previousPlayer;
    }
    
    // ADDED: Method to get previous played cards
    public ArrayList<Cards> getPreviousPlayedCards() {
        return previousPlayedCards;
    }
    
    // Tracks play history
    public void recordPlay(Player player, ArrayList<Cards> playedCards) {
        this.previousPlayer = player;
        this.previousPlayedCards = new ArrayList<Cards>(playedCards); // Make a copy
        
        // Debug output
        System.out.println("=== RECORDING PLAY ===");
        System.out.println("Player: " + player.getName());
        System.out.println("Cards played: " + playedCards.size());
        for (Cards card : playedCards) {
            System.out.println("  - " + card.getName());
        }
        System.out.println("Set card: " + setCard.getName());
    }
    
    // Set card management
    public void setSetCard(Cards card) {
        this.setCard = card;
    }
    
    public void newSetCard(){
        int n = (int)(Math.random()*3+1);
        Cards set = new Cards(n);
        setSetCard(set);
        System.out.println("New set card: " + set.getName());
    }
    
    public Cards getSetCard(){
        return setCard;
    }
    
    // FIXED: BS checking logic with better debugging
    public boolean checkBS(){
        System.out.println("=== CHECKING BS ===");
        
        if(previousPlayedCards == null || previousPlayedCards.isEmpty()){
            System.out.println("ERROR: No previous played cards");
            return false;
        }
        
        System.out.println("Checking " + previousPlayedCards.size() + " cards against set card: " + setCard.getName());
        
        // Check each card individually for better debugging
        for (int i = 0; i < previousPlayedCards.size(); i++) {
            Cards card = previousPlayedCards.get(i);
            boolean matches = card.matchesSetCardOrJoker(setCard);
            System.out.println("Card " + (i+1) + ": " + card.getName() + " matches? " + matches);
            
            if (!matches) {
                System.out.println("RESULT: BS detected - not all cards match");
                return false; // Found a card that doesn't match - BS detected
            }
        }
        
        System.out.println("RESULT: No BS - all cards are valid");
        return true; // All cards match - no BS
    }
    
    // SIMPLIFIED: Direct BS call method (optional - can be used instead of interface logic)
    public void callBS(Player caller){
        System.out.println(caller.getName() + " calls BS on " + previousPlayer.getName());
        
        if(this.checkBS() == true){
            // Previous player was honest - BS caller loses life
            System.out.println("BS call was wrong - " + caller.getName() + " loses life");
            caller.rollLife();
        } else {
            // Previous player was lying - they lose life
            System.out.println("BS call was correct - " + previousPlayer.getName() + " loses life");
            this.previousPlayer.rollLife();
        }    
    }
    
    // Life/game management methods
    public int getAlivePlayerCount() {
        int aliveCount = 0;
        for (Player player : players) {
            if (player.getLives() > 0) {
                aliveCount++;
            }
        }
        return aliveCount;
    }
    
    // Get the winning player (only one with lives > 0)
    public Player getWinningPlayer() {
        for (Player player : players) {
            if (player.getLives() > 0) {
                return player;
            }
        }
        return null; // This shouldn't happen if game logic is correct
    }
    
    // Check if the game should end (only one player alive)
    public boolean isGameOver() {
        return getAlivePlayerCount() <= 1;
    }
    
    // Get all alive players
    public ArrayList<Player> getAlivePlayers() {
        ArrayList<Player> alivePlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getLives() > 0) {
                alivePlayers.add(player);
            }
        }
        return alivePlayers;
    }
    
    // Skip to next alive player
    public void nextAlivePlayer() {
        int attempts = 0;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % amountofPlayers;
            attempts++;
            
            // Safety check to avoid infinite loop
            if (attempts > amountofPlayers) {
                System.out.println("WARNING: Could not find next alive player");
                break;
            }
        } while (getCurrentPlayer().getLives() <= 0 && getAlivePlayerCount() > 1);
        
        System.out.println("Next player: " + getCurrentPlayer().getName());
    }
    
    // ADDED: Reset game for new round
    public void dealNewRound() {
        // Clear previous play history
        previousPlayer = null;
        previousPlayedCards = null;
        
        // Reset current player to first alive player
        currentPlayerIndex = 0;
        while (getCurrentPlayer().getLives() <= 0 && getAlivePlayerCount() > 1) {
            nextPlayer();
        }
        
        // Generate new set card
        newSetCard();
        
        // Re-deal cards to all alive players
        for (Player player : players) {
            if (player.getLives() > 0) {
                // Clear current hand
                player.clearHand();
                
                // Deal new cards (you'll need to implement card dealing logic)
                // For now, just create new cards
                for (int i = 0; i < 6; i++) {
                    // This is simplified - you'd want to deal from a proper deck
                    int cardType = (int)(Math.random() * 4) + 1;
                    player.addCard(new Cards(cardType));
                }
            }
        }
        
        System.out.println("New round started - " + getAlivePlayerCount() + " players remaining");
    }
    
    // ADDED: Clear play history (useful for debugging)
    public void clearPlayHistory() {
        previousPlayer = null;
        previousPlayedCards = null;
        System.out.println("Play history cleared");
    }
    
    // ADDED: Get detailed game state for debugging
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        state.append("=== GAME STATE ===\n");
        state.append("Current Player: ").append(getCurrentPlayer().getName()).append("\n");
        state.append("Set Card: ").append(setCard.getName()).append("\n");
        state.append("Previous Player: ").append(previousPlayer != null ? previousPlayer.getName() : "None").append("\n");
        state.append("Previous Cards: ").append(previousPlayedCards != null ? previousPlayedCards.size() : "None").append("\n");
        state.append("Alive Players: ").append(getAlivePlayerCount()).append("/").append(amountofPlayers).append("\n");
        
        for (Player player : players) {
            state.append("  ").append(player.getName()).append(": ")
                 .append(player.getLives()).append(" lives, ")
                 .append(player.getHandSize()).append(" cards\n");
        }
        
        return state.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game Status:\n");
        sb.append("Set Card: ").append(setCard.getName()).append("\n");
        sb.append("Current Player: ").append(getCurrentPlayer().getName()).append("\n");
        sb.append("Players Alive: ").append(getAlivePlayerCount()).append("/").append(amountofPlayers).append("\n");
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            sb.append(player.toString());
            if (player.getLives() <= 0) {
                sb.append(" [ELIMINATED]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
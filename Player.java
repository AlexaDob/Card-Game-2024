import java.util.ArrayList;

public class Player {
    private ArrayList<Cards> hand;
    private int lives;
    private String name;
    private CardHand handTemp; // For dealing initial cards
    
    public Player(int playerNumber) {
        this.name = "Player " + playerNumber;
        this.lives = 6; // Start with 6 lives
        this.hand = new ArrayList<Cards>();
        
        // Deal initial cards
        dealInitialCards();
        
        System.out.println("Created " + name + " with " + hand.size() + " cards and " + lives + " lives");
    }
    
    // Deal initial 6 cards to player
    private void dealInitialCards() {
        handTemp = new CardHand();
        
        // CardHand should have a givePlayerHand() method that returns 6 cards
        // Use that method if it exists
        try {
            ArrayList<Cards> dealtCards = handTemp.givePlayerHand();
            if (dealtCards != null && !dealtCards.isEmpty()) {
                hand.addAll(dealtCards);
            } else {
                // Fallback: create random cards
                createRandomHand();
            }
        } catch (Exception e) {
            // If givePlayerHand() doesn't exist, create random cards
            createRandomHand();
        }
    }
    
    // Fallback method to create a random hand
    private void createRandomHand() {
        for (int i = 0; i < 6; i++) {
            int cardType = (int)(Math.random() * 4) + 1;
            hand.add(new Cards(cardType));
        }
    }
    
    // Basic player information methods
    public String getName() {
        return name;
    }
    
    public int getLives() {
        return lives;
    }
    
    public int getHandSize() {
        return hand.size();
    }
    
    public ArrayList<Cards> getHand() {
        return new ArrayList<Cards>(hand); // Return copy to prevent external modification
    }
    
    // Card management methods
    public Cards getCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.get(index);
        }
        return null;
    }
    
    public void addCard(Cards card) {
        if (card != null) {
            hand.add(card);
            System.out.println(name + " received card: " + card.getName());
        }
    }
    
    public void removeCard(int index) {
        if (index >= 0 && index < hand.size()) {
            Cards removed = hand.remove(index);
            System.out.println(name + " played card: " + removed.getName());
        }
    }
    
    public void clearHand() {
        hand.clear();
        System.out.println(name + "'s hand cleared");
    }
    
    // Remove multiple cards at once
    public void removeCards(ArrayList<Cards> cardsToRemove) {
        for (Cards card : cardsToRemove) {
            hand.remove(card);
        }
        System.out.println(name + " removed " + cardsToRemove.size() + " cards");
    }
    
    // Life management methods
    public void loseLife() {
        if (lives > 0) {
            lives--;
            System.out.println(name + " loses a life! Lives remaining: " + lives);
            
            if (lives <= 0) {
                System.out.println(name + " has been eliminated!");
            }
        }
    }
    
    public void addLife() {
        lives++;
        System.out.println(name + " gains a life! Lives: " + lives);
    }
    
    // Death roll method - rolls 1-6, dies on 1 (with reducing chance)
    public boolean rollLife() {
        int roll = (int)(Math.random() * 6) + 1;
        System.out.println(name + " rolls a " + roll + " for life!");
        
        if (roll == 1) {
            // Death roll!
            lives = 0;
            System.out.println(name + " rolled a 1 and dies!");
            return true; // Player died
        } else {
            // Just lose a regular life
            loseLife();
            return lives <= 0; // Return true if player died from life loss
        }
    }
    
    // Alternative life roll that just removes one life
    public void rollLifeSimple() {
        loseLife();
    }
    
    // Check if player is alive
    public boolean isAlive() {
        return lives > 0;
    }
    
    // Check if player is eliminated
    public boolean isEliminated() {
        return lives <= 0;
    }
    
    // Game action methods
    public ArrayList<Cards> selectCards(int[] indices) {
        ArrayList<Cards> selectedCards = new ArrayList<>();
        
        // Sort indices in descending order to avoid index shifting when removing
        java.util.Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            int index = indices[i];
            if (index >= 0 && index < hand.size()) {
                selectedCards.add(0, hand.get(index)); // Add to front to maintain order
            }
        }
        
        return selectedCards;
    }
    
    // Play specific cards and remove them from hand
    public ArrayList<Cards> playCards(int[] indices) {
        ArrayList<Cards> playedCards = new ArrayList<>();
        
        // Sort indices in descending order to avoid index shifting
        java.util.Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            int index = indices[i];
            if (index >= 0 && index < hand.size()) {
                Cards card = hand.remove(index);
                playedCards.add(0, card); // Add to front to maintain order
            }
        }
        
        System.out.println(name + " played " + playedCards.size() + " cards");
        return playedCards;
    }
    
    // Display hand (for debugging)
    public void showHand() {
        System.out.println(name + "'s hand (" + hand.size() + " cards):");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println("  " + i + ": " + hand.get(i).getName());
        }
    }
    
    // Get hand as string for display
    public String getHandString() {
        StringBuilder handStr = new StringBuilder();
        handStr.append(name).append("'s cards: ");
        
        if (hand.isEmpty()) {
            handStr.append("No cards");
        } else {
            for (int i = 0; i < hand.size(); i++) {
                if (i > 0) handStr.append(", ");
                handStr.append(hand.get(i).getName());
            }
        }
        
        return handStr.toString();
    }
    
    // Count specific card types in hand
    public int countCardType(String cardName) {
        int count = 0;
        for (Cards card : hand) {
            if (card.getName().equals(cardName)) {
                count++;
            }
        }
        return count;
    }
    
    public int countJokers() {
        return countCardType("Joker");
    }
    
    public int countAces() {
        return countCardType("Ace");
    }
    
    public int countKings() {
        return countCardType("King");
    }
    
    public int countQueens() {
        return countCardType("Queen");
    }
    
    // Check if player has any cards that match the set card or are jokers
    public boolean hasValidCards(Cards setCard) {
        for (Cards card : hand) {
            if (card.matchesSetCardOrJoker(setCard)) {
                return true;
            }
        }
        return false;
    }
    
    // Get all valid cards for the current set card
    public ArrayList<Cards> getValidCards(Cards setCard) {
        ArrayList<Cards> validCards = new ArrayList<>();
        for (Cards card : hand) {
            if (card.matchesSetCardOrJoker(setCard)) {
                validCards.add(card);
            }
        }
        return validCards;
    }
    
    // Reset player for new game
    public void resetForNewGame() {
        lives = 6;
        clearHand();
        dealInitialCards();
        System.out.println(name + " reset for new game");
    }
    
    // Deal new cards for new round (keeping same lives)
    public void dealNewRound() {
        clearHand();
        dealInitialCards();
        System.out.println(name + " dealt new cards for new round");
    }
    
    @Override
    public String toString() {
        String status = "";
        if (lives <= 0) {
            status = " [ELIMINATED]";
        } else if (hand.isEmpty()) {
            status = " [NO CARDS]";
        }
        
        return name + " - Lives: " + lives + " - Cards: " + hand.size() + status;
    }
    
    // Detailed toString for debugging
    public String toDetailedString() {
        StringBuilder detail = new StringBuilder();
        detail.append(toString()).append("\n");
        detail.append("  Hand: ");
        
        if (hand.isEmpty()) {
            detail.append("Empty");
        } else {
            for (int i = 0; i < hand.size(); i++) {
                if (i > 0) detail.append(", ");
                detail.append(hand.get(i).getName());
            }
        }
        
        return detail.toString();
    }
}
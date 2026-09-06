import java.util.ArrayList;

public class Round{
    private Game game;
    private Cards setCard;
    private Player currentPlayer;
    private Player previousPlayer;
    private ArrayList<Cards> previousPlayedCards;
    
    public Round(Game game){
      this.game = game;
      int ran = (int)(Math.random()*3);
      setCard = new Cards(ran);
        
    }
    
    public Player getPreviousPlayer() {
        return previousPlayer;
    }
    
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    
    //Tracks play hisotry
    public void recordPlay(Player player, ArrayList<Cards> playedCards) {
        this.previousPlayer = player;
        this.previousPlayedCards = new ArrayList<Cards>(playedCards); // Make a copy
    }
    
    
    public void setSetCard(Cards card) {
        this.setCard = card;
    }
    
    //Calls BS on the current player and the previous player
    public void callBS(){
        if(this.checkBS() == true){
            this.previousPlayer.rollLife();
        }else{
            this.currentPlayer.rollLife();
        }    
    }
    
    //Checks for BS
    public boolean checkBS(){
        if(previousPlayedCards.size() == 1){
            return previousPlayedCards.get(0).matchesSetCardOrJoker(setCard);
        }else if(previousPlayedCards.size() == 2){
            return previousPlayedCards.get(0).matchesSetCardOrJoker(setCard) && previousPlayedCards.get(1).matchesSetCardOrJoker(setCard);     
        }else if(previousPlayedCards.size() == 3){
            return previousPlayedCards.get(0).matchesSetCardOrJoker(setCard) && previousPlayedCards.get(1).matchesSetCardOrJoker(setCard) && previousPlayedCards.get(2).matchesSetCardOrJoker(setCard);
        }
        if(previousPlayedCards == null || previousPlayedCards.isEmpty()){
        System.out.println("ERROR: No previous Played Cards");
        return false;
        }
        return false;
    }
}


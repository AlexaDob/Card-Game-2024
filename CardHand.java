import java.util.ArrayList;
import java.util.Collections;

public class CardHand {

    private ArrayList<Cards> hand;
    
    public CardHand(){
        hand = new ArrayList<Cards>();
        for (int i = 1; i < 4; i++) {
            for(int j = 0; j < 6; j++){
                hand.add(new Cards(i));
            }
        }
        for (int i = 0; i < 2; i++) {
            hand.add(new Cards(4));
        }
        
        Collections.shuffle(hand);
        
    }
    
    
    public Cards getCard(int n){
        return hand.get(n);
    }
    
    public void removeCard(int n){
        hand.remove(n);
    }
    
    public ArrayList<Cards> givePlayerHand(){
        ArrayList<Cards> playerHand = new ArrayList<Cards>();
        for (int i = 0; i < 6 && i < hand.size(); i++) {
            playerHand.add(hand.remove(0));
        }
        return playerHand;
    }
    
    public String toString(){
        String result = "";
        for (int i = 0; i < hand.size(); i++) {
            result += getCard(i) + "\n";
        }
        return result;
    }
}

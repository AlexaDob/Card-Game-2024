public class Cards implements Comparable{
    
    private int face;
    private String name;

    
    public Cards(int f){
        face = f;
    }
    
    public int getFace(){
        return face;
    }
    
    
    public String toString(){
        String result = "";
        if(face == 1){
            result += "Ace";
            name = "Ace";
        }
        else if(face == 2){
            result += "King";
            name = "King";
        }
        else if(face == 3){
            result += "Queen";
            name = "Queen";
        }
        else if(face == 4){
            result += "Joker";
            name = "Joker";
        }
        return result;
    }
    
    public boolean isJoker() {
        return face == 4;
    }
    
    public String getName(){
    if(face == 1) return "Ace";
    else if(face == 2) return "King";
    else if(face == 3) return "Queen";
    else if(face == 4) return "Joker";
    return "Unknown";
    }
    
    public boolean matchesSetCardOrJoker(Cards setCard) {
        return this.isJoker() || this.name.equals(setCard.getName());
    }
    
    
    public int compareTo(Object obj){
        Cards other = (Cards)obj;
        return this.getFace() - other.getFace();
    }
}


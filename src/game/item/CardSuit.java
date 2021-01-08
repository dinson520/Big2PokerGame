package game.item;

public enum CardSuit {

    CLUB('\u2663'), DIAMOND('\u2666'),HEART('\u2665'),SPADE('\u2660');

    private char image;
    private CardSuit(char image){
        this.image = image;
    }
    @Override
    public String toString() {

        return String.valueOf(image) ;
    }
    public String toSingleCharacter() {

        String result = null;

        switch (this){
            case CLUB:
                result = "C";
                break;
            case DIAMOND:
                result = "D";
                break;
            case HEART:
                result = "H";
                break;
            case SPADE:
                result = "S";
                break;
        }

        return result ;
    }
    public static CardSuit getCardSuitFromString(String t){

        if(t.toUpperCase().contains("C")) return CLUB;
        if(t.toUpperCase().contains("D")) return DIAMOND;
        if(t.toUpperCase().contains("H")) return HEART;
        if(t.toUpperCase().contains("S")) return SPADE;

        return null;
    }
}

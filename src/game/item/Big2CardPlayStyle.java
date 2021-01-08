package game.item;

public enum Big2CardPlayStyle {

    FLUSH_STRAIGHT(2), KIND_OF_A_FOUR(1), FULL_HOUSE(-1), STRAIGHT(-1), PAIR(-1), SINGLE(-1), PASS(-2);

    private int rank;

    Big2CardPlayStyle(int rank){
        this.rank = rank;
    }

    public int getRank(){return this.rank;}
}

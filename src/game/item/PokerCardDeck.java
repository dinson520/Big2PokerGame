package game.item;

import game.comparator.Big2Comparator;

import java.util.*;

public class PokerCardDeck {

    private Collection<PokerCard> theDeck = new ArrayList<>();

    public PokerCardDeck(){}
    public PokerCardDeck(List<PokerCard> cardDeck){this.theDeck = cardDeck;}

    public void addOneCard(PokerCard theCard) {
        for(PokerCard handCard: theDeck)
            if(handCard.isSame(theCard)) throw new IllegalStateException();
        theDeck.add(theCard);
    }
    public void addCards(PokerCardDeck theDeck){
        for(PokerCard curCard: theDeck.getCollection())
            addOneCard(curCard);

    }

    public void removeCards(PokerCardDeck removeDeck){
        Collection<PokerCard> removedTarget = new ArrayList<>();
        for (PokerCard myCard: theDeck)
            for(PokerCard theRemove: removeDeck.getCollection())
                if(myCard.isSame(theRemove)) removedTarget.add(myCard);
        this.theDeck.removeAll(removedTarget);
    }

    public Collection<PokerCard> getCollection() {
        return theDeck;
    }

    public void setTheDeck(List<PokerCard> theDeck) {
        this.theDeck = theDeck;
    }

    public int size() {
        return theDeck.size();
    }

    public PokerCard[] getSortedDeckArray() {

        PokerCard[] result = new PokerCard[this.size()];
        this.theDeck.toArray(result);

        for(int i =0;i<result.length;i++)
            for(int j = 1; j<(result.length-i);j++){
                int compareResult = Big2Comparator.compareBig2SingleCard(result[j-1],result[j]);
                if(compareResult>0){
                    PokerCard temp = result[j-1];
                    result[j-1] = result[j];
                    result[j] = temp;
                }
            }

        return result;
    }
    public String show(){
        String result = "[ ";
        for(PokerCard theCard:theDeck)
            result += theCard.toString()+" ";
        return result+="]";
    }

    public PokerCardDeck getSelectedCards(){
        PokerCardDeck resultDeck = new PokerCardDeck();
        for(PokerCard theCard: theDeck){
            if(theCard.isSelected()){
//                System.out.println("Card("+theCard.toString()+") is selected.");
                resultDeck.addOneCard(theCard);
            }
        }
//        System.out.println("--------------------------------");
//        System.out.println(show());


        return resultDeck;
    }

    public boolean contains(PokerCard theCard) {
        for(PokerCard curCard: theDeck)
            if(curCard.isSame(theCard)) return true;
        return false;
    }
}

package game.validator;

import game.item.Big2CardPlayStyle;
import game.item.CardSuit;
import game.item.PokerCard;
import game.item.PokerCardDeck;

import java.util.HashMap;


/*
* To check cards combination is valid.
 */

public class Big2CardSetValidator {

    public static int[][] straightCombination = {{2, 3, 4, 5, 6}, {1, 10, 11, 12, 13}, {9, 10, 11, 12, 13},
            {8, 9, 10, 11, 12}, {7, 8, 9, 10, 11}, {6, 7, 8, 9, 10},
            {5, 6, 7, 8, 9}, {4, 5, 6, 7, 8}, {3, 4, 5, 6, 7}, {1, 2, 3, 4, 5}};

    private PokerCard[] cardsArray;
    private HashMap<Integer, Integer> nonDuplicatedNumberCount = new HashMap<>();


    public static PokerCard[] bubbleSorted(PokerCardDeck cards){

        if(cards==null) return new PokerCardDeck().getSortedDeckArray();
        PokerCard[] cardsArray = cards.getSortedDeckArray();

        for(int i =0;i<cardsArray.length;i++)
            for(int j = 1; j<(cardsArray.length-i);j++){

                if(cardsArray[j].cardPoint <cardsArray[j-1].cardPoint){
                    PokerCard temp = cardsArray[j-1];
                    cardsArray[j-1] = cardsArray[j];
                    cardsArray[j] = temp;
                }
            }

//        System.out.print("Sorted result:");
//        for(PokerCard curCard:cardsArray)
//            System.out.print(curCard);
//        System.out.println();

        return cardsArray;
    }

    public HashMap<Integer,Integer> countsPointsNumber(PokerCard[] theDeck){

        HashMap<Integer,Integer> result = new HashMap();

        for(PokerCard curCard:theDeck) {
            if(!result.containsKey(curCard.cardPoint))
                result.put(curCard.cardPoint,1);
            else{
                Integer counts = result.get(curCard.cardPoint);
                result.put(curCard.cardPoint,++counts);
            }
        }
        return result;
    }

    private boolean isSameSuit(){

        CardSuit lastSuit = cardsArray[0].cardSuit;
        for (int i = 1;i<cardsArray.length;i++)
            if(lastSuit!=cardsArray[i].cardSuit) return false;

        return true;
    }

    private boolean isTriple(PokerCardDeck cards){
        if(cards == null) return false;
        this.cardsArray = bubbleSorted(cards);
        this.nonDuplicatedNumberCount = countsPointsNumber(cardsArray);

        return nonDuplicatedNumberCount.size()==1 && cardsArray.length==3;
    }

    public boolean isPair(PokerCardDeck cards) {

        if(cards == null) return false;
        this.cardsArray = bubbleSorted(cards);
        this.nonDuplicatedNumberCount = countsPointsNumber(cardsArray);

        return nonDuplicatedNumberCount.size()==1 && cardsArray.length==2;
    }

    public boolean isStraight(PokerCardDeck cards) {

        this.cardsArray = bubbleSorted(cards);

        boolean result = false;
        if (cards.size() != 5)
            result = false;
        else{
            for(int[] curSet : straightCombination){

                if((curSet[0]==cardsArray[0].cardPoint) && (curSet[1]==cardsArray[1].cardPoint) && (curSet[2]==cardsArray[2].cardPoint)
                        && (curSet[3]==cardsArray[3].cardPoint) && (curSet[4]==cardsArray[4].cardPoint)){
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public boolean isFullhouse(PokerCardDeck cards) {

        if(cards == null) return false;
        this.cardsArray = bubbleSorted(cards);
        this.nonDuplicatedNumberCount = countsPointsNumber(cardsArray);

        return (cards.size() == 5) && (nonDuplicatedNumberCount.size() == 2) && nonDuplicatedNumberCount.containsValue(3) ;
    }

    public boolean isFourOfaKind(PokerCardDeck cards) {

        if(cards == null) return false;
        this.cardsArray = bubbleSorted(cards);
        this.nonDuplicatedNumberCount = countsPointsNumber(cardsArray);

        return (cards.size() == 5) && (nonDuplicatedNumberCount.size() == 2) && nonDuplicatedNumberCount.containsValue(4) ;

    }

    public boolean isStraightFlush(PokerCardDeck cards) {

        return isStraight(cards) && isSameSuit();

    }

    public boolean isSingle(PokerCardDeck cards) {

        return cards.size() == 1;

    }

    public Big2CardPlayStyle getHighestPlayStyle(PokerCardDeck cards){
        if(cards.size()==0) return Big2CardPlayStyle.PASS;
        if(cards.size()==1) return Big2CardPlayStyle.SINGLE;
        if(cards.size()==2 && isPair(cards)) return Big2CardPlayStyle.PAIR;
        if(cards.size()==5) {
            if (isStraightFlush(cards)) return Big2CardPlayStyle.FLUSH_STRAIGHT;
            if (isFourOfaKind(cards)) return Big2CardPlayStyle.KIND_OF_A_FOUR;
            if (isFullhouse(cards)) return Big2CardPlayStyle.FULL_HOUSE;
            if (isStraight(cards)) return Big2CardPlayStyle.STRAIGHT;
        }
        System.err.println("Play style not valid, should not reached here! ... ");
        return null;
    }


}

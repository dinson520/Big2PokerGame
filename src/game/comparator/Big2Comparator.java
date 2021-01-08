package game.comparator;

import game.item.Big2CardPlayStyle;
import game.item.CardSuit;
import game.item.PokerCard;
import game.item.PokerCardDeck;
import game.validator.Big2CardSetValidator;

import java.util.*;
public class Big2Comparator implements Comparator<PokerCard>{

    private PokerCardDeck newPlay;
    private PokerCardDeck onBoard;
    private PokerCard[] newPlaySortedArray;
    private PokerCard[] onBoardSortedArray;

    private Big2CardSetValidator validator = new Big2CardSetValidator();

    private static int[] singleRank = {3,4,5,6,7,8,9,10,11,12,13,1,2};

    public static int[][] straightRank = {{1, 2, 3, 4, 5},{3, 4, 5, 6, 7}, {4, 5, 6, 7, 8},
            {5, 6, 7, 8, 9}, {6, 7, 8, 9, 10} , {7, 8, 9, 10, 11}, {8, 9, 10, 11, 12},
            {9, 10, 11, 12, 13}, {1, 10, 11, 12, 13} ,{2, 3, 4, 5, 6}};
    public Big2Comparator(){}
    public Big2Comparator(PokerCardDeck newPlay, PokerCardDeck onBoard) {
        this.newPlay = newPlay;
        this.onBoard = onBoard;
        newPlaySortedArray = Big2CardSetValidator.bubbleSorted(newPlay);
        onBoardSortedArray = Big2CardSetValidator.bubbleSorted(onBoard);
    }

    public static int compareBig2SingleCard(PokerCard o1, PokerCard o2) {
        if (o1.cardPoint != o2.cardPoint)
            return getPointRankIndex(o1.cardPoint) - getPointRankIndex(o2.cardPoint);
        else
            return getCardStandardSuitRank(o1.cardSuit) - getCardStandardSuitRank(o2.cardSuit);
    }

    public static int getCardStandardSuitRank(CardSuit suit) {
        switch (suit) {
            case CLUB: {
                return 0;
            }
            case DIAMOND: {
                return 1;
            }
            case HEART: {
                return 2;
            }
            case SPADE: {
                return 3;
            }
            default: {
                throw new Error("Should not reached!");
            }
        }
    }

    private boolean isFirstRoundPlay() {

        /* with club3 is acceptable. */
        PokerCard club3 = new PokerCard(CardSuit.CLUB,3);
        if (containsTheCard(newPlay,club3) && onBoard.size()==0) {

            if (validator.isSingle(newPlay)) return true;
            if (validator.isPair(newPlay)) return true;
            if (validator.isStraight(newPlay)) return true;
            if (validator.isFullhouse(newPlay)) return true;
            if (validator.isFourOfaKind(newPlay)) return true;

            return false;

        }
        else
            return false;
    }

    private int compareSingleToSingle() {

        if (newPlay.size() != 1 || onBoard.size() != 1)
            throw new RuntimeException("Should not reached!!!");

        PokerCard newPlayMax = getMaxCardFromDeck(newPlay);
        PokerCard originalMax = getMaxCardFromDeck(onBoard);

        return compareBig2SingleCard(newPlayMax, originalMax);
    }

    private int comparePairToPair() {

        if (newPlay.size() != 2 || onBoard.size() != 2)
            throw new RuntimeException("Should not reached!!!");

        if(validator.isPair(newPlay) && validator.isPair(onBoard)){

            PokerCard newPlayMaxCard = getMaxCardFromDeck(newPlay);
            PokerCard originalMaxCard = getMaxCardFromDeck(onBoard);

            return compareBig2SingleCard(newPlayMaxCard, originalMaxCard);

        }

        return 0;
    }

    private int compareStraightToStraight() {

        if (newPlay.size() != 5 || onBoard.size() != 5)
            throw new RuntimeException("Should not reached!!!");

        if(validator.isStraight(newPlay) && validator.isStraight(onBoard)){

            boolean newPlayIsStraightFlush = validator.isStraightFlush(newPlay);
            boolean originalIsStraightFlush = validator.isStraightFlush(onBoard);

            int newPlayStraightRank = findStraightCombinationRank(newPlaySortedArray);
            int originalStraightRank = findStraightCombinationRank(onBoardSortedArray);

            if(newPlayStraightRank==originalStraightRank && !newPlayIsStraightFlush && !originalIsStraightFlush){

                PokerCard newPlayMaxCard = getMaxCardFromDeck(newPlay);
                PokerCard originalMaxCard = getMaxCardFromDeck(onBoard);

                return compareBig2SingleCard(newPlayMaxCard, originalMaxCard);
            }

            else
                return newPlayStraightRank-originalStraightRank;

        }
        return 0;
    }

    private int compareFullHouseToFullHouse() {

        if (newPlay.size() != 5 || onBoard.size() != 5) throw new RuntimeException("Should not reached!!!");

        if(validator.isFullhouse(newPlay) && validator.isFullhouse(onBoard)){
            int newPlayPoint = getPointOfMaxCount(newPlay);
            int originalPoint = getPointOfMaxCount(onBoard);

            return getPointRankIndex(newPlayPoint) - getPointRankIndex(originalPoint);
        }
        return 0;
    }

    private int compareFourOfAKindToFourOfAKind() {

        if (newPlay.size() != 5 || onBoard.size() != 5) throw new RuntimeException("Should not reached!!!");

        if(validator.isFourOfaKind(newPlay) && validator.isFourOfaKind(onBoard)){
            int newPlayPoint = getPointOfMaxCount(newPlay);
            int originalPoint = getPointOfMaxCount(onBoard);

            return getPointRankIndex(newPlayPoint) - getPointRankIndex(originalPoint);
        }
        return 0;
    }

    private int compareFlushStraightToFlushStraight() {

        if (newPlay.size() != 5 || onBoard.size() != 5)
            throw new RuntimeException("Should not reached!!!");

        if(validator.isStraightFlush(newPlay) && validator.isStraightFlush(onBoard)){

            int newPlayStraightRank = findStraightCombinationRank(newPlaySortedArray);
            int originalStraightRank = findStraightCombinationRank(onBoardSortedArray);

            if(newPlayStraightRank==originalStraightRank){

                PokerCard newPlayMaxCard = getMaxCardFromDeck(newPlay);
                PokerCard originalMaxCard = getMaxCardFromDeck(onBoard);

                return compareBig2SingleCard(newPlayMaxCard, originalMaxCard);
            }

            else
                return newPlayStraightRank-originalStraightRank;

        }
        return 0;
    }

//    public boolean isNewPlayAccept(PokerCardDeck newPlay, PokerCardDeck onBoard){
    public boolean isNewPlayAccept(){

        if(isFirstRoundPlay()) return true;

        Big2CardPlayStyle newPlayStyle = validator.getHighestPlayStyle(newPlay);
        if(newPlayStyle==null) return false;

        Big2CardPlayStyle onBoardStyle = validator.getHighestPlayStyle(onBoard);

        int newPlayStyleRank = newPlayStyle.getRank();
        int originalStyleRank = onBoardStyle.getRank();

        if(newPlayStyle != onBoardStyle){
            if(onBoardStyle==Big2CardPlayStyle.PASS)
                return true;
            else
                return newPlayStyleRank > originalStyleRank && newPlayStyleRank > 0;
        }
        else{
            switch (newPlayStyle){
                case FLUSH_STRAIGHT:
                    return compareFlushStraightToFlushStraight()>0;
                case KIND_OF_A_FOUR:
                    return compareFourOfAKindToFourOfAKind()>0;
                case FULL_HOUSE:
                    return compareFullHouseToFullHouse()>0;
                case STRAIGHT:
                    return compareStraightToStraight()>0;
                case PAIR:
                    return comparePairToPair()>0;
                case SINGLE:
                    return compareSingleToSingle()>0;
                default:
                    throw new IllegalStateException("Unexpected value: " + newPlayStyle);
            }
        }
    }

    private int findStraightCombinationRank(PokerCard[] theDeck){
        int theRankIdx = 0 ;
        for(int[] curSet : straightRank){
            if(     (curSet[0]==theDeck[0].cardPoint) &&
                    (curSet[1]==theDeck[1].cardPoint) &&
                    (curSet[2]==theDeck[2].cardPoint) &&
                    (curSet[3]==theDeck[3].cardPoint) &&
                    (curSet[4]==theDeck[4].cardPoint)){
                break;
            }
            theRankIdx++;
        }
        return theRankIdx;
    }

    private boolean containsTheCard(PokerCardDeck newPlay, PokerCard pokerCard) {

        PokerCard[] newPlaysArray = Big2CardSetValidator.bubbleSorted(newPlay);

        for (PokerCard curCard : newPlaysArray)
            if (curCard.isSame(pokerCard)) return true;

        return false;
    }

    private PokerCard getMaxCardFromDeck(PokerCardDeck theDeck){

        PokerCard maxCard = null;

        for(PokerCard theCard:theDeck.getCollection()) {
            if (maxCard == null)
                maxCard = theCard;
            else if(getPointRankIndex(theCard.cardPoint)>getPointRankIndex(maxCard.cardPoint))
                    maxCard = theCard;

        }
        return maxCard;
    }

    private static int  getPointRankIndex(int thePoint){
        for(int i = 0;i<singleRank.length;i++ )
            if(singleRank[i]==thePoint) return i;
        throw new RuntimeException("Should not reached!!");
    }

    private int getPointOfMaxCount(PokerCardDeck theDeck){

        if(!(validator.isFourOfaKind(theDeck) || validator.isFullhouse(theDeck))) throw new IllegalStateException("Not a full house, should not reached here.");

        PokerCard[] theDeckArray = Big2CardSetValidator.bubbleSorted(theDeck);

        HashMap<Integer,Integer> pointCounter = validator.countsPointsNumber(theDeckArray);

        for (Map.Entry<Integer, Integer> entry : pointCounter.entrySet())
            if(entry.getValue()>=3) return entry.getKey();

        return 0;
    }

    private <T> T[] getArray(Collection<T> theDeck){


        T[] result = (T[]) new Object[theDeck.size()];

        theDeck.toArray(result);

        return result;

    }

    @Override
    public int compare(PokerCard o1, PokerCard o2) {
        return compareBig2SingleCard(o1, o2);
    }
}




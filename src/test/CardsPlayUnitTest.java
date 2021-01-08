package test;

import game.item.CardSuit;
import game.item.PokerCard;
import game.item.PokerCardDeck;
import game.validator.Big2CardSetValidator;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class CardsPlayUnitTest {

    CardsPlayUnitTest cases;
    Big2CardSetValidator validator;

    PokerCardDeck caseSingle = new PokerCardDeck();

    PokerCardDeck casePair = new PokerCardDeck();

    PokerCardDeck  caseStraight = new PokerCardDeck();

    PokerCardDeck  caseFullhouse = new PokerCardDeck();

    PokerCardDeck caseFourOfaKind = new PokerCardDeck();

    PokerCardDeck  caseStraightFlush = new PokerCardDeck();

    /* c = club, d = diamond, h = heart, s = spade */
    PokerCard[] club = new PokerCard[14];
    PokerCard[] diamond = new PokerCard[14];
    PokerCard[] heart = new PokerCard[14];
    PokerCard[] spade = new PokerCard[14];

    @Before
    public void setupCase(){
        cases = new CardsPlayUnitTest();
        validator = new Big2CardSetValidator();
        buildCompletedPokerCards();

    }
    public void buildCompletedPokerCards(){
        for(int i = 1;i<14;i++)
            club[i] = new PokerCard(CardSuit.CLUB,i);
        for(int i = 1;i<14;i++)
            diamond[i] = new PokerCard(CardSuit.DIAMOND,i);
        for(int i = 1;i<14;i++)
            heart[i] = new PokerCard(CardSuit.HEART,i);
        for(int i = 1;i<14;i++)
            spade[i] = new PokerCard(CardSuit.SPADE,i);
    }

    @Test
    public  void isSingleTest(){

        caseSingle.addOneCard(heart[13]);

        assertTrue(validator.isSingle(caseSingle));
    }

    @Test
    public void isPairTest(){

        casePair.addOneCard(club[1]);
        casePair.addOneCard(diamond[1]);

        assertTrue(validator.isPair(casePair));
    }

    @Test
    public void isStraightTest(){

        caseStraight.addOneCard(spade[1]);
        caseStraight.addOneCard(heart[2]);
        caseStraight.addOneCard(club[3]);
        caseStraight.addOneCard(club[4]);
        caseStraight.addOneCard(heart[5]);

        assertTrue(validator.isStraight(caseStraightFlush));
    }

    @Test
    public void isFullHouseTest(){

        caseFullhouse.addOneCard(spade[9]);
        caseFullhouse.addOneCard(heart[9]);
        caseFullhouse.addOneCard(diamond[9]);
        caseFullhouse.addOneCard(club[6]);
        caseFullhouse.addOneCard(spade[6]);

        assertTrue(validator.isFullhouse(caseFullhouse));

    }

    @Test
    public void isFourOfaKindTest(){

        caseFourOfaKind.addOneCard(spade[9]);
        caseFourOfaKind.addOneCard(heart[9]);
        caseFourOfaKind.addOneCard(diamond[9]);
        caseFourOfaKind.addOneCard(club[9]);
        caseFourOfaKind.addOneCard(spade[6]);

        assertTrue(validator.isFourOfaKind(caseFourOfaKind));

    }
    @Test
    public void isStraightFlushTest(){

        caseStraightFlush.addOneCard(club[1]);
        caseStraightFlush.addOneCard(club[2]);
        caseStraightFlush.addOneCard(club[3]);
        caseStraightFlush.addOneCard(club[4]);
        caseStraightFlush.addOneCard(club[5]);

        assertTrue(validator.isStraightFlush(caseStraightFlush));
    }

}

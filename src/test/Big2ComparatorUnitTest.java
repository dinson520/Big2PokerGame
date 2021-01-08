package test;

import game.comparator.Big2Comparator;
import game.item.CardSuit;
import game.item.PokerCard;
import game.item.PokerCardDeck;
import game.validator.Big2CardSetValidator;
import org.junit.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
public class Big2ComparatorUnitTest {

    PokerCardDeck newPlay;
    PokerCardDeck onBoard;

    CardsPlayUnitTest cases;
    Big2CardSetValidator validator;
    Big2Comparator comparator;

    /* c = club, d = diamond, h = heart, s = spade */
    PokerCard[] club = new PokerCard[14];
    PokerCard[] diamond = new PokerCard[14];
    PokerCard[] heart = new PokerCard[14];
    PokerCard[] spade = new PokerCard[14];

    @Before
    public void setupCase(){

        cases = new CardsPlayUnitTest();
        validator = new Big2CardSetValidator();

        newPlay = new PokerCardDeck();
        onBoard = new PokerCardDeck();

        buildFullDeckPokerCards();

    }
    public void buildFullDeckPokerCards(){
        for(int i = 1;i<14;i++){
            club[i] = new PokerCard(CardSuit.CLUB,i);
            diamond[i] = new PokerCard(CardSuit.DIAMOND,i);
            heart[i] = new PokerCard(CardSuit.HEART,i);
            spade[i] = new PokerCard(CardSuit.SPADE,i);
        }
    }

    @Test
    public void Single2Single(){

        newPlay.addOneCard(heart[5]);
        onBoard.addOneCard((spade[4]));

        comparator = new Big2Comparator(newPlay, onBoard);

        assertTrue(comparator.isNewPlayAccept());
    }

    @Test
    public void Pair2Pair(){

        newPlay.addOneCard(heart[5]);
        newPlay.addOneCard(spade[5]);
        onBoard.addOneCard((club[5]));
        onBoard.addOneCard((diamond[5]));

        comparator = new Big2Comparator(newPlay, onBoard);

        assertTrue(comparator.isNewPlayAccept());
    }

    @Test
    public void Straight2Straight(){

        newPlay.addOneCard(heart[1]);
        newPlay.addOneCard(heart[10]);
        newPlay.addOneCard(spade[11]);
        newPlay.addOneCard(club[12]);
        newPlay.addOneCard(spade[13]);

        onBoard.addOneCard((club[5]));
        onBoard.addOneCard((diamond[6]));
        onBoard.addOneCard((diamond[8]));
        onBoard.addOneCard((diamond[7]));
        onBoard.addOneCard((diamond[4]));

        comparator = new Big2Comparator(newPlay, onBoard);
        assertTrue(comparator.isNewPlayAccept());

    }

    @Test
    public void FullHouse2FullHouse(){

        newPlay.addOneCard(heart[5]);
        newPlay.addOneCard(club[5]);
        newPlay.addOneCard(club[8]);
        newPlay.addOneCard(spade[5]);
        newPlay.addOneCard(heart[8]);

        onBoard.addOneCard((club[1]));
        onBoard.addOneCard((diamond[1]));
        onBoard.addOneCard((heart[1]));
        onBoard.addOneCard((diamond[6]));
        onBoard.addOneCard((spade[6]));

        comparator = new Big2Comparator(newPlay, onBoard);
        assertFalse(comparator.isNewPlayAccept());

    }
    @Test
    public void FourOfAKind2FourOfAKind(){

        newPlay.addOneCard(heart[12]);
        newPlay.addOneCard(club[12]);
        newPlay.addOneCard(club[12]);
        newPlay.addOneCard(spade[12]);
        newPlay.addOneCard(heart[1]);

        onBoard.addOneCard((club[1]));
        onBoard.addOneCard((diamond[1]));
        onBoard.addOneCard((heart[1]));
        onBoard.addOneCard((diamond[6]));
        onBoard.addOneCard((spade[1]));

        comparator = new Big2Comparator(newPlay, onBoard);
        assertFalse(comparator.isNewPlayAccept());

    }

    @Test
    public void five2asymmetricalCards(){

        newPlay.addOneCard(club[9]);
        newPlay.addOneCard(diamond[9]);
        newPlay.addOneCard(heart[9]);
        newPlay.addOneCard(spade[9]);
        newPlay.addOneCard(heart[8]);

        onBoard.addOneCard((club[11]));
        onBoard.addOneCard((diamond[11]));
        onBoard.addOneCard((club[8]));
        onBoard.addOneCard((club[9]));
        onBoard.addOneCard((club[10]));

        comparator = new Big2Comparator(newPlay, onBoard);
        assertTrue(comparator.isNewPlayAccept());

    }

    @Test
    public void five2asymmetricalCards2(){

        newPlay.addOneCard(club[9]);
//        newPlay.addOneCard(diamond[9]);
//        newPlay.addOneCard(heart[9]);
//        newPlay.addOneCard(spade[9]);
//        newPlay.addOneCard(heart[8]);

//        onBoard.addOneCard((club[11]));
//        onBoard.addOneCard((diamond[11]));
//        onBoard.addOneCard((club[8]));
//        onBoard.addOneCard((club[9]));
//        onBoard.addOneCard((club[10]));

        comparator = new Big2Comparator(newPlay, onBoard);
        assertTrue(comparator.isNewPlayAccept());

    }
}

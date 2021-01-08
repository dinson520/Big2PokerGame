package test;

import game.Big2PokerGame;
import game.item.PokerCard;
import game.roles.Big2Player;
import game.roles.HumanPlayer;
import org.junit.Test;

import java.util.Collection;
public class Big2GameFlowUnitTest {
    @Test
    public void shuffleTest(){
        HumanPlayer[] humanPlayers = new HumanPlayer[4];
        humanPlayers[0] = new HumanPlayer("YD0");
        humanPlayers[1] = new HumanPlayer("YD1");
        humanPlayers[2] = new HumanPlayer("YD2");
        humanPlayers[3] = new HumanPlayer("YD3");

        new Big2PokerGame(humanPlayers);

    }
}

package game.roles;

import game.item.PokerCard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;

public class HumanPlayer extends Big2Player {

	private PokerCard[] onHandCards;


	// 測試中文
	public HumanPlayer(String name) {
		super(name);
	}

//	public HumanPlayer(String name, BufferedReader readFrom, PrintStream writeTo) {
//		super(name, readFrom, writeTo);
//	}

	@Override
	public Collection<PokerCard> onPlayerTurnPlay(Collection<PokerCard> onBoardCards) {
		return null;
	}
}

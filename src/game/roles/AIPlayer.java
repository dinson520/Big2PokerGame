package game.roles;

import game.item.PokerCard;

import java.util.Collection;

public class AIPlayer extends Big2Player {

//	private PokerCard[] onHandCards;
//
	public AIPlayer(String name) {
		super(name);
	}
//

	public static void main(String[] args) {
		System.out.println("中文測試");
	}

	@Override
	public Collection<PokerCard> onPlayerTurnPlay(Collection<PokerCard> onBoardCards) {
		return null;
	}
}

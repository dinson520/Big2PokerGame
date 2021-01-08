package game.roles;

import game.item.*;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Vector;

public abstract class Big2Player {

	private String name;

	private boolean lastActionPass = false;

	private Socket socketClient;

	private Vector v = new Vector();

	private PokerCardDeck handDeck = new PokerCardDeck();

	public Big2Player(String name) {
		this.setName(name);
	}

//	public Big2Player(String name, BufferedReader bufferedReader, PrintStream printStream) {
//		this(name);
//		this.setBufferedReader(bufferedReader);
//		this.setPrintStream(printStream);
//	}

	public abstract Collection<PokerCard> onPlayerTurnPlay(Collection<PokerCard> onBoardCards);

	public PokerCardDeck getHandDeck() {
		return handDeck;
	}

	public void removePlayedCards(PokerCardDeck theDeck){
		handDeck.removeCards(theDeck);
	}
	public void showHandDeck(){
		System.out.print(getName() +" hand deck: " + handDeck.show() );
	}

	public void setHandDeck(PokerCardDeck theDeck) {
		handDeck=theDeck;
	}

	public void addCard(PokerCard theCard){
		handDeck.addOneCard(theCard);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getSocketClient() {
		return socketClient;
	}

	public void setSocketClient(Socket socketClient) {
		this.socketClient = socketClient;
	}

	public PrintStream getPrintStream() {
		PrintStream result = null;
		try {
			result = new PrintStream(socketClient.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public BufferedReader getBufferedReader() {
		BufferedReader result = null;
		try {
			result = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

//	private BufferedReader bufferedReader;
//	private PrintStream printStream;
//
//	public BufferedReader getBufferedReader() {
//		return bufferedReader;
//	}
//
//	public void setBufferedReader(BufferedReader bufferedReader) {
//		this.bufferedReader = bufferedReader;
//	}
//
//	public PrintStream getPrintStream() {
//		return printStream;
//	}
//
//	public void setPrintStream(PrintStream printStream) {
//		this.printStream = printStream;
//	}
}

package game;

import game.comparator.Big2Comparator;
import game.item.Big2CardPlayStyle;
import game.item.CardSuit;
import game.item.PokerCard;
import game.item.PokerCardDeck;
import game.roles.Big2Player;
import game.validator.Big2CardSetValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;


/*
 *
 * Manages the game flow.
 *
 * */

public class Big2PokerGame {

    //    private HashMap<String,Big2Player> playerHashMap = new HashMap<>();


    private Big2Player[] players;

    private Stack<PokerCard> mainDeck = new Stack<>();
//    private Stack<PokerCard> discard = new Stack<>();
    private PokerCardDeck onBoard;
    private int onBoardOwnerIdx = 0;
    private int lastPlayedPlayerIdx = 0;

    public static Big2Player roundWinner = null;

    public static final String ENTER_NICKNAME = "NICKNAME";
    public static final String PLAYER_JOIN = "PlayerJoin";
    public static final String SEND_CARDS = "SendCard";
    public static final String PLAY_CARDS_3C = "PlayCard3C";
    public static final String PLAY_CARDS = "PlayCard";
    public static final PokerCardDeck PASS = new PokerCardDeck();
    public static final String ERROR_OPERATION = "ErrorOperation";
    public static final String PLAY_CARDS_NOTIFY = "PlayCardNotify";
    public static final String PLAY_NEW_ROUND = "PlayNewRound";
    public static final String GAME_OVER = "GameOver";
    public static final String LAST_ONE_CARD = "LastOneCard";
    public static final String CONTINUE_NEXT_SET = "ContinueNextSet";

    public Big2PokerGame(Big2Player[] players) {
        this.players = players;
        play();
    }

    private void play() {
        do {
            prepareMainDeck();
            shuffleMainDeck();
            distributeMainDeckToPlayers();

            playClub3();

            do {
                onNextPlayerTurn();
            } while (!isGameover());

            showRoundScores();

        } while (showYesNoDialog("Continue to new round？"));
    }

    private boolean showYesNoDialog(String message) {
        return getContinuePlayYesNoFromAllPlayers(CONTINUE_NEXT_SET, message);
    }

    private boolean getContinuePlayYesNoFromAllPlayers(String continueNextSet, String message) {

        players[0].getPrintStream().println(continueNextSet + "," + message);
        players[0].getPrintStream().flush();

        players[1].getPrintStream().println(continueNextSet + "," + message);
        players[1].getPrintStream().flush();

        players[2].getPrintStream().println(continueNextSet + "," + message);
        players[2].getPrintStream().flush();

        players[3].getPrintStream().println(continueNextSet + "," + message);
        players[3].getPrintStream().flush();

        try {
            for (int i = 0; i < 4; i++) {
                boolean continuePlay = players[i].getBufferedReader().readLine().trim().toUpperCase().equals("YES");
                if(!continuePlay){
                    System.out.println("有人不想玩了...牌局結束。");
                    players[0].getPrintStream().close();
                    players[0].getBufferedReader().close();
                    players[1].getPrintStream().close();
                    players[1].getBufferedReader().close();
                    players[2].getPrintStream().close();
                    players[2].getBufferedReader().close();
                    players[3].getPrintStream().close();
                    players[3].getBufferedReader().close();
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void prepareMainDeck() {
        for (CardSuit curSuit : CardSuit.values())
            for (int j = 1; j < 14; j++)
                mainDeck.push(new PokerCard(curSuit, j));
    }

    private void shuffleMainDeck() {
        for (int i = 0; i < 5; i++)
            Collections.shuffle((List<?>) mainDeck);
    }

    private void distributeMainDeckToPlayers() {
        for (int i = 0; i < 13; i++) {
            players[0].addCard(mainDeck.pop());
            players[1].addCard(mainDeck.pop());
            players[2].addCard(mainDeck.pop());
            players[3].addCard(mainDeck.pop());
        }
        System.out.println("Distribute all cards to players..");
        for (Big2Player curPlayer : players)
            tcpSendHandDeck(curPlayer);
    }

    private void playClub3() {

        PokerCard club3 = new PokerCard(CardSuit.CLUB, 3);

        int club3PlayerIdx = getIndexOfWhoHaveTheCard(club3);

        PrintStream bfw = players[club3PlayerIdx].getPrintStream();
        BufferedReader bfr = players[club3PlayerIdx].getBufferedReader();

        onBoard = new PokerCardDeck();

        boolean isPlayWithClub3 = false;
        try {
            do {
                bfw.println(PLAY_CARDS_3C);
                bfw.flush();
                String theDeckStr = bfr.readLine();
                PokerCardDeck playerPlayCards = getDeckFromCardsString(theDeckStr);

                Big2CardSetValidator validator = new Big2CardSetValidator();
                Big2CardPlayStyle playStyle = validator.getHighestPlayStyle(playerPlayCards);

                isPlayWithClub3 = playerPlayCards.contains(new PokerCard(CardSuit.CLUB, 3));

                if (isPlayWithClub3 && playStyle != null) {
                    boolean result = updateOnBoardCards(playerPlayCards, onBoard);
                    if (result) {
                        players[club3PlayerIdx].removePlayedCards(playerPlayCards);
                        String playedCards = getCardsStringFromHandDeck(playerPlayCards);
                        notifyAllPlayers(PLAY_CARDS_NOTIFY, players[club3PlayerIdx], playedCards);
                        onBoardOwnerIdx = club3PlayerIdx;
                        lastPlayedPlayerIdx = club3PlayerIdx;
                    }
                }
            }while(!isPlayWithClub3);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onNextPlayerTurn() {

        int thisTurnPlayer = (lastPlayedPlayerIdx+1)%4;

        PrintStream bfw = players[thisTurnPlayer].getPrintStream();
        BufferedReader bfr = players[thisTurnPlayer].getBufferedReader();

        try {
            do {

                String currentEvent = onBoardOwnerIdx == thisTurnPlayer?PLAY_NEW_ROUND:PLAY_CARDS;

                if(currentEvent==PLAY_NEW_ROUND) onBoard = new PokerCardDeck();

                bfw.println(currentEvent);
                bfw.flush();

                String theDeckStr = bfr.readLine();

                PokerCardDeck replyDeck = getDeckFromCardsString(theDeckStr);


                // ERROR OPERATION
                if (currentEvent.equalsIgnoreCase(PLAY_NEW_ROUND) && replyDeck.size()==0) {
                    bfw.println(ERROR_OPERATION);
                    bfw.flush();
                    continue;
                }
                // size == 0 means PASS this turn.
                else if(currentEvent.equalsIgnoreCase(PLAY_CARDS) && replyDeck.size()==0){
                    // player pass..
                    notifyAllPlayers(PLAY_CARDS_NOTIFY,players[thisTurnPlayer],theDeckStr);
                    lastPlayedPlayerIdx = thisTurnPlayer;
                }
//                else if (currentEvent.equalsIgnoreCase(PLAY_NEW_ROUND) && replyDeck.size()!=0){
                else if (replyDeck.size()!=0){
                    boolean result = updateOnBoardCards(replyDeck, onBoard);
                    if (result) {
                        players[thisTurnPlayer].removePlayedCards(replyDeck);
                        String playedCards = getCardsStringFromHandDeck(replyDeck);
                        notifyAllPlayers(PLAY_CARDS_NOTIFY, players[thisTurnPlayer], playedCards);
                        onBoardOwnerIdx = thisTurnPlayer;
//                        if (players[thisTurnPlayer].getHandDeck().size() == 1)
//                            notifyOther3players(players[thisTurnPlayer], LAST_ONE_CARD + "," + players[thisTurnPlayer].getName());
                        lastPlayedPlayerIdx = thisTurnPlayer;
                    } else {
                        bfw.println(ERROR_OPERATION);
                        bfw.flush();
                    }
//                } else {
//                    PokerCardDeck playerPlayCards = getDeckFromCardsString(theDeckStr);
//
//                    boolean result = updateOnBoardCards(playerPlayCards);
//                    if (result) {
//                        players[thisTurnPlayer].removePlayedCards(playerPlayCards);
//                        String playedCards = getCardsStringFromHandDeck(playerPlayCards);
//                        notifyAllPlayers(PLAY_CARDS_NOTIFY, players[thisTurnPlayer], playedCards);
//                        onBoardOwnerIdx = thisTurnPlayer;
//                        if (players[thisTurnPlayer].getHandDeck().size() == 1)
//                            notifyOther3players(players[thisTurnPlayer], LAST_ONE_CARD + "," + players[thisTurnPlayer].getName());
//                        lastPlayedPlayerIdx = thisTurnPlayer;
//                    } else {
//                        bfw.println(ERROR_OPERATION);
//                        bfw.flush();
//                    }
                }
            }while(lastPlayedPlayerIdx != thisTurnPlayer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isGameover() {
        boolean result = false;

        for (Big2Player player : players) {
            if (player.getHandDeck().size() == 0) {
                result = true;
                roundWinner = player;
                break;
            }
        }
        return result;
    }

    private void showRoundScores() {
    }

    ;

    private void notifyAllPlayers(String eventString, Big2Player eventOwner, String deckString) {

        String messageCombine = eventString + "=" + eventOwner.getName() + "," + deckString;

        for (Big2Player thePlayer : players) {
            serverLog(" -> "+thePlayer.getName()+" {"+messageCombine+"}");
            thePlayer.getPrintStream().println(messageCombine);
            thePlayer.getPrintStream().flush();
        }
    }

    private void notifyOther3players(Big2Player exceptPlayer, String message) {

        for (int i = 0; i < 4; i++) {
            if (players[i] == exceptPlayer) continue;
            players[i].getPrintStream().println(message);
            players[i].getPrintStream().flush();
            try {
                players[i].getBufferedReader().readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int findNextPlayerIndex() {

        for (int i = 0; i < 4; i++)
            if (players[i].getName().equals(onBoardOwnerIdx)) return i == 3 ? 0 : i + 1;

        throw new RuntimeException("Should not reached! ...");
    }

    private int getIndexOfWhoHaveTheCard(PokerCard theCard) {

        for (int i =0;i<4;i++) {
            for (PokerCard curCard : players[i].getHandDeck().getCollection()) {
                if (curCard.isSame(theCard)) return i;
            }
        }

        throw new IllegalStateException();
    }

    private boolean updateOnBoardCards(PokerCardDeck newPlay, PokerCardDeck onBoard) {

        Big2Comparator big2Comparator = new Big2Comparator(newPlay, onBoard);

        if (big2Comparator.isNewPlayAccept()) {
            onBoard = newPlay;
            return true;
        }

        return false;
    }

    private void tcpSendHandDeck(Big2Player player) {

        BufferedReader bfr = player.getBufferedReader();
        PrintStream bfw = player.getPrintStream();

        String message = SEND_CARDS + "=" + getCardsStringFromHandDeck(player.getHandDeck());

        bfw.println(message);
        bfw.flush();

        serverLog(message + " -> send to " + player.getName());

    }

    public static PokerCardDeck getDeckFromCardsString(String deckStr) {

        PokerCardDeck resultDeck = new PokerCardDeck();

        if(deckStr==null || deckStr.length()==0 || deckStr.equalsIgnoreCase("pass")) return resultDeck;

        String[] tempArray = deckStr.toUpperCase().split(",");

        for (String curStr : tempArray) {

            CardSuit suit = CardSuit.getCardSuitFromString(curStr.substring(0, 1));
            Integer point = Integer.parseInt(curStr.substring(1));

            PokerCard theCard = new PokerCard(suit, point);
            theCard.setShowCardBack(false);
            resultDeck.addOneCard(theCard);
        }
        return resultDeck;
    }

    public static String getCardsStringFromHandDeck(PokerCardDeck deck) {
        StringBuilder result = new StringBuilder();
        for (PokerCard curCard : deck.getCollection()) {
            result.append(curCard.cardSuit.toSingleCharacter()).append(curCard.cardPoint).append(",");
        }
        return result.toString();
    }

    private void serverLog(String outStr){
        System.out.println("serverLog: "+outStr);
    }

}

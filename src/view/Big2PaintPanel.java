package view;

import game.comparator.Big2Comparator;
import game.item.Big2CardPlayStyle;
import game.item.CardSuit;
import game.item.PokerCard;
import game.item.PokerCardDeck;
import game.validator.Big2CardSetValidator;
import media.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;

import static game.Big2PokerGame.*;

public class Big2PaintPanel extends JPanel implements PropertyChangeListener, ActionListener {

    private String myName = "";

    private final int panelWidth = Big2GameView.frameWidth;
    private final int panelHeight = Big2GameView.frameHeight;

    private JLayeredPane layeredPane;
    private Point anchorCenter;


    private final Point[] anchorHandDeck = new Point[4];
    private final Point[] anchorState = new Point[4];
    private final Point[] anchorPlay = new Point[4];
    private PokerCardDeck[] playerPlayOnBoard = new PokerCardDeck[4];

    private int[] handDeckLeft = {13,13,13,13};
//    private JLabel[] playerNameAndCardsLeftDisplay = new JLabel[4];

    // -1= not engaged, 0=pass, 1=have play cards, 2=New round starter.

    public static final int NOT_ENGAGED = -1;
    public static final int PASS = 0;
    public static final int PLAYED = 1;
    public static final int NEW_ROUND_STARTER = 2;

    private int[] playerLastHandAction = {NOT_ENGAGED,NOT_ENGAGED,NOT_ENGAGED,NOT_ENGAGED};

//    private PokerCardDeck onBoardDeck;



    //----------------------------------------VIEW-------------------------------------------------
    private Graphics g;


    public static final int halfCardWidth = PokerCard.cardWidth / 2;
    public static final int halfCardHeight = PokerCard.cardHeight / 2;

    private PropertyChangeEvent evt;
    public String[] playerNameSequence;  // 0 = down(current user), 1 = left, 2 = up, 3 = right
    private int boardOwnerNameSequenceIdx = 0;

    private final PokerCardDeck downDeck = new PokerCardDeck();

    private boolean gameStart = false;

    private final JButton buttonPass = new JButton();
    private final JButton buttonPlay = new JButton();
    private final JButton buttonCancelSelected = new JButton();

    private final Font inkFree20 = new Font("Ink Free", Font.BOLD,20);
    private final Font inkFree40 = new Font("Ink Free", Font.BOLD,40);

    //----------------------------------------VIEW-------------------------------------------------

    private final int horizontalGap = 50;
    private final int verticalGap = 30;

    private final boolean isDrawAnchor = false;

    private final SoundPlayer soundPlayer = new SoundPlayer();

    public Big2PaintPanel(String userName) {
        myName = userName;
        this.setupPanelUI();
        this.setupAnchors();
        this.setupComponents();
    }

    private void setupPanelUI() {

        this.setLayout(null);
        this.setBounds(0, 0, panelWidth, panelHeight);
        this.setBackground(new Color(224, 20, 91));
//        this.setOpaque(true);

        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, panelWidth, panelHeight);
        layeredPane.setBackground(new Color(31, 77, 123));
        layeredPane.setOpaque(true);


        this.add(layeredPane);

    }

    private void setupAnchors() {

        int statGapX = 360;
        int statGapY = 170;

        anchorCenter = new Point((int) (panelWidth * 0.5), (int) (panelHeight * 0.5 - 10));

        // down position
        anchorHandDeck[0] = new Point((int) (panelWidth * 0.5), (int) (panelHeight * 0.8));
        anchorPlay[0] = new Point(anchorCenter.x-20, anchorCenter.y + 50);
        anchorState[0] = new Point(anchorCenter.x + statGapX, anchorCenter.y + statGapY-20);

        // left position
        anchorHandDeck[1] = new Point((int) (panelWidth * 0.07), (int) (panelHeight * 0.5));
        anchorPlay[1] = new Point(anchorCenter.x - 200, anchorCenter.y);
        anchorState[1] = new Point(anchorCenter.x - statGapX - 130, anchorCenter.y + statGapY-20);

        // up position
        anchorHandDeck[2] = new Point((int) (panelWidth * 0.5), (int) (panelHeight * 0.15));
        anchorPlay[2] = new Point(anchorCenter.x+20, anchorCenter.y - 50);
        anchorState[2] = new Point(anchorCenter.x - statGapX - 130, anchorCenter.y - statGapY);

        // right position
        anchorHandDeck[3] = new Point((int) (panelWidth * 0.92), (int) (panelHeight * 0.5));
        anchorPlay[3] = new Point(anchorCenter.x + 200, anchorCenter.y);
        anchorState[3] = new Point(anchorCenter.x + statGapX, anchorCenter.y - statGapY);

    }

    private void setupComponents() {

        buttonPlay.setText("Play");
        buttonPlay.setBounds(anchorHandDeck[0].x + 400, anchorHandDeck[0].y - 100, 100, 50);
        buttonPlay.setFont(inkFree20);
        buttonPlay.setEnabled(false);
        buttonPlay.setVisible(false);
        buttonPlay.addActionListener(this);

        buttonPass.setText("Pass");
        buttonPass.setBounds(anchorHandDeck[0].x + 400, anchorHandDeck[0].y-30, 100, 50);
        buttonPass.setFont(inkFree20);
        buttonPass.setEnabled(false);
        buttonPass.setVisible(false);
        buttonPass.addActionListener(this);

        buttonCancelSelected.setText("Cancel");
        buttonCancelSelected.setBounds(anchorHandDeck[0].x + 400, anchorHandDeck[0].y+40, 100, 50);
        buttonCancelSelected.setFont(inkFree20);
        buttonCancelSelected.setEnabled(false);
        buttonCancelSelected.setVisible(false);
        buttonCancelSelected.addActionListener(this);

        layeredPane.add(buttonPlay, 20);
        layeredPane.add(buttonPass, 20);
        layeredPane.add(buttonCancelSelected, 20);

    }


    private void addTheDeckToAnchor(PokerCardDeck theDeck, Point theAnchor, boolean isHorizontalAlignment) {

        if(theDeck==null||theDeck.size()==0) return;

        int cardCommonGap = 0;

        PokerCard[] handDeckArray = theDeck.getSortedDeckArray();

        cardCommonGap = isHorizontalAlignment ? horizontalGap : verticalGap;

        int halfCardWidth = PokerCard.cardWidth / 2;
        int halfCardHeight = PokerCard.cardHeight / 2;

        boolean isHandDeckOdd = theDeck.size() % 2 == 1;
        Point firstCardLocation = new Point();
        if (isHorizontalAlignment) {
            if (isHandDeckOdd)
                firstCardLocation = new Point(
                        (theAnchor.x - halfCardWidth) - (theDeck.size() - 1) / 2 * cardCommonGap,
                        theAnchor.y - halfCardHeight);
            else
                firstCardLocation = new Point(
                        (theAnchor.x - halfCardWidth) - (theDeck.size()) / 2 * cardCommonGap + cardCommonGap / 2,
                        theAnchor.y - halfCardHeight);
        } else {
            if (isHandDeckOdd)
                firstCardLocation = new Point(
                        theAnchor.x - halfCardWidth,
                        theAnchor.y - halfCardHeight - (theDeck.size() - 1) / 2 * cardCommonGap);
            else
                firstCardLocation = new Point(
                        theAnchor.x - halfCardWidth,
                        theAnchor.y - halfCardHeight - (theDeck.size()) / 2 * cardCommonGap + cardCommonGap / 2);
        }

        for (int k = 0; k < handDeckArray.length; k++) {

            PokerCard currentCard = handDeckArray[k];


            boolean listenerExist = false;

            for(MouseListener curListener:currentCard.getMouseListeners()){
                if(curListener!=pokerSelection) continue;
                else {
                    listenerExist = true;
                    break;
                }
            }
            if(!listenerExist) currentCard.addMouseListener(pokerSelection);


            if (isHorizontalAlignment)
                currentCard.setLocation((firstCardLocation.x + k * cardCommonGap), firstCardLocation.y);
            else
                currentCard.setLocation((firstCardLocation.x), (firstCardLocation.y + k * cardCommonGap));

            layeredPane.remove(currentCard);
            layeredPane.add(currentCard, Integer.valueOf(k));
        }
    }

    private void drawOtherPlayerHandDeckWithCardBack(int theDeckNum, Point theAnchor, boolean isHorizontalAlignment) {

        int cardCommonGap = 0;

        cardCommonGap = isHorizontalAlignment ? horizontalGap : verticalGap;

        int halfCardWidth = PokerCard.cardWidth / 2;
        int halfCardHeight = PokerCard.cardHeight / 2;

        boolean isHandDeckOdd = theDeckNum % 2 == 1;
        Point firstCardLocation;
        if (isHorizontalAlignment) {
            if (isHandDeckOdd)
                firstCardLocation = new Point(
                        (theAnchor.x - halfCardWidth) - (theDeckNum- 1) / 2 * cardCommonGap,
                        theAnchor.y - halfCardHeight);
            else
                firstCardLocation = new Point(
                        (theAnchor.x - halfCardWidth) - (theDeckNum) / 2 * cardCommonGap + cardCommonGap / 2,
                        theAnchor.y - halfCardHeight);
        } else {
            if (isHandDeckOdd)
                firstCardLocation = new Point(
                        theAnchor.x - halfCardWidth,
                        theAnchor.y - halfCardHeight - (theDeckNum - 1) / 2 * cardCommonGap);
            else
                firstCardLocation = new Point(
                        theAnchor.x - halfCardWidth,
                        theAnchor.y - halfCardHeight - (theDeckNum) / 2 * cardCommonGap + cardCommonGap / 2);
        }

        Image im = new ImageIcon("Res/PNG140/back_red.png").getImage();

        for (int k = 0; k < theDeckNum; k++) {

            if (isHorizontalAlignment)
                g.drawImage(im, firstCardLocation.x + k * cardCommonGap, firstCardLocation.y, PokerCard.cardWidth, PokerCard.cardHeight, this);
            else
                g.drawImage(im, firstCardLocation.x, firstCardLocation.y + k * cardCommonGap, PokerCard.cardWidth, PokerCard.cardHeight, this);
        }

        repaint();

    }


//    private void createPlayerState(String[] tempNameArr) {
//
//        for(int i =0;i<tempNameArr.length;i++)
//            if (playerNameAndCardsLeftDisplay[i] == null) playerNameAndCardsLeftDisplay[i] = createStateLabel(tempNameArr[i], 13);
//
//        if(playerNameAndCardsLeftDisplay[0]!=null) playerNameAndCardsLeftDisplay[0].setLocation(anchorState[0].x, anchorState[0].y);
//
//        if(playerNameAndCardsLeftDisplay[1]!=null) playerNameAndCardsLeftDisplay[1].setLocation(anchorState[1].x, anchorState[1].y);
//
//        if(playerNameAndCardsLeftDisplay[2]!=null) playerNameAndCardsLeftDisplay[2].setLocation(anchorState[2].x, anchorState[2].y);
//
//        if(playerNameAndCardsLeftDisplay[3]!=null) playerNameAndCardsLeftDisplay[3].setLocation(anchorState[3].x, anchorState[3].y);
//
//        for(JLabel curLabel: playerNameAndCardsLeftDisplay)
//            if(curLabel!=null && curLabel.getParent()==null) layeredPane.add(curLabel);
////            if(curLabel!=null &) layeredPane.add(curLabel);
//    }
//
//    private JLabel createStateLabel(String name, int cardsNum){
//        JLabel result = new JLabel();
//        result.setSize(130, 20);
//        result.setForeground(new Color(46, 252, 3));
//        result.setFont(new Font("Ink Free", Font.BOLD,20));
//        result.setText("<html>" +name+ "(" +cardsNum+ ")</html>");
//        result.setHorizontalAlignment(JLabel.CENTER);
//        result.setVerticalAlignment(JLabel.CENTER);
//        return result;
//    }



    private void drawPlayedCard(int playerIdx, PokerCardDeck playedCard) {

        if(playedCard==null) return;

        boolean isDeckOdd = playedCard.size() % 2 == 1;

        Point firstCardLocation = new Point((anchorPlay[playerIdx].x - halfCardWidth) - (playedCard.size() - 1) / 2 * horizontalGap,anchorPlay[playerIdx].y - halfCardHeight);

        if (isDeckOdd) firstCardLocation.x =   (anchorPlay[playerIdx].x - halfCardWidth) - (playedCard.size()) / 2 * horizontalGap + horizontalGap / 2;

        PokerCard[] pcArr = playedCard.getSortedDeckArray();

        for (int k = 0; k < playedCard.size(); k++) {
            Image im = new ImageIcon(pcArr[k].getPokerCardFilename()).getImage();
            g.drawImage(im, firstCardLocation.x + k * horizontalGap, firstCardLocation.y, PokerCard.cardWidth, PokerCard.cardHeight, this);
        }
        repaint();

    }

    private int findPlayerIndexByPlayerName(String name){
        for(int i = 0;i<4;i++)
            if(playerNameSequence[i].equals(name)) return i;
        throw new IllegalStateException();
    }

    private void removeCardsFromPlayerDeck(int playedPlayIdx, PokerCardDeck playedCard) {

        if (playedCard == null || playedCard.size() == 0) return;

        if (myName.equals(playerNameSequence[playedPlayIdx])) {
            //  downDeck, have already removed before server reply entry, which cause object equals error, hence remove will go fail.
            repaint();
        } else
            handDeckLeft[playedPlayIdx] -= playedCard.size();

        return;

    }

    private boolean isPlayable(){

        PokerCardDeck selected = downDeck.getSelectedCards();

        Big2CardSetValidator validator = new Big2CardSetValidator();

        Big2Comparator comparator = new Big2Comparator(selected, playerPlayOnBoard[boardOwnerNameSequenceIdx]);

        Big2CardPlayStyle playStyle = validator.getHighestPlayStyle(selected);

        String eventName = evt.getPropertyName();

        if(PLAY_CARDS_3C.equals(eventName)){
            if(selected.contains(new PokerCard(CardSuit.CLUB,3)) && playStyle!=null)
                return true;
        }
        else {
                if (comparator.isNewPlayAccept())
                    return true;
        }
        return false;
    }

    private void setButtonDown(){
        buttonPlay.setVisible(false);
        buttonPlay.setEnabled(false);
        buttonPass.setVisible(false);
        buttonPass.setEnabled(false);
        buttonCancelSelected.setVisible(false);
        buttonCancelSelected.setEnabled(false);
    }
    private void setButtonUp(){
        buttonPlay.setVisible(true);
        buttonPlay.setEnabled(true);
        buttonPass.setVisible(true);
        buttonPass.setEnabled(true);
        buttonCancelSelected.setVisible(true);
        buttonCancelSelected.setEnabled(true);
    }

    public void paint(Graphics g) {

        super.paint(g);

        this.g = g;

        g.setColor(new Color(5, 243, 143));
        g.setFont(inkFree20);


        if (gameStart) {
//            addTheDeckToAnchor(downDeck,anchorHandDeck[0],true);
            drawOtherPlayerHandDeckWithCardBack(handDeckLeft[1], anchorHandDeck[1], false);
            drawOtherPlayerHandDeckWithCardBack(handDeckLeft[2], anchorHandDeck[2], true);
            drawOtherPlayerHandDeckWithCardBack(handDeckLeft[3], anchorHandDeck[3], false);

            // 先畫出牌的下一家，最後才畫出牌的
            for(int i = 1;i<5;i++){

                int drawPlaySequenceIndex = (boardOwnerNameSequenceIdx +i)%4;
                drawPlayedCard(drawPlaySequenceIndex, playerPlayOnBoard[drawPlaySequenceIndex]);

                g.drawString(playerNameSequence[i-1]+"("+handDeckLeft[i-1]+")", anchorState[i-1].x, anchorState[i-1].y);

                if(playerLastHandAction[drawPlaySequenceIndex]==0){
                    g.setColor(Color.RED);
                    g.setFont(inkFree40);

                    g.drawString("PASS!",anchorPlay[drawPlaySequenceIndex].x,anchorPlay[drawPlaySequenceIndex].y);

                    g.setColor(new Color(5, 243, 143));
                    g.setFont(inkFree20);

                }


            }
        }




        int anchorShowSize = 10;
        g.setColor(Color.RED);
        if (isDrawAnchor) {
//            g.fillOval(anchorUp.x-anchorShowSize/2, anchorUp.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
//            g.fillOval(anchorDown.x-anchorShowSize/2, anchorDown.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
//            g.fillOval(anchorLeft.x-anchorShowSize/2, anchorLeft.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
//            g.fillOval(anchorRight.x-anchorShowSize/2, anchorRight.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
            g.fillOval(anchorCenter.x - anchorShowSize / 2, anchorCenter.y - anchorShowSize / 2, anchorShowSize, anchorShowSize);

            g.fillOval(anchorState[0].x - anchorShowSize / 2, anchorState[0].y - anchorShowSize / 2, anchorShowSize, anchorShowSize);
            g.fillOval(anchorState[1].x - anchorShowSize / 2, anchorState[1].y - anchorShowSize / 2, anchorShowSize, anchorShowSize);
            g.fillOval(anchorState[2].x - anchorShowSize / 2, anchorState[2].y - anchorShowSize / 2, anchorShowSize, anchorShowSize);
            g.fillOval(anchorState[3].x - anchorShowSize / 2, anchorState[3].y - anchorShowSize / 2, anchorShowSize, anchorShowSize);

//            g.fillOval(anchorUpPlay.x-anchorShowSize/2, anchorUpPlay.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
//            g.fillOval(anchorDownPlay.x-anchorShowSize/2, anchorDownPlay.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
//            g.fillOval(anchorLeftPlay.x-anchorShowSize/2, anchorLeftPlay.y-anchorShowSize/2,anchorShowSize,anchorShowSize);
//            g.fillOval(anchorRightPlay.x-anchorShowSize/2, anchorRightPlay.y-anchorShowSize/2,anchorShowSize,anchorShowSize);

        }

    }
    private MouseListener pokerSelection = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {

            PokerCard pokerLabel = (PokerCard) e.getSource();

            if(!pokerLabel.isSelected()){

                PokerCardDeck selected = downDeck.getSelectedCards();
                if(selected.size()>=5) {
                    soundPlayer.playSound("Res/media/error.wav");
                    return;
                }

            }

            pokerLabel.click();

            soundPlayer.playSound("Res/media/select_a_card.wav");
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    };

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        this.evt = evt;

        switch (evt.getPropertyName()) {
            case PLAYER_JOIN:
                playerNameSequence = (String[]) evt.getNewValue();
//                createPlayerState(playerNameSequence);
                break;

            case SEND_CARDS:
                downDeck.addCards((PokerCardDeck) evt.getNewValue());         // userDeck
                addTheDeckToAnchor(downDeck, anchorHandDeck[0], true);

                gameStart = true;
                repaint();
                break;

            case PLAY_NEW_ROUND:
                for(int i = 0;i<4;i++){
                    playerPlayOnBoard[i] = new PokerCardDeck();
//                    playerNameAndCardsLeftDisplay[i].setText(playerNameSequence[i]+"("+ handDeckLeft[i]+")");
                    playerLastHandAction[i] = NOT_ENGAGED;
                }
                playerLastHandAction[0] = NEW_ROUND_STARTER;
                repaint();

                setButtonUp();
                buttonPass.setEnabled(false);
                break;

            case ERROR_OPERATION:
                soundPlayer.playSound("Res/media/error.wav");
                setButtonUp();
                break;

            case PLAY_CARDS:
                setButtonUp();
                break;

            case PLAY_CARDS_3C:
                setButtonUp();
                buttonPass.setEnabled(false);
                break;

            case PLAY_CARDS_NOTIFY:
                String playerName = (String) evt.getOldValue();
                PokerCardDeck playedCard = (PokerCardDeck) evt.getNewValue();

                int playerIdx = findPlayerIndexByPlayerName(playerName);

                // case = PASS
                if(playedCard==null || playedCard.size()==0){
                    soundPlayer.playSound("Res/media/pass.wav");
                    playerPlayOnBoard[playerIdx] = new PokerCardDeck();
                    playerLastHandAction[playerIdx] = PASS;

                }
                // case = PLAYED
                else {
                    // displayPlayedCard(playerName, playedCard);
                    removeCardsFromPlayerDeck(playerIdx, playedCard);

                    playerPlayOnBoard[playerIdx] = playedCard;
                    boardOwnerNameSequenceIdx = playerIdx;
                    playerLastHandAction[playerIdx] = PLAYED;
                }
                repaint();
                break;

            case GAME_OVER:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + evt.getPropertyName());
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrintStream writer = (PrintStream) evt.getNewValue();

        if(e.getSource()==buttonPlay){
            PokerCardDeck selected = downDeck.getSelectedCards();
            System.out.println(playerNameSequence[0]+" "+downDeck);
            if(isPlayable()){
                String reply = getCardsStringFromHandDeck(selected);

                writer.println(reply);
                writer.flush();

                downDeck.removeCards(selected);
                for(PokerCard curCard: selected.getCollection())
                    layeredPane.remove(curCard);

                handDeckLeft[0] = downDeck.size();

                // re-add to align center.
                addTheDeckToAnchor(downDeck,anchorHandDeck[0],true);

                setButtonDown();
            }
            else{
                soundPlayer.playSound("Res/media/error.wav");
                return;
            }
        }
        if(e.getSource()==buttonPass){

            // 如果前兩家已經 pass，那我再按 pass 就全過了。

            for (PokerCard curCard:downDeck.getCollection()) {
                if(curCard.isSelected())
                    curCard.click();  // reverse the selection.
            }
//            reply = Big2PokerGame.PASS;
            String pass = "";
//            reply = getCardsStringFromHandDeck(PASS);
            writer.println(pass);
            writer.flush();
            setButtonDown();
        }
        if(e.getSource()==buttonCancelSelected){
            PokerCardDeck selected = downDeck.getSelectedCards();
            for(PokerCard curCard:selected.getCollection())
                curCard.click();  // reverse the selection.
        }

        writer.flush();

    }

}

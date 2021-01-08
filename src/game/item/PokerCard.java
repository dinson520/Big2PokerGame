package game.item;

import media.SoundPlayer;

import javax.swing.*;
public class PokerCard extends JLabel {

    public CardSuit cardSuit;
    public int cardPoint;


//    public static int cardWidth = 150;
//    public static int cardHeight = 229;
//    private String cardPictureFolderPath = "Res\\PNG150\\";
//    private String cardBackFilename = "red_back";

    public static int cardWidth = 140;
    public static int cardHeight = 214;
    private String cardPictureFolderPath = "Res\\PNG140\\";
    private String cardBackFilename = "back_red";

    private String cardPictureFilenameExtention = ".png";

//    private String cardImageFilePath;

    private boolean isSelected = false;
    private boolean isShowCardBack = false;

    public PokerCard(){}
    public PokerCard(CardSuit cardSuit, int cardPoint){
        this.cardSuit = cardSuit;
        this.cardPoint = cardPoint;
        this.setSize(cardWidth,cardHeight);
        this.setIcon(new ImageIcon(getPokerCardFilename()));
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
    }
    public PokerCard(CardSuit cardSuit, int cardPoint, boolean isShowCardBack){

        this(cardSuit,cardPoint);

        this.isShowCardBack = isShowCardBack;
        this.setIcon(new ImageIcon(getPokerCardFilename()));

    }
    public String getPokerCardFilename(){

        String suit;

        switch (this.cardSuit) {
            case CLUB:
                suit = "C";
                break;
            case DIAMOND:
                suit = "D";
                break;
            case HEART:
                suit = "H";
                break;
            case SPADE:
                suit = "S";
                break;
            default:
                throw new RuntimeException("Should not reached....!!!");
        }

        String cardFilename = isShowCardBack?cardBackFilename:suit+this.cardPoint;

        return cardPictureFolderPath+cardFilename+cardPictureFilenameExtention;
    }

//    @Override
//    public void setLocation(int x, int y){
//        super.setLocation(x-cardWidth/2, y-cardHeight/2);
//    }
//---------------------Getter and setter---------------------


    public String toString(){
        return cardSuit.toSingleCharacter()+cardPoint;
    }

    public boolean isSelected() {
        return isSelected;
    }

    private void setSelected(boolean selected) {
            isSelected = selected;
    }

    public void click(){

        SoundPlayer sp = new SoundPlayer();
        sp.playSound("Res/media/select_a_card.wav");
        if(isSelected==false){
            isSelected = true;
            this.setLocation(getX(),getY()-30);}
        else{
            isSelected = false;
            this.setLocation(getX(),getY()+30);}

    }

    public boolean isShowCardBack() {
        return isShowCardBack;
    }

    public void setShowCardBack(boolean showCardBack) {
        isShowCardBack = showCardBack;
    }

    public boolean isSame(PokerCard theCard) {
        return theCard.cardSuit == this.cardSuit && theCard.cardPoint == this.cardPoint;
    }

}

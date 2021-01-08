package game;

import game.item.PokerCardDeck;
import game.roles.Big2Player;
import game.roles.HumanPlayer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;

import static game.Big2PokerGame.*;

public class Big2Client extends Thread {


    private Socket socketClient;
    BufferedReader reader;
    PrintStream writer;

//    private HashMap<Integer,String, Integer> cardsLeft = new HashMap<>();

    private int maximalTimeoutCount = 2;

    private Big2Player thisPlayer;
    private int myPositionIndex = 999;

    public String[] playerNameSequence;

    private PropertyChangeSupport notifyGUI = new PropertyChangeSupport(this);

    public Big2Client(String userName) {

        setThisPlayer(new HumanPlayer(userName));

    }


    @Override
    public void run() {startInteractive();}

    public Collection<String[]> scanLAN4GameDesks() {

        Collection<String[]> gameDesks = new ArrayList<>();

        try {
            DatagramSocket udpClient = new DatagramSocket();
            udpClient.setSoTimeout(1000);

            String localhostIP = InetAddress.getLocalHost().getHostAddress();
            String localhostDomainPrefix = localhostIP.substring(0, localhostIP.lastIndexOf(".") + 1);
            InetAddress broadcastIP = InetAddress.getByName(localhostDomainPrefix + 255);

            int timeoutCount = 0;

            String sayHi = "Hi, someone there?";

            byte[] sayHiBytes = sayHi.getBytes();

            DatagramPacket broadcastPacket = new DatagramPacket(sayHiBytes, sayHiBytes.length, broadcastIP, Big2Server.udpPort);
            udpClient.send(broadcastPacket);

            for (; ; ) {
                DatagramPacket udpReplyPacket = new DatagramPacket(new byte[4096], 4096);
                try {
                    udpClient.receive(udpReplyPacket);
                } catch (SocketTimeoutException e) {
                    System.out.println("UDP receive timeout: " + ++timeoutCount);
                    if (timeoutCount == maximalTimeoutCount) {
                        break;
                    }
                }

                String serverReturnedData = new String(udpReplyPacket.getData()).trim();
                // Y,0,2.6.2.7 means owner = Y, joined client = 0, serverIP = 2.6.2.7, minimal length will be 10.
                if (serverReturnedData.length() >= 10) {

                    String serverReplyData = new String(udpReplyPacket.getData());
                    System.out.println("scanLAN4GameDesks() -> "+serverReplyData);

                    System.out.println("Server reply: from " + udpReplyPacket.getAddress() + ":" + udpReplyPacket.getPort());
                    String[] gameDeskInfo = serverReplyData.split(",");

                    System.out.println("Server reply: GameDesk info = " + new String(udpReplyPacket.getData()));

                    gameDesks.add(gameDeskInfo);
                }
//                else {
//                    System.out.println("Server reply GameDesk info(not valid): " + new String(udpReplyPacket.getData()));
//                    System.out.println("Throw it away.");
//                }

            }

            udpClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return gameDesks;
    }

    public void connectToGameDesk(String gameDeskAddress, int gameDeskPort) {

        try {
            setSocketClient(new Socket(InetAddress.getByName(gameDeskAddress), gameDeskPort));

            reader = new BufferedReader(new InputStreamReader(getSocketClient().getInputStream()));
            writer = new PrintStream(getSocketClient().getOutputStream());

            if (getThisPlayer() == null)
                setThisPlayer(new HumanPlayer(thisPlayer.getName()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(socketClient==null)
            return false;
        return getSocketClient().isConnected();
    }

    private void startInteractive() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {

                System.out.println("SERVER >>> " + message);

                String[] messageArray = message.split("=");

                switch (messageArray[0]) {
                    case ENTER_NICKNAME:
                        writer.println(getThisPlayer().getName());
                        writer.flush();
                        break;

                    case PLAYER_JOIN:
                        String[] playerNameList = messageArray[1].split(",");
                        if (myPositionIndex > playerNameList.length - 1)
                            myPositionIndex = playerNameList.length - 1;

                        playerNameList = leftRotateString(playerNameList, myPositionIndex);

                        pc_playerJoin(playerNameList);
                        break;

                    case SEND_CARDS:
                        thisPlayer.setHandDeck(getDeckFromCardsString(messageArray[1]));
                        pc_sendCard(thisPlayer.getHandDeck());
                        break;

                    case PLAY_NEW_ROUND:
                        pc_playNewRound();
                        break;

                    case PLAY_CARDS:
                        pc_playCard(writer);
                        break;

                    case PLAY_CARDS_3C:
                        pc_playCard3C(writer);
                        break;

                    case ERROR_OPERATION:
                        pc_errorOperation(writer);
                        break;

                    case LAST_ONE_CARD:
                        break;

                    case PLAY_CARDS_NOTIFY:
                        String[] playInfo = messageArray[1].split(",");
                        String playerName = playInfo[0];

                        StringBuilder cardsString = new StringBuilder();
                        for (int i = 1; i < messageArray[1].split(",").length; i++)
                            cardsString.append(playInfo[i] + ",");

                        pc_receiveNotify(playerName, getDeckFromCardsString(cardsString.toString()));
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(getName()+": process end....>\"< Orz....");
    }

    //------------------------------------------------------- inform panel to paint -----------------------------------------------------
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        notifyGUI.addPropertyChangeListener(pcl);
    }

    private void pc_playerJoin(String[] nameArray) {
        notifyGUI.firePropertyChange(PLAYER_JOIN, null, nameArray);
    }

    private void pc_sendCard(PokerCardDeck userHandDeck) {
        notifyGUI.firePropertyChange(SEND_CARDS, null, userHandDeck);
    }

    private void pc_playNewRound() {
        notifyGUI.firePropertyChange(PLAY_NEW_ROUND, null, writer);
    }

    private void pc_playCard3C(PrintStream writer) {
        notifyGUI.firePropertyChange(PLAY_CARDS_3C, null, writer);
    }

    private void pc_playCard(PrintStream writer) {
        notifyGUI.firePropertyChange(PLAY_CARDS, null, writer);
    }
    private void pc_errorOperation(PrintStream writer) {
        notifyGUI.firePropertyChange(ERROR_OPERATION, null, writer);
    }

    private void pc_receiveNotify(String playerName, PokerCardDeck playedCard) {
        notifyGUI.firePropertyChange(PLAY_CARDS_NOTIFY, playerName, playedCard);
    }

    public Big2Player getThisPlayer() {
        return thisPlayer;
    }

    public void setThisPlayer(Big2Player thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    public Socket getSocketClient() {
        return socketClient;
    }

    public void setSocketClient(Socket socketClient) {
        this.socketClient = socketClient;
    }

    public void closeSocketClient() {
        if(socketClient!=null && socketClient.isConnected()) {
            try {
                socketClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] leftRotateString(String[] theStrArr, int times){

        for(int k = 0;k<times;k++) {
            String firstStr = theStrArr[0];

            for (int i = 0; i < theStrArr.length - 1; i++)
                theStrArr[i] = theStrArr[i + 1];

            theStrArr[theStrArr.length - 1] = firstStr;
        }
        return theStrArr;
    }

    public static void main(String[] args) {

        try {
            Big2Client client2 = new Big2Client("Client2");
            client2.connectToGameDesk("127.0.0.1",11223);
            client2.start();

            Thread.sleep(1000);


            Big2Client client3 = new Big2Client("Client3");
            client3.connectToGameDesk("127.0.0.1",11223);
            client3.start();


            Thread.sleep(1000);

            Big2Client client4 = new Big2Client("Client4");
            client4.connectToGameDesk("127.0.0.1",11223);
            client4.start();


            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

package game;

import game.roles.Big2Player;
import game.roles.HumanPlayer;

import java.io.*;
import java.net.*;
import java.util.*;

/*
*
*  Construct a GameDesk for client, manages the connections and message exchanges.
*
* */

public class Big2Server extends Thread{

    String owner = "NoName";
    public static int tcpPort = 11223;
    private ServerSocket tcpSocket;
    private List<Socket> clientSockets;


    public static int udpPort = 11224;
    private DatagramSocket udpSocket;
    DatagramPacket udpPacket;
    byte[] buffer = new byte[BUFSIZE];

    Big2PokerGame big2PokerGame;
    Big2Player[] players = new Big2Player[4];

    HashMap<String,Socket> playerNameMap = new HashMap<>();

    Collection<PrintStream> toAll = new ArrayList<>();

    public static final int BUFSIZE = 4096;

    public Big2Server(){}
    public Big2Server(String owner){
        System.out.println("setting owner to: "+owner);
        this.owner = owner;}



    @Override
    public void run(){
        buildService();
        processConnection();
        gamePlaying();
        finished();
    }
    private void buildService(){
        setClientSockets(new ArrayList<>());
        try {
            setTcpSocket(new ServerSocket(tcpPort));
            setUdpSocket(new DatagramSocket(udpPort));
            UdpEchoThread udpEchoThread = new UdpEchoThread();
            Thread thread = new Thread(udpEchoThread);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Service binding port: tcp(" +tcpPort+")/udp(" +udpPort + ") ready.");
    }
    private void processConnection(){


        StringBuffer stringBuffer = new StringBuffer();

        for(int i = 0;i<4;i++){
            Socket socket = null;
            try {
                System.out.println("Server >>> waiting for connection....");
                socket = getTcpSocket().accept();
                getClientSockets().add(socket);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream());

                writer.println(Big2PokerGame.ENTER_NICKNAME);
                writer.flush();

                String name = reader.readLine().trim();

                System.out.println("SERVER >>> "+"("+i+")"+name+" is connected.");

                playerNameMap.put(name,socket);

//                players[i] = new HumanPlayer("("+i+")"+name);
                players[i] = new HumanPlayer(name);
                players[i].setSocketClient(socket);

                stringBuffer.append(name+",");

                toAll.add(writer);

                tellApiece(Big2PokerGame.PLAYER_JOIN+"="+stringBuffer.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void gamePlaying(){
        big2PokerGame = new Big2PokerGame(players);
    }
    private void finished() {
        for(Socket curSocket: getClientSockets()) {
            try {
                curSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server執行完畢。");
    }

    public void echoClientGameDeskInfo() {
        // Create a buffer large enough for incoming packets

        try {
//            socket.setSoTimeout(3 * 1000);
            while(!getTcpSocket().isClosed()) {

                InetAddress serverIP = InetAddress.getLocalHost();
                String ipStr = serverIP.toString().split("/")[1];

                // Create a DatagramPacket for reading UDP packets
                udpPacket = new DatagramPacket(buffer, BUFSIZE);

                // Receive incoming packets
                getUdpSocket().receive(udpPacket);

                System.out.println("Packet received from " + udpPacket.getAddress() +
                        ":" + udpPacket.getPort() + " of length " + udpPacket.getLength());

                // added for ensuring data is received
                System.out.println("ClientPacket Data = ");
                System.out.write(udpPacket.getData());
                System.out.println();

                String replayGameDeskInfo = new String(owner+","+ getClientSockets().size()+","+ipStr+","+tcpPort);
                System.out.println("Reply: "+replayGameDeskInfo);
                byte[] returnData = replayGameDeskInfo.getBytes();

                DatagramPacket replayDataPacket = new DatagramPacket(returnData, returnData.length,udpPacket.getAddress(),udpPacket.getPort());
//                udpPacket.setData(returnData);
                getUdpSocket().send(replayDataPacket);
            }
        } catch (Exception ioe) {
            System.err.println("Error : " + ioe);
        }
    }

    public ServerSocket getTcpSocket() {
        return tcpSocket;
    }

    public void setTcpSocket(ServerSocket tcpSocket) {
        this.tcpSocket = tcpSocket;
    }

    public List<Socket> getClientSockets() {
        return clientSockets;
    }

    public void setClientSockets(List<Socket> clientSockets) {
        this.clientSockets = clientSockets;
    }

    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public void setUdpSocket(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    private class UdpEchoThread implements Runnable{

        @Override
        public void run() {
            echoClientGameDeskInfo();
        }
    }

    public void setOwner(String owner){
        this.owner = owner;
    }
    public String getOwner(){
        return this.owner;
    }

    public static void main(String[] args) {
        Big2Server sds = new Big2Server("Dinson");
        sds.start();
    }
    public void tellApiece(String message){
        //產生iterator可以存取集合內的元素資料
        Iterator it = toAll.iterator();
        //向下讀取元件
        while(it.hasNext()){
            try{
                //取集合內資料
                PrintStream writer = (PrintStream) it.next();
                //印出
                writer.println(message);
                //刷新該串流的緩衝。
                writer.flush();
            }
            catch(Exception ex){
                System.out.println("連接失敗Process");
            }
        }
    }
}

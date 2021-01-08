package view;

import game.Big2Client;
import game.Big2Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class Big2GameView extends JFrame implements ActionListener {

//    public static JLayeredPane layeredPane = null;

    private String userName = "";

    Collection<String[]> scanLanResult;

    private Big2PaintPanel paintPanel;

    public static int frameWidth = 1440;
    public static int frameHeight = 960;

    private String iconFile = "FrameIcon.png";

    private JMenuBar jMenuBar;
    private JMenu gameMenu, viewMenu, helpMenu;
    private JMenuItem newGameDesk, quickStart, searchGameDesk, leaveAGame, exit;
    private JMenuItem scoreBox, gameHistory;
    private JMenuItem howToPlay, aboutAuthor;

    Big2Server sds;
    Big2Client b2c;

    //------------------------Search related components----------------------------
    String searchDialogCaption = "Scanning Lan for Game exist ...";
    JDialog searchDialog = new JDialog();

    JLabel jProgressBarStatus = new JLabel("Loading....");
    JProgressBar searchProgressBar = new JProgressBar();

    JComboBox searchResultComboBox = new JComboBox();
    JButton doJoinButton = new JButton("Join");

    //----------------------------------------------------------------------------

    public Big2GameView() {

        userName = (String) JOptionPane.showInputDialog(null, "Please enter your name",
                "Hey bro, how do i call you?", JOptionPane.QUESTION_MESSAGE, null, null, userName);

        this.basicFrameSetting();

        this.setVisible(true);

        b2c = new Big2Client(userName);

    }
    public Big2GameView(String name){

        this.userName = name;

        this.basicFrameSetting();

        this.setVisible(true);

        b2c = new Big2Client(userName);
    }
    public Big2GameView(String name, String option){

        this.userName = name;

        this.basicFrameSetting();

        this.setVisible(true);

        b2c = new Big2Client(userName);

        switch (option){
            case "Q":
                quickStart();
                break;
            case "L":
            case "N":
            case "S":
        }


    }


    public void basicFrameSetting() {

        this.setSize(frameWidth, frameHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle(userName+", Welcome!");
        this.setIconImage(new ImageIcon(iconFile).getImage());
        this.getContentPane().setBackground(Color.BLACK);
        this.setLayout(null);

        this.setupFrameAlignmentCenter(this);

        this.setupFrameParts();
//        this.setupPanel();

    }

    private void setupFrameAlignmentCenter(Container window) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
        window.setLocation(x, y);
    }

    private void setupFrameParts() {
        jMenuBar = new JMenuBar();

        gameMenu = new JMenu("Game");
        viewMenu = new JMenu("View");
        helpMenu = new JMenu("Help");

        gameMenu.setMnemonic(KeyEvent.VK_G);
        viewMenu.setMnemonic(KeyEvent.VK_V);
        helpMenu.setMnemonic(KeyEvent.VK_H);

        jMenuBar.add(gameMenu);
        jMenuBar.add(viewMenu);
        jMenuBar.add(helpMenu);

        newGameDesk = new JMenuItem("Create New Game");
        newGameDesk.setMnemonic(KeyEvent.VK_N);
        newGameDesk.addActionListener(this);

        quickStart = new JMenuItem("Quick Start");
        quickStart.setMnemonic(KeyEvent.VK_Q);
        quickStart.addActionListener(this);

        searchGameDesk = new JMenuItem("Search GameDesk");
        searchGameDesk.setMnemonic(KeyEvent.VK_S);
        searchGameDesk.addActionListener(this);

        leaveAGame = new JMenuItem("Leave game");
        leaveAGame.setMnemonic(KeyEvent.VK_L);
        leaveAGame.addActionListener(this);
        leaveAGame.setEnabled(false);

        exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.addActionListener(this);

        gameMenu.add(newGameDesk);
        gameMenu.add(quickStart);
        gameMenu.add(searchGameDesk);
        gameMenu.add(leaveAGame);
        gameMenu.add(exit);

        scoreBox = new JMenuItem("Check Score");
        scoreBox.setMnemonic(KeyEvent.VK_C);
        gameHistory = new JMenuItem("Check game History!");
        gameHistory.setMnemonic(KeyEvent.VK_H);

        viewMenu.add(scoreBox);
        viewMenu.add(gameHistory);

        this.setJMenuBar(jMenuBar);
    }

    private void setupPanel() {

        paintPanel = new Big2PaintPanel(userName);


        b2c.addPropertyChangeListener(paintPanel);
        this.add(paintPanel);
    }

    private void delayMS(int milliSecond) {

        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String getUserName(){return userName;}
    public void setUserName(String nickname){ userName=nickname;}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameDesk)
            createNewGame();

        if(e.getSource() == quickStart){
            quickStart();
        }

        if(e.getSource() == searchGameDesk){

            buildSearchGUI();

            SearchLanGameDesk searchLanGameDesk = new SearchLanGameDesk();
            searchLanGameDesk.execute();

        }

        if(e.getSource() == leaveAGame){
            this.newGameDesk.setEnabled(true);
            this.searchGameDesk.setEnabled(true);
            this.quickStart.setEnabled(true);
            this.leaveAGame.setEnabled(false);

            closeSocketAndResetFrame();
        }

        if (e.getSource() == exit) {
            System.exit(0);
        }
    }

    private void createNewGame() {
        bindingService();
        updateMenuItemStateWhenJoinAGame();

        b2c.connectToGameDesk("127.0.0.1", Big2Server.tcpPort);
        b2c.start();

        setupPanel();
        this.repaint();
    }
    private void quickStart(){

        this.newGameDesk.setEnabled(false);
        this.searchGameDesk.setEnabled(false);
        this.quickStart.setEnabled(false);
        this.leaveAGame.setEnabled(true);

        QuickStart qs = new QuickStart();
        qs.execute();
    }

    private void bindingService(){
        sds = new Big2Server(userName);
        sds.setOwner(userName);
        sds.start();
    }

    private void buildSearchGUI() {
        searchDialog.setSize(new Dimension(300,100));
        searchDialog.setAlwaysOnTop(true);
        searchDialog.setTitle(searchDialogCaption);

        setupFrameAlignmentCenter(searchDialog);

        jProgressBarStatus.setFont(new Font("Ink Free",Font.BOLD,25));
        jProgressBarStatus.setBounds(20,20,150,40);

        searchProgressBar.setIndeterminate(true);
        searchProgressBar.setBounds(20,40,150,40);

        doJoinButton.setBounds(175, 150, 50, 40);
        doJoinButton.setEnabled(false);

        searchDialog.add(searchProgressBar,BorderLayout.CENTER);
        searchDialog.add(jProgressBarStatus,BorderLayout.NORTH);
        searchDialog.add(doJoinButton,BorderLayout.EAST);

        searchDialog.setVisible(true);
    }

    private void closeSocketAndResetFrame() {
        try {
            if (b2c.isConnected()) b2c.closeSocketClient();
            for (Socket curSocket : sds.getClientSockets())
                if (curSocket.isConnected()) curSocket.close();
            if (!sds.getTcpSocket().isClosed()) sds.getTcpSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resetPanel();
        repaint();
    }

    private void resetPanel() {
        this.remove(paintPanel);
        repaint();
    }

    class SearchLanGameDesk extends SwingWorker<Collection<String[]>,Void>{
        @Override
        protected Collection<String[]> doInBackground() throws Exception {
            buildSearchGUI();
            scanLanResult = b2c.scanLAN4GameDesks();
            return scanLanResult;
        }
        public void done(){

            doJoinButton.setEnabled(true);
            searchProgressBar.setVisible(false);
            jProgressBarStatus.setText("Result:");

            searchDialog.remove(searchProgressBar);

            searchResultComboBox = new JComboBox();
            searchResultComboBox.setBounds(20,150,150,35);

            for(String[] result: scanLanResult){
                String itemString = result[0]+", "+result[1]+" player joined.";
                searchResultComboBox.addItem(itemString);
            }

            searchDialog.add(searchResultComboBox,BorderLayout.CENTER);
            searchDialog.add(doJoinButton,BorderLayout.EAST);

            doJoinButton.addActionListener( e -> {
                    String selectedGameOwner = ((String) searchResultComboBox.getSelectedItem()).split(",")[0];
                    for(String[] curStrArray : scanLanResult)
                        if(curStrArray[0].equals(selectedGameOwner)) {
                            String targetIP = curStrArray[2];
//                            int targetPort = Integer.parseInt(curStrArray[3]);
                            int targetPort = Big2Server.tcpPort;

                            b2c.connectToGameDesk(targetIP, targetPort);
                            setupPanel();
                            b2c.start();
                            searchDialog.setVisible(false);
                            repaint();
                            return;
                        }
                    }
            );
        }
    }
    class QuickStart extends SwingWorker<Collection<String[]>,Void>{
        @Override
        protected Collection<String[]> doInBackground() throws Exception {
            buildSearchGUI();
            scanLanResult = b2c.scanLAN4GameDesks();
            return scanLanResult;
        }
        public void done(){

            searchDialog.setVisible(false);

            searchDialog.remove(doJoinButton);
            searchDialog.remove(jProgressBarStatus);
            searchDialog.remove(searchProgressBar);

            if(scanLanResult.size()>0){
                for(String[] curResult:scanLanResult){
                    String targetIP = curResult[2];
//                    int targetPort = Integer.parseInt(curResult[3]);
                    int targetPort = Integer.parseInt("11223");
                    b2c.connectToGameDesk(targetIP,targetPort);
                    setupPanel();
                    b2c.start();
                    searchDialog.setVisible(false);
                    repaint();
                    return;
                }
            }
            else{
                createNewGame();
            }

        }
    }


    private void updateMenuItemStateWhenJoinAGame(){
        this.newGameDesk.setEnabled(false);
        this.searchGameDesk.setEnabled(false);
        this.quickStart.setEnabled(false);
        this.leaveAGame.setEnabled(true);
    }

    public static void main(String[] args) {
//        System.out.println(Thread.currentThread().getName());
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Big2GameView("YD000");
            }

        });
    }

}

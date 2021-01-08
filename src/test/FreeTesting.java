package test;

import javax.swing.*;
import java.awt.*;

public class FreeTesting {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame jFrame = new JFrame();
                jFrame.setBounds(200, 200, 500, 500);
//                jFrame.setLayout(null);
                jFrame.setVisible(true);

                JPanel jPanel = new JPanel();
                jPanel.setSize(500, 500);
//                jPanel.setLayout(null);

                JButton jButton11 = new JButton("TEST1");
                jButton11.setBounds(123, 123, 50, 25);

                jFrame.add(jPanel,BorderLayout.CENTER);

                JLayeredPane jLayeredPane = new JLayeredPane();


                jPanel.add(jLayeredPane,BorderLayout.CENTER);

                jLayeredPane.add(jButton11,Integer.valueOf(1));
//                jLayeredPane.remove(jButton11);
                jLayeredPane.add(jButton11,Integer.valueOf(2));

            }
        });
    }
}

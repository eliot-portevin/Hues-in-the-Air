package ch.unibas.dmi.dbis.cs108.example.gui;

import javax.swing.*;

public class GuiFirstAttempts {

    public void createFrame() {
        JFrame frame = new JFrame("Hues in the Air");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JButton  startButton = new JButton("Start");
        JPanel newPanel= new JPanel();
        newPanel.setSize(700, 500);

    }

}

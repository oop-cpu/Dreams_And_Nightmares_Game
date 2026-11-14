import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.net.URL;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class run{

    // Frames
    public static JFrame game = new JFrame();

    // Starting vars
    //public static player user = new player();

    // Images (example var 'image')
    public static URL playerImage = run.class.getResource("playerImage.png");

    // Colors
    public static Color backgroundColor = new Color(18, 18, 28);

    public static void main(String[] args){

        // Image Loading (example var 'image' and 'imageLabel')
        JLabel playerLabel;
        if (playerImage != null) {
            ImageIcon icon = new ImageIcon(playerImage);
            playerLabel = new JLabel(icon);
            playerLabel.setBounds(100, 100, 2000, 2000);
        } else {
            System.out.println("playerImage not found!");
            playerLabel = new JLabel("playerImage");
        }

        // Frame Setup
        game.setExtendedState(JFrame.MAXIMIZED_BOTH);
        game.getContentPane().setBackground(backgroundColor);
        game.setLayout(null);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game.add(playerLabel);

        //Frame init
        game.setVisible(true);

    }

    
    
}

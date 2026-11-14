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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class run{

    // Temp Vars
    public static int posX = 10;
    public static int posY = 10;
    public static int speed = 10;

    // Frames
    public static JFrame game = new JFrame();

    // Starting vars
    //public static player user = new player();

    // Images (example var 'image')
    public static URL playerImage = run.class.getResource("playerImage.png");

    // Elements
    public static JLabel playerLabel;

    // Colors
    public static Color backgroundColor = new Color(18, 18, 28);

    public static void main(String[] args){

        // Image Loading (example var 'image' and 'imageLabel')
        if (playerImage != null) {
            ImageIcon icon = new ImageIcon(playerImage);
            playerLabel = new JLabel(icon);
            playerLabel.setBounds(10, 10, 2000, 2000);
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

        // Key Listener
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            JPanel panel = new JPanel();

            // Bind keys to the panel even when child components have focus
            InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = panel.getActionMap();

            // Register Spacebar
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spaceAction");
            actionMap.put("spaceAction", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    posChange('r');
                }
            });

            // Register w
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "wAction");
            actionMap.put("wAction", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    posChange('u');
                }
            });

            // Register a
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "aAction");
            actionMap.put("aAction", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    posChange('l');
                }
            });

            // Register d
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "dAction");
            actionMap.put("dAction", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    posChange('r');
                }
            });

            // Register s
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "sAction");
            actionMap.put("sAction", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    posChange('d');
                }
            });

            frame.add(panel);
            frame.setSize(0, 0);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    public static void posChange(char dir){
        switch(dir){
            case 'l':
                posX -= speed;
                break;
            case 'r':
                posX += speed;
                break;
            case 'u':
                posY -= speed;
                break;
            case 'd':
                posY += speed;
                break;
            default:
                System.out.println("dir error");
                return;
        }

        playerLabel.setBounds(posX, posY, 2000, 2000);

    }

    
    
}

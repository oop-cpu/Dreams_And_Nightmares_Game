import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.net.URL;

import javax.sound.sampled.*;

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

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class run{

    // CHANGED: removed Temp Vars (posX, posY, speed)
    // Position and speed now live in player.java (user.getX(), user.getY(), speed)

    // Frames
    public static JFrame game = new JFrame();

    // Starting vars
    // CHANGED: uncommented and gave a start position so the game uses player.java
    public static player user = new player(10, 10);
    public static Rectangle screen;

    // Images (example var 'image')
    public static URL playerImage = run.class.getResource("playerImage.png");

    // Elements
    public static JLabel playerLabel;

    // Colors
    public static Color backgroundColor = new Color(18, 18, 28);

    public static node[] map;   // all the rooms (static so the key bindings can reach it)

    public static void main(String[] args){

        // Image Loading (example var 'image' and 'imageLabel')
        if (playerImage != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(playerImage).getImage().getScaledInstance(80, -1, Image.SCALE_SMOOTH));
            playerLabel = new JLabel(icon);
            playerLabel.setBounds(10, 10, 10, 10);
        } else {
            System.out.println("playerImage not found!");
            playerLabel = new JLabel("playerImage");
        }

        
        screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int imgW = playerLabel.getPreferredSize().width;
        int imgH = playerLabel.getPreferredSize().height;
        user.setPosition(screen.width / 2 - imgW / 2, screen.height / 2 - imgH / 2);
        playerLabel.setBounds(user.getX(), user.getY(), imgW, imgH);

        // Frame Setup
        game.setExtendedState(JFrame.MAXIMIZED_BOTH);
        game.getContentPane().setBackground(backgroundColor);
        game.setLayout(null);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game.add(playerLabel);

        setTile(user.getNode());

        //Frame init
        game.setVisible(true);

        // Key Listener
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            JPanel panel = new JPanel();

            // Bind keys to the panel even when child components have focus
            InputMap inputMap = game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); // intput map go to the rootpane
            ActionMap actionMap = game.getRootPane().getActionMap(); // action map go to the rootpane

            // CHANGED: each WASD key now has a PRESSED and a RELEASED binding
            // Pressed sets user.up/down/left/right = true, released sets it back to false
            // Holding two keys at once (like W + D) moves diagonally = 8 directions
            // REMOVED: Space binding (it only moved right, same as D). Can be used for attacks

            // Register space = light attack on enemies in this room
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "attack");
            actionMap.put("attack", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    combat.playerAttack(user, map[user.getNode()].getEnemies());
                }
            });

            // Register w (pressed = start moving, released = stop)
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "wPressed");
            actionMap.put("wPressed", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.up = true;
                    user.lastV = 'u';
                }
            });
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "wReleased");
            actionMap.put("wReleased", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.up = false;
                }
            });

            // Register a (pressed = start moving, released = stop)
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "aPressed");
            actionMap.put("aPressed", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.left = true;
                    user.lastH = 'l';
                }
            });
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "aReleased");
            actionMap.put("aReleased", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.left = false;
                }
            });

            // Register s (pressed = start moving, released = stop)
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "sPressed");
            actionMap.put("sPressed", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.down = true;
                    user.lastV = 'd';
                }
            });
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "sReleased");
            actionMap.put("sReleased", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.down = false;
                }
            });

            // Register d (pressed = start moving, released = stop)
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "dPressed");
            actionMap.put("dPressed", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.right = true;
                    user.lastH = 'r';
                }
            });
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "dReleased");
            actionMap.put("dReleased", new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e){
                    user.right = false;
                }
            });

            frame.add(panel);
            frame.setSize(0, 0);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(false); //set to false to not see the action map pane
        });

        // ADDED: game loop (~60 times a second). Replaces posChange():
        // player.update() moves the player using the held keys, then the image follows
        // Uses javax.swing.Timer by full name because java.util.* also has a Timer class
        // Game loop: about 60 times a second, move the player and the image
        
        map = new node[10];
        Random rng = new Random();
        Rectangle area = new Rectangle(100, 100, screen.width - 200, screen.height - 200);
        for (int i = 0; i < map.length; i++) {
            map[i] = new node();
            map[i].init(i, 5, area, rng);
        }
        map[user.getNode()].addAndShow(game); // show the enemies of the room the player starts in

        new javax.swing.Timer(16, e -> {
            map[user.getNode()].update(user.getX(), user.getY()); //update node
            combat.enemyContact(user, map[user.getNode()].getEnemies()); // enemies touching you hurt
            user.update();
            if(user.getX() > screen.width){
                if(user.getNode() == 9){
                    user.setPosition(user.getX()-1, user.getY());
                }
                else{
                    user.setNode(user.getNode() + 1);
                    user.setPosition(1, user.getY());
                }
            }
            else if(user.getX() < 0){
                if(user.getNode() == 0){
                    user.setPosition(user.getX()+1, user.getY());
                }
                else{
                    user.setNode(user.getNode() - 1);
                    user.setPosition(screen.width-1, user.getY());
                }
            }
            if(user.getY() < 0){
                user.setPosition(user.getX(), 1);
            }
            else if(user.getY() > screen.height-10){
                user.setPosition(user.getX(), screen.height-10);
            }
            playerLabel.setBounds(user.getX(), user.getY(), imgW, imgH);
        }).start();

        // REMOVED: posChange(char dir). Movement is handled by player.update() in the game loop
    }



    public static void setTile(int id){
        // Background floor from the Puny Dungeon tileset (added AFTER the player so it's drawn behind it)
        try {
            BufferedImage tiles = ImageIO.read(run.class.getResource("punyworld-dungeon-tileset.png"));
            int[][] floorSpots = { {64, 0}, {0, 80}, {16, 80} };      // x, y of floor blocks in the tileset
            int[] spot = floorSpots[id % floorSpots.length];          // % keeps it inside the list
            BufferedImage floorTile = tiles.getSubimage(spot[0], spot[1], 64, 64);   // 4x4 block of floor tiles
            int tileScale = Math.max(1, screen.width / 480); 
            int t = 64 * tileScale;

            BufferedImage floor = new BufferedImage(screen.width, screen.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D fg = floor.createGraphics();
            for (int y = 0; y < screen.height; y += t) {
                for (int x = 0; x < screen.width; x += t) {
                    fg.drawImage(floorTile, x, y, t, t, null);
                }
            }
            fg.setColor(new Color(10, 5, 30, 140));   // dark, dreamy tint over the floor
            fg.fillRect(0, 0, screen.width, screen.height);
            fg.dispose();

            JLabel bgLabel = new JLabel(new ImageIcon(floor));
            bgLabel.setBounds(0, 0, screen.width, screen.height);
            game.add(bgLabel);
        } catch (Exception ex) {
            System.out.println("Could not load floor tileset: " + ex);
        }
    }

    
}
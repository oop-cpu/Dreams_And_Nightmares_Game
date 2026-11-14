import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.net.URL;

public class enemies {
    // ==== ENEMY TYPES ====
    public enum Type { WISP, SHADE, BRUTE}

    // position and size
    private double x, y;
    private int width, height;

    // stats
    private Type type;
    private double speed;
    private int maxHealth;
    private int health;
    private int damage;

    // contact damage cooldown: only hurt player once per second
    private long hitCoolDown = 1000;
    private long lastTimeHit = 0;

    // Picture on screen
    private JLabel label;

    public enemies(Type type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;

        switch (type) {
            case WISP: // small ones fast and weak
            width = 32; height = 32; speed = 2.5; maxHealth = 10; damage = 5;
            break;
            case SHADE: // Normal ones
            width = 48; height = 48; speed = 1.6; maxHealth = 25; damage = 10;
            break;
            case BRUTE: // small ones
            width = 72; height = 72; speed = 0.9; maxHealth = 60; damage = 20;
            break;
        }
        health = maxHealth;

        URL enemyImage = enemies.class.getResource(type.name().toLowerCase() + ".png");
        if (enemyImage != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(enemyImage).getImage().getScaledInstance(height, width, Image.SCALE_REPLICATE));
            label = new JLabel(icon);
        } else {
            System.out.println(" Image not found, placeholder");
          //  label = new JLabel (new ImageIcon(makePlaceholderImage()));
        }
        // syncLabel();
    }

    // ==== movement ====
    private double hoverRange = 120;
    
    // ==== move toward player ====
    public void increment(int userX, int userY) {
        double dx = userX - this.x;
        double dy = userY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 1) return;

        // direction to the player
        double dirX = dx / dist;
        double dirY = dy / dist;

        if (dist > hoverRange) {
            this.x += dirX * speed;
            this.y += dirY * speed;
        }
        // syncLabel();
    }

    // Contact damage
    // public void tryHit(player target) {
    //     health = Math.max(0, health - amount);
    //     if (!isAlive()) 
    // }
}


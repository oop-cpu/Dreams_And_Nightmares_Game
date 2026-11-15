import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.net.URL;

public class enemies {
    // ==== ENEMY TYPES ====
    public enum Type { WISP, SHADE, BRUTE }

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
            case WISP: // small ones, fast and weak
            width = 32; height = 32; speed = 2.5; maxHealth = 10; damage = 5;
            break;
            case SHADE: // normal ones
            width = 48; height = 48; speed = 1.6; maxHealth = 25; damage = 10;
            break;
            case BRUTE: // big ones, slow and tanky
            width = 72; height = 72; speed = 0.9; maxHealth = 60; damage = 20;
            break;
        }
        health = maxHealth;

        URL enemyImage = enemies.class.getResource(type.name().toLowerCase() + ".png");
        if (enemyImage != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(enemyImage).getImage().getScaledInstance(width, height, Image.SCALE_REPLICATE));
            label = new JLabel(icon);
        } else {
            System.out.println(" Image not found, placeholder");
            label = new JLabel(new ImageIcon(makePlaceholderImage()));
        }
        syncLabel();
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
            // far away: move toward the player
            this.x += dirX * speed;
            this.y += dirY * speed;
        } else {
            // close: circle around the player
            this.x += -dirY * speed;
            this.y +=  dirX * speed;
        }
        syncLabel();
    }

    // contact damage
    public void tryHit(player target) {
        long now = System.currentTimeMillis();
        if (getBounds().intersects(target.getBounds()) && now - lastTimeHit >= hitCoolDown) {
            target.takeDamage(damage);
            lastTimeHit = now;
        }
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
        if (!isAlive()) label.setVisible(false);
    }

    public boolean isAlive() {return health > 0;}

    // picture
    private void syncLabel() {
        label.setBounds((int) x, (int) y, width, height);
    }

    // placeholder picture until the pixel art is ready: dark blob with red eyes
    private Image makePlaceholderImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(type == Type.BRUTE ? new Color(60, 10, 30) : new Color(40, 10, 60));
        g.fillOval(0, 0, width - 1, height - 1);
        g.setColor(new Color(110, 40, 150));
        g.drawOval(0, 0, width - 1, height - 1);
        g.setColor(new Color(255, 60, 80));
        int eye = Math.max(4, width / 7);
        g.fillOval(width / 4, height / 3, eye, eye);
        g.fillOval(width * 3 / 4 - eye, height / 3, eye, eye);
        g.dispose();
        return img;
    }

    // ==== GETTERS ====
    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, width, height); }
    public JLabel getLabel() { return label; }
    public Type getType() { return type; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
}
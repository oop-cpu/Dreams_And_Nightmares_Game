/**
 * player.java - All player attricbutes will be here.
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class player {

    // Position & size
    private int x, y;
    private int width = 32, height = 32;

    // Movement
    private int baseSpeed = 5;
    private int speed = baseSpeed;
    public boolean up, down, left, right;

    // Stats
    private int maxHealth = 100;
    private int health = maxHealth;
    private int baseAttack = 5;
    private int bonusDamage = 0;   // from power-ups
    private int shield = 0;        // absorbs damage first

    // Items
    private List<items.Item> inventory = new ArrayList<>();
    private int maxInventory = 10;
    private items.Weapon equippedWeapon = items.fists();
    private List<items.PowerUp> activePowerUps = new ArrayList<>();

    public player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g) {
        // Player body
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);

        // Health bar above player
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, width, 5);
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, (int) (width * (health / (double) maxHealth)), 5);

        // Shield bar
        if (shield > 0) {
            g.setColor(Color.CYAN);
            g.fillRect(x, y - 16, Math.min(width, shield), 4);
        }
    }

    // ===== POSITION =====
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // ===== MOVEMENT =====
    // run.java sets up/down/left/right while WASD is held and calls update() every frame.
    // Holding two keys (like W + D) moves diagonally, so all 8 directions work.
    public void update() {
        int dx = 0, dy = 0;
        if (up)    dy -= 1;
        if (down)  dy += 1;
        if (left)  dx -= 1;
        if (right) dx += 1;

        // Diagonals: scale down so moving diagonally isn't faster than straight
        double step = (dx != 0 && dy != 0) ? speed * 0.7071 : speed;
        x += (int) Math.round(dx * step);
        y += (int) Math.round(dy * step);

        checkPowerUps();
    }

    // Remove expired power-ups and undo their effect
    private void checkPowerUps() {
        Iterator<items.PowerUp> it = activePowerUps.iterator();
        while (it.hasNext()) {
            items.PowerUp p = it.next();
            if (p.isExpired()) {
                removeEffect(p);
                it.remove();
            }
        }
    }

    // ===== HEALTH =====
    public void takeDamage(int amount) {
        if (shield > 0) {
            int blocked = Math.min(shield, amount);
            shield -= blocked;
            amount -= blocked;
        }
        health = Math.max(0, health - amount);
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public boolean isAlive() { return health > 0; }

    // ===== ATTACK =====
    public int getAttackDamage() {
        return baseAttack + equippedWeapon.getDamage() + bonusDamage;
    }

    /** Returns damage dealt, or 0 if the weapon is still on cooldown. */
    public int attack() {
        if (!equippedWeapon.canAttack()) return 0;
        equippedWeapon.markAttack();
        return getAttackDamage();
    }

    // ===== INVENTORY =====
    public boolean pickUp(items.Item item) {
        if (inventory.size() >= maxInventory) return false;
        inventory.add(item);
        return true;
    }

    /** Use item in a slot. Power-ups get used up; weapons get equipped. */
    public void useItem(int slot) {
        if (slot < 0 || slot >= inventory.size()) return;
        items.Item item = inventory.get(slot);
        item.use(this);
        if (item instanceof items.PowerUp) {
            inventory.remove(slot);
        }
    }

    public void equipWeapon(items.Weapon weapon) {
        equippedWeapon = weapon;
    }

    // ===== POWER-UPS =====
    public void applyPowerUp(items.PowerUp p) {
        switch (p.getType()) {
            case HEAL:   heal(p.getAmount()); break;
            case SPEED:  speed += p.getAmount(); break;
            case DAMAGE: bonusDamage += p.getAmount(); break;
            case SHIELD: shield += p.getAmount(); break;
        }
        if (!p.isInstant()) activePowerUps.add(p);
    }

    private void removeEffect(items.PowerUp p) {
        switch (p.getType()) {
            case SPEED:  speed -= p.getAmount(); break;
            case DAMAGE: bonusDamage -= p.getAmount(); break;
            case SHIELD: shield = Math.max(0, shield - p.getAmount()); break;
            default: break;
        }
    }

    // ===== GETTERS =====
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSpeed() { return speed; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getShield() { return shield; }
    public items.Weapon getWeapon() { return equippedWeapon; }
    public List<items.Item> getInventory() { return inventory; }
    public List<items.PowerUp> getActivePowerUps() { return activePowerUps; }
}
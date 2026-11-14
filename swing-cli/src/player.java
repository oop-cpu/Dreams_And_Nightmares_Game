import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {

    // Position and size
    private int x, y;
    private int width = 10, height = 10;

    // Movement
    private int baseSpeed = 5;
    private int speed = baseSpeed;
    public boolean up, down, left, right;

    // Stats
    private int maxHealth = 100;
    private int health = maxHealth;
    private int baseAttack = 5;
    private int bonusDamage = 0; // from Power-Up
    private int sheild = 0;      // absorb/reduce damage

    // Items 
    private list <Items.Item> inventory = new ArrayList<>();
    private int maxInventory = 10;
    private Items.Weapon equippedWeapon = Items.fists();
    private List<Items.PowerUp> activatePowerUps = new ArrayList<> ();
    
    
}
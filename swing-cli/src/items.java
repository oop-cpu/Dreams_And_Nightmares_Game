import java.util.Random;

/**
 * items.java - all weapons and power-ups live here.
 * Use them like:  items.Weapon sword = items.sword();
 * For roguelite drops/rewards use:  items.randomItem(rng);
 */
public class items {

    // ===== BASE ITEM =====
    public static abstract class Item {
        protected String name;
        protected String description;

        public Item(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }

        public abstract void use(player p);

        @Override
        public String toString() { return name; }
    }

    // ===== WEAPON =====
    public static class Weapon extends Item {
        private int damage;
        private int range;
        private long cooldownMs;
        private long lastAttackTime = 0;

        public Weapon(String name, String description, int damage, int range, long cooldownMs) {
            super(name, description);
            this.damage = damage;
            this.range = range;
            this.cooldownMs = cooldownMs;
        }

        public boolean canAttack() {
            return System.currentTimeMillis() - lastAttackTime >= cooldownMs;
        }

        public void markAttack() {
            lastAttackTime = System.currentTimeMillis();
        }

        @Override
        public void use(player p) {
            p.equipWeapon(this);
        }

        public int getDamage() { return damage; }
        public int getRange() { return range; }
        public long getCooldownMs() { return cooldownMs; }
    }

    // ===== POWER-UP =====
    public static class PowerUp extends Item {

        public enum Type { HEAL, SPEED, DAMAGE, SHIELD }

        private Type type;
        private int amount;
        private long durationMs;   // 0 = instant
        private long startTime;

        public PowerUp(String name, String description, Type type, int amount, long durationMs) {
            super(name, description);
            this.type = type;
            this.amount = amount;
            this.durationMs = durationMs;
        }

        @Override
        public void use(player p) {
            startTime = System.currentTimeMillis();
            p.applyPowerUp(this);
        }

        public boolean isExpired() {
            return durationMs > 0 && System.currentTimeMillis() - startTime >= durationMs;
        }

        public boolean isInstant() { return durationMs == 0; }
        public Type getType() { return type; }
        public int getAmount() { return amount; }

        public int secondsLeft() {
            long left = durationMs - (System.currentTimeMillis() - startTime);
            return (int) Math.max(0, left / 1000);
        }
    }

    // ===== READY-MADE ITEMS (add more here) =====

    // Weapons:                            name                description                  dmg range cooldown
    public static Weapon fists()       { return new Weapon("Paws",              "Better than nothing",        0,  30,  400); }
    public static Weapon sword()       { return new Weapon("Moonbeam Claw",     "Solid all-rounder",         15,  50,  500); }
    public static Weapon axe()         { return new Weapon("Starfall Swipe",    "Big hits, slow",            25,  45,  900); }
    public static Weapon bow()         { return new Weapon("Wisp Bolt",         "Long range",                10, 250,  800); }
    public static Weapon lightPulse()  { return new Weapon("Light Pulse",       "Hits everything around you", 8, 120, 1200); }

    // Power-ups:                            name               description                  type                 amount  duration(ms)
    public static PowerUp healthPotion() { return new PowerUp("Dream Shard",    "Restores 30 light",         PowerUp.Type.HEAL,   30, 0); }
    public static PowerUp speedBoost()   { return new PowerUp("Catnip Dash",    "+3 speed for 5s",           PowerUp.Type.SPEED,   3, 5000); }
    public static PowerUp ragePotion()   { return new PowerUp("Starburst",      "+10 damage for 8s",         PowerUp.Type.DAMAGE, 10, 8000); }
    public static PowerUp shield()       { return new PowerUp("Moonlight Ward", "Blocks 25 damage for 10s",  PowerUp.Type.SHIELD, 25, 10000); }

    // ===== RANDOM ITEMS (roguelite drops and rewards) =====

    /** A random weapon (not including the starting Paws). */
    public static Weapon randomWeapon(Random rng) {
        switch (rng.nextInt(4)) {
            case 0:  return sword();
            case 1:  return axe();
            case 2:  return bow();
            default: return lightPulse();
        }
    }

    /** A random power-up. */
    public static PowerUp randomPowerUp(Random rng) {
        switch (rng.nextInt(4)) {
            case 0:  return healthPotion();
            case 1:  return speedBoost();
            case 2:  return ragePotion();
            default: return shield();
        }
    }

    /** A random item: power-ups are more common (75%) than weapons (25%). */
    public static Item randomItem(Random rng) {
        if (rng.nextInt(100) < 75) return randomPowerUp(rng);
        return randomWeapon(rng);
    }

    /** Three different random items to choose from after a wave. */
    public static Item[] rewardChoices(Random rng) {
        Item[] choices = new Item[3];
        for (int i = 0; i < 3; i++) {
            Item pick;
            boolean repeat;
            do {
                pick = randomItem(rng);
                repeat = false;
                for (int j = 0; j < i; j++) {
                    if (choices[j].getName().equals(pick.getName())) repeat = true;
                }
            } while (repeat);
            choices[i] = pick;
        }
        return choices;
    }
}
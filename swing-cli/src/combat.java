import java.awt.Rectangle;

/**
 * combat.java - All attacks and hits in the game happen here.
 * Player -> enemies (light attack) and enemies -> player (contact damage).
 */
public class combat {

    // How far the light attack reaches (enemies circle at 120, so keep this bigger)
    public static final int ATTACK_RANGE = 160;

    // ===== PLAYER ATTACKS =====

    /** Returns the player's damage, or 0 if their weapon is still on cooldown. */
    public static int tryPlayerAttack(player user) {
        items.Weapon weapon = user.getWeapon();
        if (!weapon.canAttack()) return 0;
        weapon.markAttack();
        return user.getAttackDamage();
    }

    /** Player's light attack: damages every living enemy within ATTACK_RANGE. */
    public static void playerAttack(player user, enemies[] list) {
        int dmg = tryPlayerAttack(user);
        if (dmg == 0) return;

        for (enemies e : list) {
            if (e == null || !e.isAlive()) continue;
            if (distance(user.getBounds(), e.getBounds()) <= ATTACK_RANGE) {
                e.takeDamage(dmg);
            }
        }
    }

    // ===== ENEMY ATTACKS =====

    /** Enemies touching the player drain its light (each enemy has its own cooldown). */
    public static void enemyContact(player user, enemies[] list) {
        for (enemies e : list) {
            if (e == null || !e.isAlive()) continue;
            if (e.getBounds().intersects(user.getBounds()) && e.canHit()) {
                user.takeDamage(e.getDamage());
                e.markHit();
            }
        }
    }

    // ===== HELPERS =====

    /** Distance between the centers of two boxes. */
    public static double distance(Rectangle a, Rectangle b) {
        double dx = b.getCenterX() - a.getCenterX();
        double dy = b.getCenterY() - a.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
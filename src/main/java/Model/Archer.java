package Model;


/**
 * Archer class is a template of which attributes an Archer character would have.
 * It also has methods for different behaviour/ what the Archer can do.
 */
public class Archer extends Character {

    private final int killedReward = 15; // When an archer is killed the opponent gets 15 gold.
    private final int cost = 25; // Cost 25 gold (in-game currency).
    private final int damage = 30; // Each hit does 30 damage to opponent character.
    private final int attackRange = 3; // Assumed 50 pixels range


    /**
     * Instantiates the health, position (where on the map the character is) and
     * if it is alive or not.
     * @param healthPoints the character's health.
     * @param position Where it is located.
     * @param isAlive if it is alive or not.
     */
    public Archer(int characterId, int healthPoints, double position, boolean isAlive, double speed, long attackSpeed, float size) {
        super(characterId,healthPoints, position, speed, isAlive, attackSpeed, size);
    }

    /**
     * Check if the character is alive or not.
     *
     * @return
     */
    @Override
    public boolean isAlive(){
        return false;
    }

    /**
     * Sends the reward to the opponent for killing the character.
     * @return returns an int (gold) to the opponent as a reward.
     */
    @Override
    public int getKillReward(){
       return killedReward;
    }

    public int getCost() {
        return cost;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public int getAttackRange() {
        return attackRange;
    }
}

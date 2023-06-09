package Model;


/**
 * Rider class is a template of which attributes a Rider character would have.
 * It also has methods for different behaviour/ what the Rider can do.
 */
public class Rider extends Character {

    private final int killedReward = 65; // When an archer is killed the opponent gets 65 gold.
    private final int cost = 75; // Cost 75 gold (in-game currency).
    private final int damage = 50; // Each hit does 50 damage to opponent character.
    private final int attackRange = 0; // Assumed 20 pixels range


    /**
     * Instantiates the health, position (where on the map the character is) and
     * if it is alive or not.
     * @param healthPoints the character's health.
     * @param position Where it is located.
     * @param isAlive if it is alive or not.
     */
    public Rider(int characterId, int healthPoints, double position, boolean isAlive, double speed, long attackSpeed, float size) {
        super(characterId,healthPoints, position, speed, isAlive, attackSpeed, size);
    }

    /**
     * Check if the character is alive or not.
     *
     * @return
     */
    @Override
    public boolean isAlive(){
        return this.getHealthPoints() > 0;
    }

    /**
     * Sends the reward to the opponent for killing the character.
     * @return returns an int (gold) to the opponent as a reward.
     */
    @Override
    public int getKillReward(){
        // Need to add the reward to opponents gold. This method might not need changing if dealt with in gameManager.
        return killedReward;
    }

    public int getCost() {
        return cost;
    }
    public int getDamage(){
        return damage;
    }

    @Override
    public int getAttackRange() {
        return attackRange;
    }
}

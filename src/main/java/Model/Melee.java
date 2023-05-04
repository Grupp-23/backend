package Model;


/**
 * Melee class is a template of which attributes a Melee character would have.
 * It also has methods for different behaviour/ what the Melee character can do.
 */
public class Melee extends Character {

    private final int killedReward = 5; // When an archer is killed the opponent gets 5 gold.
    private final int cost = 15; // Cost 15 gold (in-game currency).
    private final int damage = 20; // Each hit does 20 damage to opponent character.
    private final int attackRange = 10; // Assumed 10 pixels range

    /**
     * Instantiates the health, position (where on the map the character is) and
     * if it is alive or not.
     * @param healthPoints the character's health.
     * @param position Where it is located.
     * @param isAlive if it is alive or not.
     */
    public Melee(int healthPoints, int position, boolean isAlive) {
        super(healthPoints, position, isAlive);
    }

    /**
     * Attack method for attacking an opponent.
     */
    @Override
    public void attack(){

    }

    /**
     * Method for updating the character's health,
     * how much damage is based on the opponent.
     */
    @Override
    public void takeDamage(){

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
}

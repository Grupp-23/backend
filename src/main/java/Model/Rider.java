package Model;


/**
 * Rider class is a template of which attributes a Rider character would have.
 * It also has methods for different behaviour/ what the Rider can do.
 */
public class Rider extends Character {

    private final int killedReward = 65; // When an archer is killed the opponent gets 65 gold.
    private final int cost = 75; // Cost 75 gold (in-game currency).
    private final int damage = 50; // Each hit does 50 damage to opponent character.
    private final int attackRange = 20; // Assumed 20 pixels range

    /**
     * Instantiates the health, position (where on the map the character is) and
     * if it is alive or not.
     * @param healthPoints the character's health.
     * @param position Where it is located.
     * @param isAlive if it is alive or not.
     */
    public Rider(int healthPoints, int position, boolean isAlive) {
        super(healthPoints, position, isAlive);
    }

    /**
     * Attack method for attacking an opponent.
     */
    @Override
    public void attack(){

        int characterPos = getPosition();
        //int enemyCharacterPos = gameManager.enemyPosition; // Get enemyPos from gameManager or Controller.

        //int distance = Math.abs(characterPos - enemyCharacterPos);

        //while(distance <= attackRange){
            //controller.attackedOpponent(damage);
        //}
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
     */
    @Override
    public void isAlive(){
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

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
        /*
        While game is active, the method needs to be run the whole match so that the enemyPOS is updated frequently.
        But also when a character is alive (Nested while loops might not be good):

            while(enemy is in range){
                attack
            }

         */

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
        /*
        Probably needs an in parameter so that it knows how much it has taken damage.
        Then just update the character's health.
         */

        // this.getHealthPoints() -= incomingDamage();
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
}

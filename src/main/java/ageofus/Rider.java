package ageofus;

public class Rider extends Character {

    private final int killedReward = 65; // When an archer is killed the opponent gets 65 gold.
    private final int cost = 75; // Cost 75 gold (in-game currency).
    private final int damage = 50; // Each hit does 50 damage to opponent character.
    private final int attackRange = 20; // Assumed 20 pixels range


    public Rider(int healthPoints, int position, boolean isAlive) {
        super(healthPoints, position, isAlive);
    }

    @Override
    public void attack(){

        int characterPos = getPosition();
        //int enemyCharacterPos = gameManager.enemyPosition; // Get enemyPos from gameManager or Controller.

        //int distance = Math.abs(characterPos - enemyCharacterPos);

        //while(distance <= attackRange){
            //controller.attackedOpponent(damage);
        //}
    }

    @Override
    public void takeDamage(){}

    @Override
    public void isAlive(){}

    @Override
    public int getKillReward(){
        return killedReward;
    }
}

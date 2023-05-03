package ageofus;

public class Archer extends Character {

    private final int killedReward = 15; // When an archer is killed the opponent gets 15 gold.
    private final int cost = 25; // Cost 25 gold (in-game currency).
    private final int damage = 30; // Each hit does 30 damage to opponent character.
    private final int attackRange = 50; // Assumed 50 pixels range

    public Archer(int healthPoints, int position, boolean isAlive) {
        super(healthPoints, position, isAlive);
    }

    @Override
    public void attack(){

    }

    @Override
    public void takeDamage(){

    }

    @Override
    public void isAlive(){
    }

    @Override
    public int getKillReward(){
       return killedReward;
    }
}

package Model;

public class Melee extends Character {

    private final int killedReward = 5; // When an archer is killed the opponent gets 5 gold.
    private final int cost = 15; // Cost 15 gold (in-game currency).
    private final int damage = 20; // Each hit does 20 damage to opponent character.
    private final int attackRange = 10; // Assumed 10 pixels range

    public Melee(int healthPoints, int position, boolean isAlive) {
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

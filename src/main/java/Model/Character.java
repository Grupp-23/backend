package Model;

public abstract class Character {

    private int healthPoints;
    private int position;
    private boolean isAlive;
    private int killedReward;

    public Character(int healthPoints, int position, boolean isAlive){

        this.healthPoints = healthPoints;
        this.position = position;
        this.isAlive = isAlive;
    }

    public void attack(){}

    public void takeDamage(){}

    public void isAlive(){
    }

    public int getKillReward(){
        return killedReward;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public int getPosition() {
        return position;
    }
}

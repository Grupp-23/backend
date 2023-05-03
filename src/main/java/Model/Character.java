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

    /**
     * Method for each character played to attack
     */
    public void attack(){}

    /**
     * Method for each character played to take damage
     */
    public void takeDamage(){}

    /**
     * Method to check if each character played is alive or not
     */
    public void isAlive(){
    }

    /**
     * Method to get reward for each character type killed
     */
    public int getKillReward(){
        return killedReward;
    }

    /**
     * Method to get each character played health
     */
    public int getHealthPoints() {
        return healthPoints;
    }

    /**
     * Method to get position of each played character. 
     */
    public int getPosition() {
        return position;
    }
}

package Model;

public abstract class Character {

    private int healthPoints; //Health of a character
    private double position; //Position of a character
    private boolean isAlive; //Status to check if a character is alive
    private int killedReward; //Amount of gold for killing a character

    private double speed; // The speed of a character
    private int cost;

    private int characterId;

    public Character(int characterId,int healthPoints, double position,double speed, boolean isAlive){

        this.characterId = characterId;
        this.healthPoints = healthPoints;
        this.position = position;
        this.speed = speed;
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
     *
     * @return
     */
    public boolean isAlive(){
        return false;
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
    public double getPosition() {
        return position;
    }

    public void updatePosition(double speed, int direction) {
        this.position = position+(speed*direction);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCharacterId() {
        return characterId;
    }

    public double getSpeed() {
        return speed;
    }
}

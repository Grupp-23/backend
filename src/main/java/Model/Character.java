package Model;

public abstract class Character {

    private int healthPoints; //Health of a character
    private double position; //Position of a character
    private boolean isAlive; //Status to check if a character is alive
    private int killedReward; //Amount of gold for killing a character

    private int damage;
    private double speed; // The speed of a character
    private int cost;

    private int characterId;

    private long lastAttackTime;
    private long attackSpeed;

    public int attackRange;

    private long spawnTime;

    private float size;

    public Character(int characterId,int healthPoints, double position,double speed, boolean isAlive, long attackSpeed, float size){

        this.characterId = characterId;
        this.healthPoints = healthPoints;
        this.position = position;
        this.speed = speed;
        this.isAlive = isAlive;
        this.attackSpeed = attackSpeed;
        this.spawnTime = spawnTime;
        this.size = size;
    }
    public boolean canAttack (long currentTime){
        return currentTime - lastAttackTime >= attackSpeed;
    }


    public long getSpawnTime() {
        return spawnTime;
    }

    /**
     * Method for each character played to attack
     */
    public void attack(long currentTime){
        lastAttackTime = currentTime;
    }

    /**
     * Method for each character played to take damage
     */
    public void takeDamage(int damage){
        healthPoints = healthPoints - damage;
    }

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

    public int getDamage(){
        return damage;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public float getSize() {
        return size;
    }

}

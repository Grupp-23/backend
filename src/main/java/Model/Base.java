package Model;

public class Base {
    private int baseHealthPoints = 1000;

    public void takeDamage(int damage){
        baseHealthPoints -= damage;
    }

    public boolean isDestroyed(){
        return baseHealthPoints <= 0;
    }
    public int getBaseHealthPoints(){
        return baseHealthPoints;
    }
}

package Model;

public class Base {
    private int baseHealthPoints = 1000;

    public void takeDamage(){
        baseHealthPoints -= 100; // Todo getter till dmg från character
    }

    public boolean isDestroyed(){
        return baseHealthPoints >= 0;
    }
}

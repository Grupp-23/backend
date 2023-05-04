package Model;

public class Player {

    private String name;
    private int gold;
    private Base base;
    private int id; // team id?

    public int getId(){
        return id;
    }

    /**
     * Method to buy a type of character/unit
     */
    public void buyUnit(){ // buyCharacter?

    }

    /**
     *  Gets the amount of gold from the specific player
     * @return the amount of gold the player has
     */
    public int getGold(){
        return gold;
    }

    public Base getBase(){
        return base;
    }

    public void reduceGold(int cost){
        this.gold -= cost;
    }
}

package Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {

    //Lägg in spelarna i en lista (väntar svar på oliver från hans kod)

    private boolean gameWinner;
    private HashMap<Integer, Character> team0Characters = new HashMap<>();
    private HashMap<Integer, Character> team1Characters = new HashMap<>();

    private int characterCounter = 1;
    private Player player0;
    private Player player1;

    /**
     * Starts the game when two clients are ready
     */
    public void startGame(){
        player0 = new Player(); // == team 0
        player1 = new Player(); // == team 1
    }

    /**
     * Ends game when client disconects or the base of one client/player is destoryed.
     */
    public void endGame(){
        gameWinner = true;
    }

    public JsonArray updateGameState(){

        JsonArray jsonArray = new JsonArray();



        for (int key:team0Characters.keySet()) {

            team0Characters.get(key).updatePosition(team0Characters.get(key).getSpeed(),1);
            JsonObject obj = new JsonObject();
            obj.addProperty("team", 0);
            obj.addProperty("id",key);
            obj.addProperty("pos",team0Characters.get(key).getPosition());
            jsonArray.add(obj);



        }
        for (int key:team1Characters.keySet()) {

            team1Characters.get(key).updatePosition(team1Characters.get(key).getSpeed(),(-1));
            JsonObject obj = new JsonObject();
            obj.addProperty("team", 1);
            obj.addProperty("id",key);
            obj.addProperty("pos",team1Characters.get(key).getPosition());
            jsonArray.add(obj);
        }

        return jsonArray;



    }

    /**
     * Gets position of character from gameThread
     */
    public void getPosition(){ // of what?

    }

    /**
     * Set the position for charachter
     */
    public void setCharacterPosition(){

    }

    /**
     * Gets health of base and if base is destoryed or not
     */
    public void getBaseStatus(){

    }

    public int[] spawnCharacter(int characterType, int team){ // CharacterType= the type of character played. Synchronized?
        characterCounter++;
        System.out.println("Spawning character");
        Character character = null;
        switch(characterType){
            case 1:
                System.out.println("Spawning Melee character");
                character = new Melee(100, (team*100),true,0.07);
                break;
            case 2:
                character = new Archer(75, (team*100), true,0.07);
                break;
            case 3:
                character = new Rider(300, (team*100), true,0.02);
                break;
        }
        System.out.println("Are you cont after the swtich?");


        if(team == 0){
            System.out.println("Team 0 has been selected");
            boolean checkPlayer0Gold = checkGold(player0, character);
            if (checkPlayer0Gold){
                System.out.println("Player-0 have this amount: "+player0.getGold());
                team0Characters.put(characterCounter,character);
                player0.reduceGold(character.getCost());
            }
        }
        if (team == 1){
            System.out.println("Team 1 has been selected");
            boolean checkPlayer1Gold = checkGold(player0, character);
            if (checkPlayer1Gold){
                System.out.println("Player-1 have this amount: "+player1.getGold());
                team1Characters.put(characterCounter, character);
                player1.reduceGold(character.getCost());
            }
        }




        return new int[]{characterCounter,1};
    }

    public void removeCharacter(){

    }

    /**
     * Checks so a player has sufficent amount of gold before buying a character
     * @return returns if player can buy character or not
     */
    public boolean checkGold(Player player, Character character){ // parameter for player and character
        return player.getGold() >= character.getCost();
    }

    /**
     * Sends gold for characters killed to the player that killed them.
     */
    public void getReward(){

    }
    
}

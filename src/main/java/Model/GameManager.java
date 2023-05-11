package Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameManager {

    private boolean gameWinner;
    private ArrayList<Character> team0Characters = new ArrayList<>();
    private ArrayList<Character> team1Characters = new ArrayList<>();
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


        for (int i = 0; i < team0Characters.size(); i++) {

            if (team0Characters.get(i).getPosition() >= 87){
                continue;
            }
            if(i >= 1){
                if(team0Characters.get(i).getPosition() >= team0Characters.get(i-1).getPosition()-3){
                    continue;
                }
            }
            if(team1Characters.size() > 0){
                if(team0Characters.get(i).getPosition() >= team1Characters.get(0).getPosition()-3){
                    continue;
                }
            }

            team0Characters.get(i).updatePosition(team0Characters.get(i).getSpeed(),1);
            JsonObject obj = new JsonObject();
            obj.addProperty("team", 0);
            obj.addProperty("id",team0Characters.get(i).getCharacterId());
            obj.addProperty("pos",team0Characters.get(i).getPosition());
            jsonArray.add(obj);


        }

        for (int i = 0; i < team1Characters.size(); i++) {
            if (team1Characters.get(i).getPosition() <= 10){
                continue;
            }
            if(i >= 1){
                if(team1Characters.get(i).getPosition() <= team1Characters.get(i-1).getPosition()+3){
                    continue;
                }
            }
            if(team0Characters.size() > 0){
                if(team1Characters.get(i).getPosition() <= team0Characters.get(0).getPosition()+3){
                    continue;
                }
            }

            team1Characters.get(i).updatePosition(team1Characters.get(i).getSpeed(),(-1));
            JsonObject obj = new JsonObject();
            obj.addProperty("team", 1);
            obj.addProperty("id",team1Characters.get(i).getCharacterId());
            obj.addProperty("pos",team1Characters.get(i).getPosition());
            jsonArray.add(obj);
        }

        return jsonArray;



    }

    /**
     * Gets position of character from gameThread
     */
    public void getPosition(){ // of what?

    }

    public void attackCharacter(Character characterAlly, Character characterEnemy){
        int allyDamage = characterAlly.getDamage();
        characterEnemy.takeDamage(allyDamage);
        System.out.println("Enemey character have: "+ characterEnemy.getHealthPoints()+ "hp");
    }

    /**
     * Set the position for character
     */
    public void setCharacterPosition(){

    }

    /**
     * Gets health of base and if base is destroyed or not
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
                character = new Melee(characterCounter, 100,((team*100)+(5+((-15)*team))),true,0.07);
                break;
            case 2:
                character = new Archer(characterCounter,75, ((team*100)+(5+((-15)*team))), true,0.07);
                break;
            case 3:
                character = new Rider(characterCounter,300, ((team*100)+(5+((-15)*team))), true,0.02);
                break;
        }
        System.out.println("Are you cont after the swtich?");


        if(team == 0){
            System.out.println("Team 0 has been selected");
            boolean checkPlayer0Gold = checkGold(player0, character);
            if (checkPlayer0Gold){
                System.out.println("Player-0 have this amount: "+player0.getGold());
                team0Characters.add(character);
                player0.reduceGold(character.getCost());
                System.out.println("Player-0 have bought a charachter for: "+ character.getCost());
            }
        }
        if (team == 1){
            System.out.println("Team 1 has been selected");
            boolean checkPlayer1Gold = checkGold(player0, character);
            if (checkPlayer1Gold){
                System.out.println("Player-1 have this amount: "+player1.getGold());
                team1Characters.add(character);
                player1.reduceGold(character.getCost());
                System.out.println("Player-1 have bought a charachter for: "+ character.getCost());
            }
        }




        return new int[]{characterCounter,1};
    }

    public void removeCharacter(int team, int characterId){
        if(team == 0){
            team0Characters.remove(characterId);

        }
        if(team == 1){
            team1Characters.remove(characterId);
        }
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
    public void getReward(Player player, Character character){
       player.increaseGold(character.getKillReward());

    }

    
}

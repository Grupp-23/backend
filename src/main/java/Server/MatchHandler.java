package Server;

import Model.*;
import Model.Character;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class MatchHandler extends Thread {

    private Client client0;
    private Client client1;

    //GameManager var ------------------
    private boolean gameWinner;
    private ArrayList<Character> team0Characters = new ArrayList<>();
    private ArrayList<Character> team1Characters = new ArrayList<>();
    private int characterCounter = 1;
    private Player player0;
    private Player player1;

    //--------------

    public MatchHandler(Client client0, Client client1){
        String json1 = "found";

        client0.sendJson(json1);
        client1.sendJson(json1);

        this.client0 = client0;
        this.client1 = client1;

        client0.setTeam(0);
        client1.setTeam(1);

        start();
    }

    /**
     * Start game creates two players
     */
    public void startGame(){
        player0 = new Player(); // == team 0
        player1 = new Player(); // == team 1
    }

    /**
     * End the game
     */
    public void endGame(){
        gameWinner = true;
    }

    public void spawnCharacter(Client client, int characterType){
        //GameManager --------------
        characterCounter++;
        int team = client.getTeam();
        System.out.println("Spawning character");
        Character character = null;
        switch(characterType){
            case 1:
                System.out.println("Spawning Melee character");
                character = new Melee(characterCounter, 100,((team*100)+(5+((-15)*team))),true,0.07);
                System.out.println(character.toString());
                break;
            case 2:
                character = new Archer(characterCounter,75, ((team*100)+(5+((-15)*team))), true,0.07);
                break;
            case 3:
                character = new Rider(characterCounter,300, ((team*100)+(5+((-15)*team))), true,0.02);
                break;
        }

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

        //------------------


        if (character != null){

            client0.sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+"}");
            client1.sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+"}");
        }
    }


    public void setCharacterPosition(){

        JsonObject obj = new JsonObject();
        obj.addProperty("method", "update");

        JsonArray array = updateGameState();
        obj.add("game", array);

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        //System.out.println(json);
        client0.sendJson(json);
        client1.sendJson(json);
    }





    public void run() {
        startGame();

        while (true){
            setCharacterPosition();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
    //GameManager----------------------

    /**
     * Checks so a player has sufficent amount of gold before buying a character
     * @return returns if player can buy character or not
     */
    public boolean checkGold(Player player, Character character){ // parameter for player and character
        return player.getGold() >= character.getCost();
    }

    public void removeCharacter(int team, int characterId){
        if(team == 0){
            team0Characters.remove(characterId);
        }
        if(team == 1){
            team1Characters.remove(characterId);
        }
    }

    public void attackCharacter(Character characterAlly, Character characterEnemy){
        int allyDamage = characterAlly.getDamage();
        characterEnemy.takeDamage(allyDamage);
        System.out.println("Enemey character have: "+ characterEnemy.getHealthPoints()+ "hp");
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

                    attackCharacter(team0Characters.get(i),team1Characters.get(0));

                    if(team1Characters.get(0).getHealthPoints() <= 0){
                        team1Characters.remove(0);

                    }

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
    //---------------------------------------------------
}

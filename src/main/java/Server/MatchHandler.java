package Server;

import Model.*;
import Model.Character;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class MatchHandler extends Thread {

    private Client client0;
    private Client client1;

    //GameManager var ------------------
    private boolean gameWinner;
    private ArrayList<Character> team0Characters = new ArrayList<>();
    private ArrayList<Character> team1Characters = new ArrayList<>();

    private LinkedList<Integer> team0SpawnQueue = new LinkedList<>();
    private LinkedList<Integer> team1SpawnQueue = new LinkedList<>();

    private int characterCounter = 1;
    private Player player0;
    private Player player1;
    private long lastSpawnTime;

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

    public boolean canSpawn (long currentTime, Character character){
        return currentTime - lastSpawnTime >= character.getSpawnTime();
    }
    public void spawn(long currentTime){
        lastSpawnTime = currentTime;
    }
    public void addToSpawnQueue(int team, int characterType){
        if (team == 0){
            team0SpawnQueue.add(characterType);
        }
        if (team == 1){
            team1SpawnQueue.add(characterType);
        }

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
                character = new Melee(characterCounter, 100,((team*100)+(5+((-15)*team))),true,0.07, 1000);
                break;
            case 2:
                character = new Archer(characterCounter,75, ((team*100)+(5+((-15)*team))), true,0.07, 1500);
                break;
            case 3:
                character = new Rider(characterCounter,300, ((team*100)+(5+((-15)*team))), true,0.02, 2000);
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
            client0.sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");
            client1.sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");
        }
    }


    public void setCharacterPosition(){
        JsonObject obj = new JsonObject();
        obj.addProperty("method", "update");

        JsonArray array = updateGameState();
        obj.add("game", array);

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        System.out.println("New pos: "+json);
        client1.sendJson(json);
        client0.sendJson(json);

    }
    public void removeCharacter(int team, int id){
        JsonObject object = new JsonObject();
        object.addProperty("method","characterdead");
        object.addProperty("team",team);
        object.addProperty("id", id);


        Gson gson = new Gson();
        String json = gson.toJson(object);
        System.out.println("Remove: "+json);
        client1.sendJson(json);
        client0.sendJson(json);

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

    public void removeCharacterFromlist(int team, int indexInList){
        if(team == 0){
            team0Characters.remove(indexInList);
        }
        if(team == 1){
            team1Characters.remove(indexInList);
        }
    }


    public void attackCharacter(Character characterAlly, Character characterEnemy){
        int allyDamage = characterAlly.getDamage();
        characterAlly.attack(System.currentTimeMillis());
        characterEnemy.takeDamage(allyDamage);
        System.out.println("Enemey character have: "+ characterEnemy.getHealthPoints()+ "hp");
    }
    public void attackBase(Character character, Base base){
        int characterDamage = character.getDamage();
        character.attack(System.currentTimeMillis());
        base.takeDamage(characterDamage);
        System.out.println("Enemy base have"+ base.getBaseHealthPoints()+"hp");
    }


    public JsonArray updateGameState(){

        JsonArray jsonArray = new JsonArray();


        for (int i = 0; i < team0Characters.size(); i++) {

            Character characterTeam0 = team0Characters.get(i); //Saves the character on position index from the ArrayList
            Long currentTime = System.currentTimeMillis(); //Saves the


            if (characterTeam0.getPosition() >= 87 && characterTeam0.canAttack(currentTime)){
                attackBase(characterTeam0, player1.getBase());
                continue;
            }


            if(i >= 1 && characterTeam0.getPosition() >= team0Characters.get(i-1).getPosition()-3){
                continue;
            }

            if(team1Characters.size() > 0){
                if(characterTeam0.getPosition() >= team1Characters.get(0).getPosition()-3){

                    if (characterTeam0.canAttack(currentTime)){
                        attackCharacter(characterTeam0,team1Characters.get(0));
                    }

                    if(team1Characters.get(0).getHealthPoints() <= 0){

                        player0.increaseGold(team1Characters.get(0).getKillReward());
                        System.out.println("Player-0 earned: "+ team1Characters.get(0).getKillReward());

                        removeCharacter(client1.getTeam(), team1Characters.get(0).getCharacterId());
                        removeCharacterFromlist(1,0);

                    }

                    continue;
                }
            }

            characterTeam0.updatePosition(characterTeam0.getSpeed(),1);
            JsonObject obj = new JsonObject();
            obj.addProperty("team", 0);
            obj.addProperty("id",characterTeam0.getCharacterId());
            obj.addProperty("pos",characterTeam0.getPosition());
            jsonArray.add(obj);




        }

        for (int i = 0; i < team1Characters.size(); i++) {

            Character characterTeam1 = team1Characters.get(i); //Saves the character on position index from the ArrayList
            long currentTime = System.currentTimeMillis();


            if (characterTeam1.getPosition() <= 10 && characterTeam1.canAttack(currentTime)){
                attackBase(characterTeam1, player0.getBase());
                continue;
            }

            if(i >= 1 && characterTeam1.getPosition() <= team1Characters.get(i-1).getPosition()+3) {
                continue;
            }

            if(team0Characters.size() > 0){
                if(characterTeam1.getPosition() <= team0Characters.get(0).getPosition()+3){

                    if (characterTeam1.canAttack(currentTime)){
                        attackCharacter(characterTeam1,team0Characters.get(0));
                    }

                    if(team0Characters.get(0).getHealthPoints() <= 0){

                        player1.increaseGold(team0Characters.get(0).getKillReward());
                        System.out.println("Player-0 earned: "+ team0Characters.get(0).getKillReward());

                        removeCharacter(client0.getTeam(), team0Characters.get(0).getCharacterId());
                        removeCharacterFromlist(0,0);

                    }

                    continue;
                }
            }

            characterTeam1.updatePosition(characterTeam1.getSpeed(),(-1));
            JsonObject obj = new JsonObject();
            obj.addProperty("team", 1);
            obj.addProperty("id",characterTeam1.getCharacterId());
            obj.addProperty("pos",characterTeam1.getPosition());
            jsonArray.add(obj);
        }

        return jsonArray;



    }
    //---------------------------------------------------
}

package Server;

import Model.*;
import Model.Character;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MatchHandler extends Thread {

    private Client[] clients;
    private ArrayList<Character>[] teamCharacters;

    private int characterCounter = 0;

    public MatchHandler(Client client0, Client client1) {
        initCharacterLists();
        initClients(client0, client1);

        String json1 = "found";

        clients[0].sendJson(json1);
        clients[1].sendJson(json1);

        start();
    }

    public void initClients(Client client0, Client client1) {
        clients = new Client[2];
        clients[0] = client0;
        clients[1] = client1;

        clients[0].setTeam(0);
        clients[1].setTeam(1);
    }

    public void initCharacterLists() {
        teamCharacters = new ArrayList[2];

        teamCharacters[0] = new ArrayList<>();
        teamCharacters[1] = new ArrayList<>();
    }

    public void spawnCharacter(Client client, int characterType) {
        characterCounter++;

        int team = client.getTeam();

        Character character = null;

        switch(characterType) {
            case 1:
                character = new Melee(characterCounter, 100,((team*100)+(5+((-15)*team))),true,0.07, 1000, 3.3f);
                break;
            case 2:
                character = new Archer(characterCounter,75, ((team*100)+(5+((-15)*team))), true,0.07, 1500, 3.3f);
                break;
            case 3:
                character = new Rider(characterCounter,300, ((team*100)+(5+((-15)*team))), true,0.04, 2000, 9.5f);
                break;
            default:
                return;
        }

        teamCharacters[team].add(character);

        clients[0].sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");
        clients[1].sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");
    }

    public void updateCharacterPosition(Character character, int team) {

        int temp = (int)(((team-0.5f)*2)*-1);

        character.updatePosition(character.getSpeed(),temp);

        JsonObject obj = new JsonObject();
        obj.addProperty("method", "move");
        obj.addProperty("team", team);
        obj.addProperty("id",character.getCharacterId());
        obj.addProperty("pos",character.getPosition());

        Gson gson = new Gson();
        String json = gson.toJson(obj);

        clients[0].sendJson(json);
        clients[1].sendJson(json);
    }

    public boolean baseCollision(int team, Character character) {

        float baseColl = character.getSize()/2 + character.getAttackRange();

        if (team == 0) {
            return (character.getPosition() >= 89-baseColl);
        }
        else {
            return (character.getPosition() <= 8+baseColl);
        }
    }

    public boolean allyCollision(int team, int index, Character character) {

        if (index < 1) {
            return false;
        }

        Character tempCharacter = teamCharacters[team].get(index - 1);
        double collisionRange = (tempCharacter.getSize() / 2) + (character.getSize() / 2);

        if (team == 0) {
            return (character.getPosition() >= tempCharacter.getPosition() - collisionRange);
        }
        else {
            return (character.getPosition() <= tempCharacter.getPosition() + collisionRange);
        }
    }

    public boolean enemyCollision(int team, Character character, int enemyId) {

        if (teamCharacters[enemyId].size() == 0) {
            return false;
        }

        Character tempCharacter = teamCharacters[enemyId].get(0);
        double collisionRange = (tempCharacter.getSize()/2)+(character.getSize()/2);

        if (team == 0) {
            return (character.getPosition() >= tempCharacter.getPosition()-(collisionRange+character.getAttackRange()));
        }
        else {
            return (character.getPosition() <= tempCharacter.getPosition()+(collisionRange+character.getAttackRange()));
        }
    }

    public void attackBase(Character character, Base base, long time){
        character.attack(time);
        base.takeDamage(character.getDamage());
    }

    public void attackCharacter(Character allyCharacter, Character enemyCharacter, long time, int enemyId) {
        allyCharacter.attack(time);
        enemyCharacter.takeDamage(allyCharacter.getDamage());

        if (enemyCharacter.getHealthPoints() <= 0) {
            removeCharacter(enemyCharacter, enemyId);
        }
    }

    public void removeCharacter(Character character, int enemyId) {
        teamCharacters[enemyId].remove(character);

        JsonObject object = new JsonObject();
        object.addProperty("method","characterdead");
        object.addProperty("team",enemyId);
        object.addProperty("id", character.getCharacterId());

        Gson gson = new Gson();
        String json = gson.toJson(object);
        clients[0].sendJson(json);
        clients[1].sendJson(json);
    }

    public void update() {

        long currentTime = System.currentTimeMillis();

        for (int currentTeam = 0; currentTeam < clients.length; currentTeam++) {
            int enemyId = (currentTeam -1)*(-1);

            for (int i = 0; i < teamCharacters[currentTeam].size(); i++) {
                Character currentCharacter = teamCharacters[currentTeam].get(i);

                // Attack enemy character
                if (enemyCollision(currentTeam, currentCharacter, enemyId)) {
                    if (currentCharacter.canAttack(currentTime)) {
                        attackCharacter(currentCharacter, teamCharacters[enemyId].get(0), currentTime, enemyId);
                    }
                }

                // Attack enemy base
                else if(baseCollision(currentTeam, currentCharacter)) {
                    if (currentCharacter.canAttack(currentTime)) {
                        attackBase(currentCharacter, clients[enemyId].getBase(), currentTime);
                    }
                }

                // If no allied collision: Update position
                else if(!allyCollision(currentTeam, i, currentCharacter)){
                    updateCharacterPosition(currentCharacter, currentTeam);
                }

            }
        }
    }

    @Override
    public void run() {
        while (true) {
            update();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

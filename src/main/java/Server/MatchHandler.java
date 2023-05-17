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

    private final int startGold = 200;
    private final int passiveIncomeAmount = 5;
    private final int passiveIncomeInterval = 2000; // milliseconds

    private Client[] clients;
    private ArrayList<Character>[] teamCharacters;

    private ArrayList<Projectile>[] projectiles;

    private int characterCounter = 0;
    private int projectileCounter = 0;
    private long lastIncomeTick = 0; // Last time the players got passive income

    public MatchHandler(Client client0, Client client1) {
        initCharacterLists();
        initProjectileLists();
        initClients(client0, client1);

        String json1 = "found";

        clients[0].sendJson(json1);
        clients[1].sendJson(json1);

        clients[0].increaseGold(startGold);
        clients[1].increaseGold(startGold);

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

    public void initProjectileLists() {
        projectiles = new ArrayList[2];

        projectiles[0] = new ArrayList<>();
        projectiles[1] = new ArrayList<>();
    }

    public Client[] getClients() {
        return clients;
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

        if (!hasGold(client, character.getCost())) {
            return;
        }

        teamCharacters[team].add(character);

        clients[0].sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");
        clients[1].sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");

        client.reduceGold(character.getCost());
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

    public boolean projectileCollision(Projectile projectile, int victimId) {
        if (victimId == 1) {
            return (projectile.getX() >= teamCharacters[victimId].get(0).getPosition());
        }
        else {
            return (projectile.getX() <= teamCharacters[victimId].get(0).getPosition());
        }
    }

    public boolean hasGold(Client client, int cost) {
        return client.getGold() >= cost;
    }

    public void attackBase(Character character, Base base, int victimId, int attackerId, long time){
        character.attack(time);

        if (character.getAttackRange() >= 0.5f) {
            int temp = (int)(((victimId-0.5f)*2));
            spawnProjectile(character.getPosition(), 8, character.getDamage(), 0.21f, Math.acos(temp), attackerId, 3);
        }
        else {
            base.takeDamage(character.getDamage());
        }

        if (base.isDestroyed()) {
            announceWinner(attackerId, victimId);
        }
    }

    public void attackCharacter(Character allyCharacter, Character enemyCharacter, long time, int victimId, int attackerId) {
        allyCharacter.attack(time);

        if (allyCharacter.getAttackRange() >= 0.5f) {
            int temp = (int)(((victimId-0.5f)*2));
            spawnProjectile(allyCharacter.getPosition(), 8, allyCharacter.getDamage(), 0.21f, Math.acos(temp), attackerId, Math.abs(allyCharacter.getPosition() - enemyCharacter.getPosition()));
        }
        else {
            enemyCharacter.takeDamage(allyCharacter.getDamage());
        }

        if (enemyCharacter.getHealthPoints() <= 0) {
            removeCharacter(enemyCharacter, victimId, attackerId);
        }
    }

    public void projectileAttack(int projectileId, int victimId, int attackerId) {
        Character victim = teamCharacters[victimId].get(0);

        victim.takeDamage(projectiles[attackerId].get(projectileId).getDamage());
        projectiles[attackerId].remove(projectileId);

        if (victim.getHealthPoints() <= 0) {
            removeCharacter(victim, victimId, attackerId);
        }
    }

    public void spawnProjectile(double x, double y, int damage, float speed, double direction, int team, double distance) {
        projectileCounter++;

        projectiles[team].add(new Projectile(x, y, damage, direction, speed, projectileCounter));

        JsonObject obj = new JsonObject();
        obj.addProperty("method", "projectile");
        obj.addProperty("team", team);
        obj.addProperty("id", projectileCounter);
        obj.addProperty("direction", direction);
        obj.addProperty("speed", speed);
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        obj.addProperty("distance", distance);

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        clients[0].sendJson(json);
        clients[1].sendJson(json);
    }

    public void removeCharacter(Character character, int victimId, int killerId) {
        teamCharacters[victimId].remove(character);

        JsonObject object = new JsonObject();
        object.addProperty("method","characterdead");
        object.addProperty("team",victimId);
        object.addProperty("id", character.getCharacterId());

        Gson gson = new Gson();
        String json = gson.toJson(object);
        clients[0].sendJson(json);
        clients[1].sendJson(json);

        clients[killerId].increaseGold(character.getKillReward());
    }

    public void announceWinner(int winnerId, int loserId) {
        JsonObject object = new JsonObject();
        object.addProperty("method", "win");
        object.addProperty("status", "Victory");

        Gson gson = new Gson();
        String json = gson.toJson(object);

        System.out.println(json);

        clients[winnerId].sendJson(json);

        object.addProperty("status", "Defeat");

        json = gson.toJson(object);
        System.out.println(json);

        clients[loserId].sendJson(json);
    }

    public void updatePassiveIncome(long time) {
        lastIncomeTick = time;
        clients[0].increaseGold(passiveIncomeAmount);
        clients[1].increaseGold(passiveIncomeAmount);
    }

    public void update() {

        long currentTime = System.currentTimeMillis();

        if (currentTime >= lastIncomeTick+passiveIncomeInterval) {
            updatePassiveIncome(currentTime);
        }

        for (int currentTeam = 0; currentTeam < clients.length; currentTeam++) {
            int enemyId = (currentTeam -1)*(-1);

            for (int i = teamCharacters[currentTeam].size() - 1; i >= 0; i--) {
                Character currentCharacter = teamCharacters[currentTeam].get(i);

                // Attack enemy character
                if (enemyCollision(currentTeam, currentCharacter, enemyId)) {
                    if (currentCharacter.canAttack(currentTime)) {
                        attackCharacter(currentCharacter, teamCharacters[enemyId].get(0), currentTime, enemyId, currentTeam);
                    }
                }

                // Attack enemy base
                else if(baseCollision(currentTeam, currentCharacter)) {
                    if (currentCharacter.canAttack(currentTime)) {
                        attackBase(currentCharacter, clients[enemyId].getBase(), enemyId, i, currentTime);
                    }
                }

                // If no allied collision: Update position
                else if(!allyCollision(currentTeam, i, currentCharacter)){
                    updateCharacterPosition(currentCharacter, currentTeam);
                }
            }

            for (int i = projectiles[currentTeam].size() - 1; i >= 0; i--) {
                Projectile tempProjectile = projectiles[currentTeam].get(i);

                if (projectileCollision(tempProjectile, enemyId)) {
                    projectileAttack(i, enemyId, currentTeam);
                }

                else {
                    tempProjectile.updatePosition();
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

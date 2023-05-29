package Server;

import Model.*;
import Model.Character;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Represents a single match between 2 clients
 */
public class MatchHandler extends Thread {

    private final int startGold = 15;
    private final int passiveIncomeAmount = 5;
    private final int passiveIncomeInterval = 5000; // milliseconds

    private Client[] clients;
    private ArrayList<Character>[] teamCharacters;
    private LinkedList<Integer>[] queueCharacters; //Queue for characters to be spawned
    private ArrayList<Projectile>[] projectiles;

    private int characterCounter = 0;
    private int projectileCounter = 0;
    private long lastIncomeTick = 0; // Last time the players got passive income
    private volatile boolean stopThread = false; // Boolean to gracefully stop match-thread //todo ta kanske bort volitile
    private long[] lastSpawnedTime = new long[2];

    public MatchHandler(Client client0, Client client1) {
        initCharacterLists();
        initProjectileLists();
        initClients(client0, client1);
        initCharactersQueue();

        String json1 = "found";

        clients[0].sendJson(json1);
        clients[1].sendJson(json1);

        clients[0].increaseGold(startGold);
        clients[1].increaseGold(startGold);

        start();
    }

    /**
     * Adds the clients to clients list and assigns them a team number
     * @param client0
     * @param client1
     */
    public void initClients(Client client0, Client client1) {
        clients = new Client[2];
        clients[0] = client0;
        clients[1] = client1;

        clients[0].setTeam(0);
        clients[1].setTeam(1);
    }

    /**
     * Initializes the list of characters for each team
     */
    public void initCharacterLists() {
        teamCharacters = new ArrayList[2];

        teamCharacters[0] = new ArrayList<>();
        teamCharacters[1] = new ArrayList<>();
    }

    /**
     * Initializes the queue for character spawning for each team
     */
    public void initCharactersQueue(){
        queueCharacters = new LinkedList[2];
        queueCharacters[0] = new LinkedList<>();
        queueCharacters[1] = new LinkedList<>();
    }

    /**
     * Initializes the list for projectiles for each team
     */
    public void initProjectileLists() {
        projectiles = new ArrayList[2];

        projectiles[0] = new ArrayList<>();
        projectiles[1] = new ArrayList<>();
    }

    /**
     * Adds a character to the spawning queue for selected team
     * @param characterType the character type to spawn
     * @param client the client which should spawn the character
     */
    public void addCharacterToQueue(int characterType, Client client){
        queueCharacters[client.getTeam()].add(characterType);
        System.out.println("Character have been added to queue");
    }

    /**
     * Gets and removes the first character in the queue
     * @param team
     * @return the character type that is first in the queue
     */
    public int getCharacterFromQueue(int team){
        int characterCurrent = queueCharacters[team].getFirst();
        queueCharacters[team].removeFirst();
        System.out.println("Character: "+ characterCurrent + "has been return to team " + team + " Character list");
        return characterCurrent;
    }

    /**
     * Gets both clients as an array of length 2
     * @return the relevant clients for this match
     */
    public Client[] getClients() {
        return clients;
    }

    /**
     * Spawns a character for the selected client and saves the time when this character was spawned
     * @param client client that should spawn the character
     * @param characterType which type of character should be spawned
     * @param time the current time
     */
    public void spawnCharacter(Client client, int characterType, long time) {
        characterCounter++;

        int team = client.getTeam();
        lastSpawnedTime[team] = time;
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

        if (!hasGold(client, character.getCost())) { //Check if player have gold
            return;
        }

        teamCharacters[team].add(character);

        clients[0].sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");
        clients[1].sendJson("{ \"method\": \"spawn\",\"type\":"+characterType+",\"team\":"+team+",\"id\":"+characterCounter+",\"pos\": "+character.getPosition()+"}");

        client.reduceGold(character.getCost());
    }

    /**
     * Updates the position of a character
     * @param character the character which should have its position updated
     * @param team which team the character is part of
     */
    public void updateCharacterPosition(Character character, int team) {

        int direction = team == 1 ? -1 : 1;

        character.updatePosition(character.getSpeed(),direction);

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

    /**
     * Checks if a base is inside a character's attack range
     * @param team the opponent team to the character
     * @param character which characted is checked for collision
     * @return if the base is in attack range of character
     */
    public boolean baseCollision(int team, Character character) {

        float baseColl = character.getSize()/2 + character.getAttackRange();

        if (team == 0) {
            return (character.getPosition() >= 89-baseColl);
        }
        else {
            return (character.getPosition() <= 8+baseColl);
        }
    }

    /**
     * Checks if a character is colliding with an allied character
     * @param team the team of the relevant character
     * @param index the index of the relevant character
     * @param character the relevant character
     * @return if the relevant character is colliding with an allied character
     */
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

    /**
     * Checks if an enemy character is inside the attack range of a character
     * @param team the team of the relevant character
     * @param character the relevant character
     * @param enemyId the enemy team of the relevant character
     * @return if an enemy character is inside attack range of the relevant character
     */
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

    /**
     * Checks if a projectile is colliding with an enemy character
     * @param projectile the relevant projectile
     * @param victimId the enemy team id of the relevant projectile
     * @return if the projectile has hit an enemy character
     */
    public boolean projectileCharacterCollision(Projectile projectile, int victimId) {
        if (victimId == 1) {
            return (projectile.getX() >= teamCharacters[victimId].get(0).getPosition());
        }
        else {
            return (projectile.getX() <= teamCharacters[victimId]. get(0).getPosition());
        }
    }

    /**
     * Checks if a client has enough gold
     * @param client the relevant client
     * @param cost the cost of what the relevant client is trying to purchase
     * @return if the client has enough gold
     */
    public boolean hasGold(Client client, int cost) {
        return client.getGold() >= cost;
    }

    /**
     * Attack a base
     * @param character the character that is attacking
     * @param base the base that is being attacked
     * @param victimId the team id of the victim
     * @param attackerId the team id of the attacker
     * @param time the time of when the attacker is attacking
     */
    public void attackBase(Character character, Base base, int victimId, int attackerId, long time){
        character.attack(time);

        if (character.getAttackRange() >= 0.5f) {
            int direction = victimId == 1 ? 1 : -1;
            spawnProjectile(character.getPosition(), 8, character.getDamage(), 0.21f, Math.acos(direction), attackerId);
        }
        else {
            base.takeDamage(character.getDamage());
        }

        if (base.isDestroyed()) {
            announceMatchEnd(attackerId, victimId);
        }
    }

    /**
     * Attack a character
     * @param allyCharacter the character that is attacking
     * @param enemyCharacter the character that is being attacked
     * @param time the time of when the attacker is attacking
     * @param victimId the team id of the victim
     * @param attackerId the team id of the attacker
     */
    public void attackCharacter(Character allyCharacter, Character enemyCharacter, long time, int victimId, int attackerId) {
        allyCharacter.attack(time);

        if (allyCharacter.getAttackRange() >= 0.5f) {
            int direction = victimId == 1 ? 1 : -1;
            spawnProjectile(allyCharacter.getPosition(), 8, allyCharacter.getDamage(), 0.21f, Math.acos(direction), attackerId);
        }
        else {
            enemyCharacter.takeDamage(allyCharacter.getDamage());
        }

        if (enemyCharacter.getHealthPoints() <= 0) {
            removeCharacter(enemyCharacter, victimId, attackerId);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", "characterdmg");

        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);

        clients[0].sendJson(json);
        clients[1].sendJson(json);


    }

    /**
     * Projectile attack character
     * @param projectileId the id of the projectile which is attacking
     * @param victimId the team id of the victim
     * @param attackerId the team id of the attacker
     */
    public void projectileCharacterAttack(int projectileId, int victimId, int attackerId) {
        Character victim = teamCharacters[victimId].get(0);

        victim.takeDamage(projectiles[attackerId].get(projectileId).getDamage());

        if (victim.getHealthPoints() <= 0) {
            removeCharacter(victim, victimId, attackerId);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", "projectiledmg");
        jsonObject.addProperty("id", projectiles[attackerId].get(projectileId).getId());

        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);

        clients[0].sendJson(json);
        clients[1].sendJson(json);

        projectiles[attackerId].remove(projectileId);

    }

    /**
     * Spawns a projectile
     * @param x the x position of the projectile
     * @param y the y position of the projectile
     * @param damage the amount of damage that the projectile should do
     * @param speed the speed of the projectile
     * @param direction the direction of the projectiles in radians
     * @param team the id of the team that should spawn a projectile
     */
    public void spawnProjectile(double x, double y, int damage, float speed, double direction, int team) {
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

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        clients[0].sendJson(json);
        clients[1].sendJson(json);
    }

    /**
     * Remove a character
     * @param character the relevant character
     * @param victimId the team id of the relevant character
     * @param killerId the team id of the relevant character's killer
     */
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

    /**
     * Announce a winner and stops the match update
     * @param team the relevant team id
     * @param status whether "Victory" or "Defeat"
     */
    public void announceWinner(int team, String status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", "win");
        jsonObject.addProperty("status", status);

        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);

        clients[team].sendJson(json);
    }

    /**
     * Stop the match and announce winners
     * @param winnerId the team id of the winner
     * @param loserId the team id of the loser
     */
    public void announceMatchEnd(int winnerId, int loserId) {
        System.out.println(winnerId + " " + loserId);
        announceWinner(winnerId, "Victory");
        announceWinner(loserId, "Defeat");

        stopThread = true;
        stopWithInterrupt();
    }

    /**
     * Give passive income to both clients
     * @param time the time of when the income is added
     */
    public void updatePassiveIncome(long time) {
        lastIncomeTick = time;
        clients[0].increaseGold(passiveIncomeAmount);
        clients[1].increaseGold(passiveIncomeAmount);
    }

    /**
     * The update method which updates the whole match (is called every 10 ms)
     */
    public void update() {

        long currentTime = System.currentTimeMillis();

        if (currentTime >= lastIncomeTick+passiveIncomeInterval) {
            updatePassiveIncome(currentTime);
        }

        for (int currentTeam = 0; currentTeam < clients.length; currentTeam++) {
            int enemyId = currentTeam == 1 ? 0 : 1;

            if ((queueCharacters[currentTeam].size() > 0)){ //If queue has one character and the time have been 2000 ms after last spawned

                if(currentTime-lastSpawnedTime[currentTeam] >= 2000){
                    Client client = clients[currentTeam];
                    int characterType = getCharacterFromQueue(currentTeam);
                    spawnCharacter(client,characterType,currentTime);
                }
            }

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
                        attackBase(currentCharacter, clients[enemyId].getBase(), enemyId, currentTeam, currentTime);
                    }
                }

                // If no allied collision: Update position
                else if(!allyCollision(currentTeam, i, currentCharacter)){
                    updateCharacterPosition(currentCharacter, currentTeam);
                }
            }

            for (int i = projectiles[currentTeam].size() - 1; i >= 0; i--) {
                Projectile tempProjectile = projectiles[currentTeam].get(i);

                if (projectileCharacterCollision(tempProjectile, enemyId)) {
                    projectileCharacterAttack(i, enemyId, currentTeam);
                }

                else {
                    tempProjectile.updatePosition();
                }
            }
        }
    }

    @Override
    public void run() {
        while (!stopThread && !Thread.currentThread().isInterrupted()) {
            update();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
               // Thread.currentThread().interrupt();

            }
        }

    }

    /**
     * Interrupts the thread
     */
    public void stopWithInterrupt() {
        interrupt(); // Interrupt the thread to wake it up from blocking or waiting
    }

    /**
     * Stops the thread
     * @param stopThread should the thread be stopped
     */
    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }
}

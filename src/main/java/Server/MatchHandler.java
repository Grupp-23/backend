package Server;

import Model.GameManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MatchHandler extends Thread {

    private GameManager gameManager; //Instance of GameManager
    private Client client0;
    private Client client1;

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

    public void spawnCharacter(Client client, int character){
        int[] spawnChar = gameManager.spawnCharacter(character, client.getTeam());
        if (spawnChar[1] == 1){

            client0.sendJson("{ \"method\": \"spawn\",\"type\":"+character+",\"team\":"+client.getTeam()+",\"id\":"+spawnChar[0]+"}");
            client1.sendJson("{ \"method\": \"spawn\",\"type\":"+character+",\"team\":"+client.getTeam()+",\"id\":"+spawnChar[0]+"}");
        }
    }
    public void setCharacterPosition(){

        JsonObject obj = new JsonObject();
        obj.addProperty("method", "update");

        JsonArray array = gameManager.updateGameState();
        obj.add("game", array);

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        //System.out.println(json);
        client0.sendJson(json);
        client1.sendJson(json);
    }





    public void run() {
        gameManager = new GameManager();
        gameManager.startGame();

        while (true){
            setCharacterPosition();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

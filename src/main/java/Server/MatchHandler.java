package Server;

import Model.GameManager;
import com.google.gson.Gson;

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
        boolean spawnChar = gameManager.spawnCharacter(character, client.getTeam());
        if (spawnChar){
            client0.sendJson("{ \"method\": \"spawn\",\"type\":"+character+",\"team\":"+client.getTeam()+",\"id\":1}");
            client1.sendJson("{ \"method\": \"spawn\",\"type\":"+character+",\"team\":"+client.getTeam()+",\"id\":1}");
        }
    }
    public void setCharacterPosition(){
        gameManager.setCharacterPosition();

        Gson gson = new Gson();
        gson.

    }



    public void run() {
        gameManager = new GameManager();
        gameManager.startGame();

        while (true){
            setCharacterPosition();
        }

    }
}
